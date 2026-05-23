package com.automation.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DriverManager {

    // make webdriver thread safe for parallel execution
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<ChromeDriverService> service = new ThreadLocal<>();
    private static final AtomicInteger LOG_COUNTER = new AtomicInteger(0);
    private static volatile boolean logsCleanedUp = false; // Run cleanup once per JVM

    // Public accessor
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            driver.set(createDriver());
        }
        return driver.get();
    }

    // Create a browser instance (synchronized to avoid races)
    private static WebDriver createDriver() {
        // If already initialized by another thread, return it
        if (driver.get() != null && isDriverAlive(driver.get())) {
            return driver.get();
        }

        System.out.println("[DriverManager] Setting up WebDriverManager...");
        WebDriverManager.chromedriver().setup();

        // ChromeDriverService with log file for each thread
        String baseDir = System.getProperty("user.dir");
        File logDir = new File(baseDir, "target");

        // synchronized to prevent concurrent thread logs from overwriting each other
        synchronized (DriverManager.class) {
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            if (!logsCleanedUp) {
                cleanOldChromeDriverLogs(logDir);
                logsCleanedUp = true;
            }
        }

        String logFileName = "chromedriver-" + Thread.currentThread().getId() + "-" + LOG_COUNTER.incrementAndGet() + ".log";
        File logFile = new File(logDir, logFileName);

        ChromeDriverService chromeService = new ChromeDriverService.Builder()
                .usingAnyFreePort()
                .withLogFile(logFile)
                .build();

        // Enable verbose logging for chromedriver (helps debugging)
        System.setProperty("webdriver.chrome.verboseLogging", "true");

        System.out.println("[DriverManager] Creating ChromeOptions...");
        ChromeOptions options = new ChromeOptions();

        // Common stable options
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.setAcceptInsecureCerts(true);

        // Reduce GPU related crashes (useful on headless or some Windows GPUs)
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-software-rasterizer");

//        // Headless handling (use -Dheadless=true to enable)
//        if (System.getProperty("headless", "false").equalsIgnoreCase("true")) {
//            options.addArguments("--headless=new");
//            options.addArguments("--window-size=1920,1080");
//        } else {
//            // prefer normal interactive runs
//            // Avoid adding --no-sandbox and --disable-dev-shm-usage on Windows by default
//            // If running in Linux container you may enable them via -DuseNoSandbox=true
//            if (System.getProperty("useNoSandbox", "false").equalsIgnoreCase("true")) {
//                options.addArguments("--no-sandbox");
//                options.addArguments("--disable-dev-shm-usage");
//            }
//        }

        // Headless handling: Checks System properties, Environment variables, AND a fallback default config
        boolean isHeadlessRequested = System.getProperty("headless", "false").equalsIgnoreCase("true")
                || System.getenv("headless") != null && System.getenv("headless").equalsIgnoreCase("true")
                || "true".equalsIgnoreCase(System.getProperty("cucumber.options", "").contains("--headless") ? "true" : "false");

        if (isHeadlessRequested) {
            System.out.println("[DriverManager] FORCING HEADLESS MODE ACTIVATION for Thread: " + Thread.currentThread().getId());
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        } else {
            // prefer normal interactive runs
            if (System.getProperty("useNoSandbox", "false").equalsIgnoreCase("true")) {
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
            }
        }

        // Create the driver using the service (so logs go to file)
        System.out.println("[DriverManager] Starting ChromeDriverService (log: " + logFile.getAbsolutePath() + ")...");
        try {
            chromeService.start();
        } catch (IOException e) {
            System.err.println("[DriverManager] Failed to start ChromeDriverService: " + e.getMessage());
            // proceed to try creating driver without explicit start (ChromeDriver ctor may start its own service)
        }

        System.out.println("[DriverManager] Creating ChromeDriver instance...");
        WebDriver webDriver = new ChromeDriver(chromeService, options);

        // Keep references for shutdown and debugging
        service.set(chromeService);

        // Set timeouts to fail fast if navigation hangs
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        System.out.println("[DriverManager] Chrome browser launched successfully (PID log: " + logFile.getAbsolutePath() + ")");
        return webDriver;
    }

    // Determine if a driver is still usable
    private static boolean isDriverAlive(WebDriver wd) {
        try {
            if (wd == null) {
                return false;
            }
            wd.getTitle(); // simple check
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    // Public helper to safely maximize window (call from your page methods)
    public static void safeMaximize(WebDriver wd) {
        if (wd == null) {
            return;
        }
        try {
            wd.manage().window().maximize();
        } catch (WebDriverException e) {
            System.err.println("[DriverManager] maximize() failed: " + e.getMessage() + " — falling back to setSize");
            try {
                wd.manage().window().setSize(new Dimension(1920, 1080));
            } catch (Throwable ex) {
                System.err.println("[DriverManager] setSize fallback also failed: " + ex.getMessage());
            }
        }
    }

    // Close and cleanup
    public static void closeDriver() {
        WebDriver wd = driver.get();
        if (wd != null) {
            try {
                System.out.println("[DriverManager] Closing WebDriver...");
                wd.quit();
            } catch (Throwable t) {
                System.err.println("[DriverManager] Error quitting WebDriver: " + t.getMessage());
            } finally {
                driver.remove();
            }
        }

        ChromeDriverService svc = service.get();
        if (svc != null && svc.isRunning()) {
            try {
                svc.stop();
            } catch (Throwable t) {
                System.err.println("[DriverManager] Error stopping ChromeDriverService: " + t.getMessage());
            } finally {
                service.remove();
            }
        }
        System.out.println("[DriverManager] Browser closed successfully");
    }

    // Utility for tests: check if current thread's driver is alive
    public static boolean isCurrentDriverAlive() {
        return isDriverAlive(driver.get());
    }

    /**
     * Keep only the most recent number of chromedriver logs per thread.
     * Deletes older logs to avoid disk clutter during long runs.
     */
    private static void cleanOldChromeDriverLogs(File logDir) {
        final int MAX_LOGS_PER_THREAD = 0;
        if (!logDir.exists() || !logDir.isDirectory()) {
            return;
        }
        try {
            // Group logs by thread ID and sort by modification time
            Map<String, List<File>> logsByThread = new HashMap<>();
            File[] allLogs = logDir.listFiles((dir, name) -> name.matches("chromedriver-\\d+-.+\\.log"));

            if (allLogs == null || allLogs.length == 0) {
                return;
            }

            // Group logs by thread ID (robust extraction) and delete older files, keeping MAX_LOGS_PER_THREAD
            final Pattern strictPattern = Pattern.compile("^chromedriver-(\\d+)-(\\d+)\\.log$");
            final Pattern digitsPattern = Pattern.compile("(\\d+)");

            for (File log : allLogs) {
                String name = log.getName();
                String threadId = "unknown";

                // Try strict match first: chromedriver-<threadId>-<counter>.log
                Matcher m = strictPattern.matcher(name);
                if (m.matches()) {
                    threadId = m.group(1); // group(1) is the threadId (digits)
                } else {
                    // Fallback: find first digit sequence in the filename
                    Matcher fallback = digitsPattern.matcher(name);
                    if (fallback.find()) {
                        threadId = fallback.group(1);
                    }
                }

                logsByThread.computeIfAbsent(threadId, k -> new ArrayList<>()).add(log);
            }

            // Delete old logs, keeping only the most recent MAX_LOGS_PER_THREAD per thread
            int deleted = 0;
            for (List<File> logs : logsByThread.values()) {
                if (logs.size() > MAX_LOGS_PER_THREAD) {
                    // Sort by modification time (oldest first)
                    logs.sort(Comparator.comparingLong(File::lastModified));
                    int toDelete = logs.size() - MAX_LOGS_PER_THREAD;
                    for (int i = 0; i < toDelete; i++) {
                        File f = logs.get(i);
                        try {
                            if (f.delete()) {
                                deleted++;
                            } else {
                                System.err.println("[DriverManager] Warning: could not delete log file: " + f.getAbsolutePath());
                            }
                        } catch (SecurityException se) {
                            System.err.println("[DriverManager] Warning: SecurityException deleting log file: " + f.getAbsolutePath() + " -> " + se.getMessage());
                        }
                    }
                }
            }
            if (deleted > 0) {
                System.out.println("[DriverManager] Cleaned up " + deleted + " old chromedriver logs (keeping " + MAX_LOGS_PER_THREAD + " per thread)");
            }
        } catch (Exception e) {
            System.err.println("[DriverManager] Warning: failed to clean chromedriver logs: " + e.getMessage());
        }
    }
}