package apitests;

import com.intuit.karate.Runner;
import com.intuit.karate.Runner.Builder;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.File;

class ApiTestRunner {

    @Test
    void runAllTests() {
        // Clean old Karate reports before running
        cleanKarateReports();

        // The Run configuration
        Builder builder = Runner.path("classpath:apifeatures")
                .tags("~@ignore");

        // Specify parallel execution threads
        var results = builder.parallel(5);

        // Asserts that there are no test failures
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
    private void cleanKarateReports() {
        File target = new File(System.getProperty("user.dir"), "target");
        if (!target.exists() || !target.isDirectory()) {
            return;
        }

        // Delete karate-summary*.html files
        File[] summaries = target.listFiles((d, name) ->
                name.startsWith("karate-summary") && name.endsWith(".html"));
        if (summaries != null) {
            for (File f : summaries) {
                if (f.delete()) {
                    System.out.println("[APITestRunner] Deleted: " + f.getName());
                }
            }
        }

        // Delete karate-reports* directories
        File[] dirs = target.listFiles((d, name) ->
                name.startsWith("karate-reports") && new File(d, name).isDirectory());
        if (dirs != null) {
            for (File dir : dirs) {
                deleteRecursively(dir);
                System.out.println("[APITestRunner] Deleted directory: " + dir.getName());
            }
        }
    }

    private void deleteRecursively(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) deleteRecursively(c);
            }
        }
        f.delete();
    }
}

