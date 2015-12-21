package org.apache.maven.cli;


import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.mockito.Mockito.when;
@Ignore
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
//        userProperties.setProperty(CsvPublisher.BUILDTIME_OUTPUT_CSV_FILE_PROPERTY,
//                testFile.getAbsolutePath());
//        userProperties.setProperty(CsvPublisher.BUILDTIME_OUTPUT_CSV_PROPERTY, "true");

        MavenProject project = new MavenProject();

        when(sessionEndEvent.getSession()).thenReturn(session);
        when(sessionEndEvent.getProject()).thenReturn(project);
        when(session.getSystemProperties()).thenReturn(systemProperties);
        when(session.getUserProperties()).thenReturn(userProperties);
    }

    @Test
    public void outputCsvWhenExtenstionIsAvailable(){
        subject.mojoSucceeded(sessionEndEvent);
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
