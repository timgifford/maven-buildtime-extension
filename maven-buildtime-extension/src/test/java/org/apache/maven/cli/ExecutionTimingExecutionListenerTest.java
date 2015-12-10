package org.apache.maven.cli;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import co.leantechniques.maven.buildtime.publisher.CsvPublisher;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class ExecutionTimingExecutionListenerTest {
    @Mock
    private Logger logger;

    @Mock
    private ExecutionEvent sessionEndEvent;

    @Mock
    private MavenSession session;

    private Properties userProperties = new Properties();

    private Properties systemProperties = new Properties();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    private ExecutionTimingExecutionListener subject;

    private File testFile;

    @Before
    public void setUp() throws IOException{
        subject = new ExecutionTimingExecutionListener(logger);

        testFile = new File(temporaryFolder.getRoot(), "test.csv");
        userProperties.setProperty(CsvPublisher.BUILDTIME_OUTPUT_CSV_FILE_PROPERTY,
                testFile.getAbsolutePath());
        userProperties.setProperty(CsvPublisher.BUILDTIME_OUTPUT_CSV_PROPERTY, "true");

        when(sessionEndEvent.getSession()).thenReturn(session);
        when(session.getSystemProperties()).thenReturn(systemProperties);
        when(session.getUserProperties()).thenReturn(userProperties);
    }

    @Test
    public void testCsvHeaderOutput() throws IOException {
        subject.sessionEnded(sessionEndEvent);

        assertTrue(testFile.exists());
        List<String> lines = FileUtils.loadFile(testFile);
        assertEquals("\"Module\";\"Mojo\";\"Time\"", lines.get(0));
    }


    @Test
    public void testCsvEntries() throws IOException {
        ExecutionEvent mojo = mock(ExecutionEvent.class);
        when(mojo.getProject()).thenReturn(createMavenProject());
        when(mojo.getMojoExecution()).thenReturn(createMojoExecution());

        subject.mojoStarted(mojo);
        subject.mojoSucceeded(mojo);
        subject.sessionEnded(sessionEndEvent);

        assertTrue(testFile.exists());
        List<String> lines = FileUtils.loadFile(testFile);
        assertEquals("\"Module\";\"Mojo\";\"Time\"", lines.get(0));
        
        String[] split = lines.get(1).split(";");

        assertEquals(3, split.length);
        assertEquals("\"maven-project-artifact\"", split[0]);
        assertEquals("\"plugin:goal (executionId)\"", split[1]);
        assertTrue(Pattern.matches("\"\\d+\\.\\d+\"", split[2]));

    }

    private MojoExecution createMojoExecution() {
        Plugin plugin = new Plugin();
        plugin.setArtifactId("plugin");
        return new MojoExecution(plugin, "goal", "executionId");
    }

    private MavenProject createMavenProject() {
        MavenProject project = new MavenProject();
        project.setArtifactId("maven-project-artifact");
        return project;
    }
}
