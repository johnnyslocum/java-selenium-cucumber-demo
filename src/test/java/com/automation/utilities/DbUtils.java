package com.automation.utilities;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.*;
import java.time.Duration;
import java.util.*;

/**
 * Generic DB utility that uses HikariCP (if present) for pooling.
 * Reads configuration from classpath: db.properties (see example).
 */
public final class DbUtils {

    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);

    private static final String DEFAULT_PROPERTIES = "db.properties";
    private static volatile HikariDataSource dataSource;
    private static volatile Properties props;

    private DbUtils() { /* no instantiation */ }

    static {
        loadPropertiesAndInit();
    }

    private static synchronized void loadPropertiesAndInit() {
        if (props != null) {
            return; // already initialized
        }
        props = new Properties();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_PROPERTIES)) {
            if (is != null) {
                props.load(is);
                logger.info("[DbUtils] Loaded DB properties from classpath: {}", DEFAULT_PROPERTIES);
            } else {
                logger.warn("[DbUtils] No {} on classpath - expecting system properties or explicit config", DEFAULT_PROPERTIES);
            }
        } catch (Exception e) {
            logger.warn("[DbUtils] Error loading {}: {}", DEFAULT_PROPERTIES, e.getMessage(), e);
        }

        // Allow override via system properties (typical in CI)
        // Keys: db.url db.user db.password db.driver db.poolSize db.connectionTimeoutMs
        String url = get("db.url", null);
        String user = get("db.user", null);
        String pass = get("db.password", null);

        // Try to set up Hikari if URL provided
        if (url != null) {
            try {
                HikariConfig cfg = new HikariConfig();
                cfg.setJdbcUrl(url);
                if (user != null) cfg.setUsername(user);
                if (pass != null) cfg.setPassword(pass);

                String driver = get("db.driver", null);
                if (driver != null) cfg.setDriverClassName(driver);

                int poolSize = Integer.parseInt(get("db.poolSize", "5"));
                cfg.setMaximumPoolSize(poolSize);

                long connTimeout = Long.parseLong(get("db.connectionTimeoutMs", "30000"));
                cfg.setConnectionTimeout(connTimeout);

                // Optional validation query
                String validationQuery = get("db.validationQuery", null);
                if (validationQuery != null && !validationQuery.isBlank()) {
                    cfg.setConnectionTestQuery(validationQuery);
                }

                // Optional: set initializationFailTimeout so startup fails fast if DB unreachable
                cfg.setInitializationFailTimeout(Long.parseLong(get("db.initFailTimeoutMs", "10000")));

                dataSource = new HikariDataSource(cfg);
                logger.info("[DbUtils] HikariCP DataSource created (poolSize={})", poolSize);
            } catch (Throwable t) {
                logger.warn("[DbUtils] HikariCP initialization failed, falling back to DriverManager: {}", t.getMessage());
                dataSource = null;
            }
        } else {
            logger.info("[DbUtils] No db.url specified - DbUtils will not create a pool. Use System properties or db.properties to configure.");
        }
    }

    private static String get(String key, String def) {
        String sys = System.getProperty(key);
        if (sys != null) return sys;
        String env = System.getenv(key.replace('.', '_').toUpperCase()); // support env var override: DB_URL, DB_USER...
        if (env != null) return env;
        String prop = props.getProperty(key);
        return prop != null ? prop : def;
    }

    /**
     * Obtain a connection. Caller must close it (try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        // Fallback to DriverManager (requires db.driver to be present or driver auto-loaded)
        String url = get("db.url", null);
        if (url == null) {
            throw new SQLException("No data source configured (db.url missing).");
        }
        String user = get("db.user", null);
        String pass = get("db.password", null);
        String driver = get("db.driver", null);
        if (driver != null && !driver.isBlank()) {
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                logger.warn("[DbUtils] Driver class not found: {}", driver);
            }
        }
        if (user != null) {
            return java.sql.DriverManager.getConnection(url, user, pass);
        } else {
            return java.sql.DriverManager.getConnection(url);
        }
    }

    /**
     * Execute a SELECT query and return results as list of maps (column -> value).
     * Use try-with-resources for connection via this helper (method handles closing).
     */
    public static List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = prepareStatement(c, sql, params);
             ResultSet rs = ps.executeQuery()) {

            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= cols; i++) {
                    row.put(md.getColumnLabel(i), rs.getObject(i));
                }
                rows.add(row);
            }
            return rows;
        }
    }

    /**
     * Execute INSERT/UPDATE/DELETE. Returns affected row count.
     */
    public static int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = prepareStatement(c, sql, params)) {
            return ps.executeUpdate();
        }
    }

    /**
     * Execute scalar query (first column of first row) or return null.
     */
    public static Object queryForObject(String sql, Object... params) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = prepareStatement(c, sql, params);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
        }
    }

    private static PreparedStatement prepareStatement(Connection c, String sql, Object... params) throws SQLException {
        PreparedStatement ps = c.prepareStatement(sql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
        return ps;
    }

    /**
     * Clean shutdown - close Hikari pool if present.
     * Call this from test suite teardown (Hooks @AfterAll or equivalent).
     */
    public static synchronized void shutdown() {
        if (dataSource != null) {
            try {
                logger.info("[DbUtils] Closing HikariCP datasource...");
                dataSource.close();
            } catch (Throwable t) {
                logger.warn("[DbUtils] Error closing datasource: {}", t.getMessage(), t);
            } finally {
                dataSource = null;
            }
        }
    }
}