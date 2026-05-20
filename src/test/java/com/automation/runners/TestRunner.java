package com.automation.runners;


import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("uifeatures") // Ensure your .feature files are in src/test/resources/features

public class TestRunner {
}
