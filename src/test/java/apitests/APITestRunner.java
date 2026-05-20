package apitests;

import com.intuit.karate.Runner;
import com.intuit.karate.Runner.Builder;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ApiTestRunner {

    @Test
    void runAllTests() {
        // The Run configuration
        Builder builder = Runner.path("classpath:apifeatures")
                .tags("~@ignore");

        // Specify parallel execution threads
        var results = builder.parallel(5);

        // Asserts that there are no test failures
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
}

