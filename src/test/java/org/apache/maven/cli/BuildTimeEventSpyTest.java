package org.apache.maven.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import io.github.glytching.junit.extension.folder.TemporaryFolder;
import io.github.glytching.junit.extension.folder.TemporaryFolderExtension;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import co.leantechniques.maven.buildtime.Constants;
import co.leantechniques.maven.buildtime.output.CsvReporter;
import co.leantechniques.maven.buildtime.output.LogReporter;

@ExtendWith({MockitoExtension.class, TemporaryFolderExtension.class})
class BuildTimeEventSpyTest {

    @Mock
    private Logger logger;

    @Mock
    private ExecutionEvent sessionEndEvent;

    @Mock
    private MavenSession session;

    private final Properties userProperties = new Properties();

    private final Properties systemProperties = new Properties();

    public TemporaryFolder temporaryFolder;

    private BuildTimeEventSpy subject;

    private File testFile;

    @BeforeEach
    public void setUp(TemporaryFolder temporaryFolder) throws IOException {
        subject = new BuildTimeEventSpy(logger, new LogReporter(), new CsvReporter());

        this.temporaryFolder = temporaryFolder;

        testFile = this.temporaryFolder.createFile("test.csv");
        userProperties.setProperty(Constants.BUILDTIME_OUTPUT_CSV_FILE_PROPERTY,
                testFile.getAbsolutePath());
        userProperties.setProperty(Constants.BUILDTIME_OUTPUT_CSV_PROPERTY, "true");

        when(sessionEndEvent.getSession()).thenReturn(session);
        when(session.getSystemProperties()).thenReturn(systemProperties);
        when(session.getUserProperties()).thenReturn(userProperties);
        when(sessionEndEvent.getType()).thenReturn(ExecutionEvent.Type.SessionEnded);
    }

    @Test
    void testCsvHeaderOutput() throws Exception {
        subject.onEvent(sessionEndEvent);

        assertTrue(testFile.exists());
        List<String> lines = FileUtils.loadFile(testFile);
        assertEquals("\"Module\";\"Mojo\";\"Time\"", lines.get(0));
    }


    @Test
    void testCsvEntries() throws Exception {
        ExecutionEvent mojo = mock(ExecutionEvent.class);
        when(mojo.getProject()).thenReturn(createMavenProject());
        when(mojo.getMojoExecution()).thenReturn(createMojoExecution());
        when(mojo.getType()).thenReturn(ExecutionEvent.Type.MojoStarted);
        when(mojo.getType()).thenReturn(ExecutionEvent.Type.MojoSucceeded);

        subject.onEvent(mojo);
        subject.onEvent(mojo);
        subject.onEvent(sessionEndEvent);

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
