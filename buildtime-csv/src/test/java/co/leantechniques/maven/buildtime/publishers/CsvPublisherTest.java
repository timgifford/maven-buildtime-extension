package co.leantechniques.maven.buildtime.publishers;

import co.leantechniques.maven.buildtime.MojoTimer;
import co.leantechniques.maven.buildtime.SessionTimer;
import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class CsvPublisherTest {

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


        private File testFile;
    private CsvPublisher subject;
    private SessionTimer sessionTimer;
    private MavenProject project;

    @Before
        public void setUp() throws IOException{
            subject = new CsvPublisher(logger);

            testFile = new File(temporaryFolder.getRoot(), "test.csv");
//        userProperties.setProperty(CsvPublisher.BUILDTIME_OUTPUT_CSV_FILE_PROPERTY,
//                testFile.getAbsolutePath());
//        userProperties.setProperty(CsvPublisher.BUILDTIME_OUTPUT_CSV_PROPERTY, "true");

        project = new MavenProject();

        when(sessionEndEvent.getSession()).thenReturn(session);
            when(sessionEndEvent.getProject()).thenReturn(project);
            when(session.getSystemProperties()).thenReturn(systemProperties);
            when(session.getUserProperties()).thenReturn(userProperties);
        }

    @Test
    public void testCsvHeaderOutput() throws IOException {
//        subject.sessionEnded(sessionEndEvent);
        subject.publish(sessionEndEvent, sessionTimer );
        assertTrue(testFile.exists());
        List<String> lines = FileUtils.loadFile(testFile);
        assertEquals("\"Module\";\"Mojo\";\"Time\"", lines.get(0));
    }


    @Test
    public void testCsvEntries() throws IOException {
        ExecutionEvent mojo = mock(ExecutionEvent.class);
        when(mojo.getProject()).thenReturn(createMavenProject());
        when(mojo.getMojoExecution()).thenReturn(createMojoExecution());

//        subject.mojoStarted(mojo);
//        subject.mojoSucceeded(mojo);
//        subject.sessionEnded(sessionEndEvent);

        assertTrue(testFile.exists());
        List<String> lines = FileUtils.loadFile(testFile);
        assertEquals("\"Module\";\"Mojo\";\"Time\"", lines.get(0));

        String[] split = lines.get(1).split(";");

        assertEquals(3, split.length);
        assertEquals("\"maven-project-artifact\"", split[0]);
        assertEquals("\"plugin:goal (executionId)\"", split[1]);
        assertTrue(Pattern.matches("\"\\d+\\.\\d+\"", split[2]));

    }

//    @Test
//    public void writeToOneProjectWithOnePlugin() {
//
//        MojoTimer goal1Timer = new MojoTimer("artifactId:goal1", 1, 2);
//        MojoTimer goal2Timer = new MojoTimer("artifactId:goal2", 1, 3);
//        mojoTiming.put(goal1Timer.getName(), goal1Timer);
//        mojoTiming.put(goal2Timer.getName(), goal2Timer);
//        sessionTimer.get
//
//        existingProjects.put("one", oneProject);
//
//        new CsvPublisher(null).writeTo(printWriter, sessionTimer);
//
//        printWriter.flush();
//        String output = outputStream.toString();
//        String[] split = output.split("\r?\n");
//
//        Assert.assertEquals("\"Module\";\"Mojo\";\"Time\"", split[0]);
//        Assert.assertEquals("\"one\";\"artifactId:goal1\";\"0.001\"", split[1]);
//        Assert.assertEquals("\"one\";\"artifactId:goal2\";\"0.002\"", split[2]);
//    }

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