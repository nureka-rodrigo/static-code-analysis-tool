/*
 *  Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.scan.utils;

import io.ballerina.projects.Project;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.ProjectLoader;
import io.ballerina.projects.directory.SingleFileProject;
import io.ballerina.projects.util.ProjectUtils;
import io.ballerina.scan.BaseTest;
import io.ballerina.scan.Issue;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.ballerina.projects.util.ProjectConstants.LOCAL_REPOSITORY_NAME;
import static io.ballerina.scan.TestConstants.LINUX_LINE_SEPARATOR;
import static io.ballerina.scan.TestConstants.WINDOWS_LINE_SEPARATOR;

/**
 * Scan utilities tests.
 *
 * @since 0.1.0
 */
public class ScanUtilsTest extends BaseTest {
    private final Path validBalProject = testResources.resolve("test-resources")
            .resolve("valid-bal-project");
    private static final String RESULTS_DIRECTORY = "results";

    @AfterTest
    void cleanup() {
        Path resultsDirectoryPath = validBalProject.resolve(RESULTS_DIRECTORY);
        if (Files.exists(resultsDirectoryPath)) {
            ProjectUtils.deleteDirectory(resultsDirectoryPath);
        }
    }

    @Test(description = "test method for printing results to console")
    void testPrintToConsole() throws IOException {
        List<Issue> issues = new ArrayList<>();
        ScanUtils.printToConsole(issues, printStream);
        String expected = "[]";
        Assert.assertEquals(readOutput(true).trim(), expected);
    }

    @Test(description = "test method for saving results to file when no directory is provided")
    void testSaveToDirectory() throws IOException {
        List<Issue> issues = new ArrayList<>();
        Project project = ProjectLoader.loadProject(validBalProject);
        Path resultsFile = ScanUtils.saveToDirectory(issues, project, null);
        String result = Files.readString(resultsFile, StandardCharsets.UTF_8)
                .replace(WINDOWS_LINE_SEPARATOR, LINUX_LINE_SEPARATOR);
        String expected = "[]";
        Assert.assertEquals(result, expected);
    }

    @Test(description = "test method for saving results to file when directory is provided")
    void testSaveToProvidedDirectory() throws IOException {
        List<Issue> issues = new ArrayList<>();
        Project project = ProjectLoader.loadProject(validBalProject);
        Path resultsFile = ScanUtils.saveToDirectory(issues, project, RESULTS_DIRECTORY);
        String result = Files.readString(resultsFile, StandardCharsets.UTF_8)
                .replace(WINDOWS_LINE_SEPARATOR, LINUX_LINE_SEPARATOR);
        String expected = "[]";
        Assert.assertEquals(result, expected);
    }

    @Test(description = "test method for creating html analysis report from analysis results")
    void testGenerateScanReport() throws IOException {
        List<Issue> issues = new ArrayList<>();
        Project project = ProjectLoader.loadProject(validBalProject);
        Path scanReportPath = ScanUtils.generateScanReport(issues, project, null);
        String result = Files.readString(scanReportPath, StandardCharsets.UTF_8)
                .replace(WINDOWS_LINE_SEPARATOR, LINUX_LINE_SEPARATOR);
        String expected = getExpectedOutput("empty-issues-html-report.txt");
        Assert.assertEquals(result, expected);
    }

    @Test(description =
            "test method for creating html analysis report from analysis results when directory is provided")
    void testGenerateScanReportToProvidedDirectory() throws IOException {
        List<Issue> issues = new ArrayList<>();
        Project project = ProjectLoader.loadProject(validBalProject);
        Path scanReportPath = ScanUtils.generateScanReport(issues, project, RESULTS_DIRECTORY);
        String result = Files.readString(scanReportPath, StandardCharsets.UTF_8)
                .replace(WINDOWS_LINE_SEPARATOR, LINUX_LINE_SEPARATOR);
        String expected = getExpectedOutput("empty-issues-html-report.txt");
        Assert.assertEquals(result, expected);
    }

    @Test(description =
            "test method for loading configurations from a Scan.toml file in a Ballerina single file project")
    void testloadScanTomlConfigurationsForSingleFileProject() {
        Path ballerinaProject = testResources.resolve("test-resources")
                .resolve("single-file-project-with-config-file").resolve("main.bal");
        Project project = SingleFileProject.load(ballerinaProject);
        System.setProperty("user.dir", ballerinaProject.toString());
        ScanTomlFile scanTomlFile = ScanUtils.loadScanTomlConfigurations(project, printStream).orElse(null);
        System.setProperty("user.dir", userDir);
        Assert.assertNotNull(scanTomlFile);
    }

    @Test(description = "test method for loading configurations from a Scan.toml file")
    void testloadScanTomlConfigurations() {
        Path ballerinaProject = testResources.resolve("test-resources")
                .resolve("bal-project-with-config-file");
        Project project = BuildProject.load(ballerinaProject);
        System.setProperty("user.dir", ballerinaProject.toString());
        ScanTomlFile scanTomlFile = ScanUtils.loadScanTomlConfigurations(project, printStream).orElse(null);
        System.setProperty("user.dir", userDir);
        Assert.assertNotNull(scanTomlFile);;
        Set<ScanTomlFile.Analyzer> analyzers = scanTomlFile.getAnalyzers();
        List<ScanTomlFile.Analyzer> analyzerList = new ArrayList<>(analyzers);
        Assert.assertEquals(analyzerList.size(), 4);
        ScanTomlFile.Analyzer analyzer = analyzerList.get(0);
        assertAnalyzer(analyzer, "exampleOrg", "example_module_static_code_analyzer", null, null);
        analyzer = analyzerList.get(1);
        assertAnalyzer(analyzer, "ballerina", "example_module_static_code_analyzer", "0.1.0", null);
        analyzer = analyzerList.get(2);
        assertAnalyzer(analyzer, "ballerinax", "example_module_static_code_analyzer", "0.1.0", LOCAL_REPOSITORY_NAME);
        Set<ScanTomlFile.RuleToFilter> rulesToInclude = scanTomlFile.getRulesToInclude();
        List<ScanTomlFile.RuleToFilter> ruleToIncludeList = new ArrayList<>(rulesToInclude);
        Assert.assertEquals(ruleToIncludeList.size(), 4);
        ScanTomlFile.RuleToFilter ruleToInclude = ruleToIncludeList.get(0);
        Assert.assertEquals(ruleToInclude.id(), "ballerina:1");
        ruleToInclude = ruleToIncludeList.get(1);
        Assert.assertEquals(ruleToInclude.id(), "exampleOrg/example_module_static_code_analyzer:1");
        ruleToInclude = ruleToIncludeList.get(2);
        Assert.assertEquals(ruleToInclude.id(), "ballerina/example_module_static_code_analyzer:1");
        ruleToInclude = ruleToIncludeList.get(3);
        Assert.assertEquals(ruleToInclude.id(), "ballerinax/example_module_static_code_analyzer:1");
        Set<ScanTomlFile.RuleToFilter> rulesToExclude = scanTomlFile.getRulesToExclude();
        List<ScanTomlFile.RuleToFilter> ruleToExcludeList = new ArrayList<>(rulesToExclude);
        Assert.assertEquals(ruleToExcludeList.size(), 1);
        ScanTomlFile.RuleToFilter ruleToExclude = ruleToExcludeList.get(0);
        Assert.assertEquals(ruleToExclude.id(), "ballerina:1");
        Set<ScanTomlFile.Platform> platforms = scanTomlFile.getPlatforms();
        Assert.assertEquals(platforms.size(), 0);
    }

    @Test(description =
            "test method for loading configurations from a Scan.toml file with invalid platform JAR")
    void testloadInvalidPlatformJAR() throws IOException {
        Path ballerinaProject = testResources.resolve("test-resources")
                .resolve("bal-project-with-invalid-platform-jar");
        Project project = BuildProject.load(ballerinaProject);
        System.setProperty("user.dir", ballerinaProject.toString());
        ScanTomlFile scanTomlFile = ScanUtils.loadScanTomlConfigurations(project, printStream).orElse(null);
        System.setProperty("user.dir", userDir);
        Assert.assertNull(scanTomlFile);
        String expected = getExpectedOutput("scan-toml-invalid-platform-jar.txt");
        Assert.assertEquals(readOutput(true).trim(), expected);
    }

    @Test(description =
            "test method for loading configurations from a Scan.toml file with invalid platform configuration")
    void testloadInvalidPlatformScanTomlConfigurations() throws IOException {
        Path ballerinaProject = testResources.resolve("test-resources")
                .resolve("bal-project-with-invalid-platform-config-file");
        Project project = BuildProject.load(ballerinaProject);
        System.setProperty("user.dir", ballerinaProject.toString());
        ScanTomlFile scanTomlFile = ScanUtils.loadScanTomlConfigurations(project, printStream).orElse(null);
        System.setProperty("user.dir", userDir);
        Assert.assertNull(scanTomlFile);
        String expected = getExpectedOutput("scan-toml-invalid-platform-config.txt");
        Assert.assertEquals(readOutput(true).trim(), expected);
    }

    @Test(description =
            "test method for loading configurations from a Scan.toml file with invalid Ballerina.toml configuration")
    void testloadScanTomlConfigurationsWithInvalidBallerinaTomlConfiguration() throws IOException {
        Path ballerinaProject = testResources.resolve("test-resources")
                .resolve("bal-project-with-invalid-file-configuration");
        Project project = BuildProject.load(ballerinaProject);
        System.setProperty("user.dir", ballerinaProject.toString());
        ScanTomlFile scanTomlFile = ScanUtils.loadScanTomlConfigurations(project, printStream).orElse(null);
        System.setProperty("user.dir", userDir);
        Assert.assertNull(scanTomlFile);
        String expected = DiagnosticLog.error(DiagnosticCode.MISSING_CONFIG_FIELD);
        Assert.assertEquals(readOutput(true).trim(), expected);
    }

    @Test(description = "test method for loading configurations from an external configuration file")
    void testloadExternalScanTomlConfigurations() {
        Path ballerinaProject = testResources.resolve("test-resources")
                .resolve("bal-project-with-external-config-file");
        Project project = BuildProject.load(ballerinaProject);
        System.setProperty("user.dir", ballerinaProject.toString());
        ScanTomlFile scanTomlFile = ScanUtils.loadScanTomlConfigurations(project, printStream).orElse(null);
        System.setProperty("user.dir", userDir);
        Assert.assertNotNull(scanTomlFile);
        Set<ScanTomlFile.Analyzer> analyzers = scanTomlFile.getAnalyzers();
        List<ScanTomlFile.Analyzer> analyzerList = new ArrayList<>(analyzers);
        Assert.assertEquals(analyzerList.size(), 4);
        ScanTomlFile.Analyzer analyzer = analyzerList.get(0);
        assertAnalyzer(analyzer, "exampleOrg", "example_module_static_code_analyzer", null, null);
        analyzer = analyzerList.get(1);
        assertAnalyzer(analyzer, "ballerina", "example_module_static_code_analyzer", "0.1.0", null);
        analyzer = analyzerList.get(2);
        assertAnalyzer(analyzer, "ballerinax", "example_module_static_code_analyzer", "0.1.0", LOCAL_REPOSITORY_NAME);
        Set<ScanTomlFile.RuleToFilter> rulesToInclude = scanTomlFile.getRulesToInclude();
        List<ScanTomlFile.RuleToFilter> ruleToIncludeList = new ArrayList<>(rulesToInclude);
        Assert.assertEquals(ruleToIncludeList.size(), 4);
        ScanTomlFile.RuleToFilter ruleToInclude = ruleToIncludeList.get(0);
        Assert.assertEquals(ruleToInclude.id(), "ballerina:1");
        ruleToInclude = ruleToIncludeList.get(1);
        Assert.assertEquals(ruleToInclude.id(), "exampleOrg/example_module_static_code_analyzer:1");
        ruleToInclude = ruleToIncludeList.get(2);
        Assert.assertEquals(ruleToInclude.id(), "ballerina/example_module_static_code_analyzer:1");
        ruleToInclude = ruleToIncludeList.get(3);
        Assert.assertEquals(ruleToInclude.id(), "ballerinax/example_module_static_code_analyzer:1");
        Set<ScanTomlFile.RuleToFilter> rulesToExclude = scanTomlFile.getRulesToExclude();
        List<ScanTomlFile.RuleToFilter> ruleToExcludeList = new ArrayList<>(rulesToExclude);
        Assert.assertEquals(ruleToExcludeList.size(), 1);
        ScanTomlFile.RuleToFilter ruleToExclude = ruleToExcludeList.get(0);
        Assert.assertEquals(ruleToExclude.id(), "ballerina:1");
        Set<ScanTomlFile.Platform> platforms = scanTomlFile.getPlatforms();
        Assert.assertEquals(platforms.size(), 0);
    }

    @Test(description = "test method for loading configurations from an invalid external configuration file")
    void testloadInvalidExternalScanTomlConfigurations() throws IOException {
        Path ballerinaProject = testResources.resolve("test-resources")
                .resolve("bal-project-with-invalid-external-config-file");
        Project project = BuildProject.load(ballerinaProject);
        System.setProperty("user.dir", ballerinaProject.toString());
        ScanTomlFile scanTomlFile = ScanUtils.loadScanTomlConfigurations(project, printStream).orElse(null);
        System.setProperty("user.dir", userDir);
        String expected = DiagnosticLog.error(DiagnosticCode.LOADING_REMOTE_CONFIG_FILE, "no protocol: ./Config.toml");
        Assert.assertNull(scanTomlFile);
        Assert.assertEquals(readOutput(true).trim(), expected);
    }

    @Test(description = "test method for loading configurations from a remote configuration file")
    void testloadRemoteScanTomlConfigurations() {
        Path ballerinaProject = testResources.resolve("test-resources")
                .resolve("bal-project-with-remote-config-file");
        Project project = BuildProject.load(ballerinaProject);
        System.setProperty("user.dir", ballerinaProject.toString());
        ScanTomlFile scanTomlFile = ScanUtils.loadScanTomlConfigurations(project, printStream).orElse(null);
        System.setProperty("user.dir", userDir);
        Assert.assertNotNull(scanTomlFile);
        Set<ScanTomlFile.Analyzer> analyzers = scanTomlFile.getAnalyzers();
        List<ScanTomlFile.Analyzer> analyzerList = new ArrayList<>(analyzers);
        Assert.assertEquals(analyzerList.size(), 4);
        ScanTomlFile.Analyzer analyzer = analyzerList.get(0);
        assertAnalyzer(analyzer, "exampleOrg", "example_module_static_code_analyzer", null, null);
        analyzer = analyzerList.get(1);
        assertAnalyzer(analyzer, "ballerina", "example_module_static_code_analyzer", "0.1.0", null);
        analyzer = analyzerList.get(2);
        assertAnalyzer(analyzer, "ballerinax", "example_module_static_code_analyzer", "0.1.0", LOCAL_REPOSITORY_NAME);
        Set<ScanTomlFile.RuleToFilter> rulesToInclude = scanTomlFile.getRulesToInclude();
        List<ScanTomlFile.RuleToFilter> ruleToIncludeList = new ArrayList<>(rulesToInclude);
        Assert.assertEquals(ruleToIncludeList.size(), 4);
        ScanTomlFile.RuleToFilter ruleToInclude = ruleToIncludeList.get(0);
        Assert.assertEquals(ruleToInclude.id(), "ballerina:1");
        ruleToInclude = ruleToIncludeList.get(1);
        Assert.assertEquals(ruleToInclude.id(), "exampleOrg/example_module_static_code_analyzer:1");
        ruleToInclude = ruleToIncludeList.get(2);
        Assert.assertEquals(ruleToInclude.id(), "ballerina/example_module_static_code_analyzer:1");
        ruleToInclude = ruleToIncludeList.get(3);
        Assert.assertEquals(ruleToInclude.id(), "ballerinax/example_module_static_code_analyzer:1");
        Set<ScanTomlFile.RuleToFilter> rulesToExclude = scanTomlFile.getRulesToExclude();
        List<ScanTomlFile.RuleToFilter> ruleToExcludeList = new ArrayList<>(rulesToExclude);
        Assert.assertEquals(ruleToExcludeList.size(), 1);
        ScanTomlFile.RuleToFilter ruleToExclude = ruleToExcludeList.get(0);
        Assert.assertEquals(ruleToExclude.id(), "ballerina:1");
        Set<ScanTomlFile.Platform> platforms = scanTomlFile.getPlatforms();
        Assert.assertEquals(platforms.size(), 0);
    }

    @Test(description = "test method for loading configurations from an invalid remote configuration file")
    void testloadInvalidRemoteScanTomlConfigurations() throws IOException {
        Path ballerinaProject = testResources.resolve("test-resources")
                .resolve("bal-project-with-invalid-remote-config-file");
        Project project = BuildProject.load(ballerinaProject);
        System.setProperty("user.dir", ballerinaProject.toString());
        ScanTomlFile scanTomlFile = ScanUtils.loadScanTomlConfigurations(project, printStream).orElse(null);
        System.setProperty("user.dir", userDir);
        String expected = DiagnosticLog.error(DiagnosticCode.LOADING_REMOTE_CONFIG_FILE,
                "no protocol: www.example.com/Config.toml");
        Assert.assertNull(scanTomlFile);
        Assert.assertEquals(readOutput(true).trim(), expected);
    }

    private void assertAnalyzer(ScanTomlFile.Analyzer analyzer, String org, String name, String version,
                                String repository) {
        Assert.assertEquals(analyzer.org(), org);
        Assert.assertEquals(analyzer.name(), name);
        Assert.assertEquals(analyzer.version(), version);
        Assert.assertEquals(analyzer.repository(), repository);
    }
}
