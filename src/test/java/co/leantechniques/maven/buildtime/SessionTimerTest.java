package co.leantechniques.maven.buildtime;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import co.leantechniques.maven.buildtime.output.CsvReporter;
import co.leantechniques.maven.buildtime.output.LogReporter;

@RunWith(MockitoJUnitRunner.class)
public class SessionTimerTest {

    private ConcurrentMap<String,ProjectTimer> existingProjects;
    private SessionTimer sessionTimer;
    private ProjectTimer oneProject;
    private ConcurrentMap<String, MojoTimer> mojoTiming;
    @Mock
    private Logger logger;

    @Mock
    private ExecutionEvent sessionEndEvent;

    @Mock
    private MavenSession session;

    private CsvReporter csvReporter;
    private LogReporter logReporter;
    private MojoExecution mojoExecution;
    private MavenProject project;
    private PrintWriter printWriter;

    private ByteArrayOutputStream outputStream;

    private Properties userProperties = new Properties();

    private Properties systemProperties = new Properties();

    @Before
    public void setUp() throws Exception {
        logReporter = new LogReporter();
        csvReporter = new CsvReporter();
        existingProjects = new ConcurrentHashMap<String, ProjectTimer>();
        SystemClock mockClock = mock(SystemClock.class);
        when(mockClock.currentTimeMillis())
                .thenReturn(100L)
                .thenReturn(200L);

        sessionTimer = new SessionTimer(existingProjects, mockClock);

        mojoTiming = new ConcurrentHashMap<String, MojoTimer>();
        oneProject = new ProjectTimer("one", mojoTiming, mockClock);
        mojoExecution = createMojoExecution();
        project = createMavenProject();
        outputStream = new ByteArrayOutputStream();
        printWriter = new PrintWriter(outputStream);

        userProperties.setProperty(Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "true");
        when(sessionEndEvent.getSession()).thenReturn(session);
        when(session.getSystemProperties()).thenReturn(systemProperties);
        when(session.getUserProperties()).thenReturn(userProperties);
        when(sessionEndEvent.getType()).thenReturn(ExecutionEvent.Type.SessionEnded);
    }

    @Test
    public void getProjectReturnsNewWhenNotExists(){
        ProjectTimer actual = sessionTimer.getProject("not existing");

        assertNotNull(actual);
    }

    @Test
    public void getProjectReturnsSameWhenExists() {
        existingProjects.put("one", oneProject);

        ProjectTimer actual = sessionTimer.getProject("one");

        assertSame(oneProject, actual);
    }

    @Test
    public void writeOneProjectWithOnePlugin() {
        MojoTimer goal1Timer = new MojoTimer("one", "artifactId:goal1", 1, 2);
        MojoTimer goal2Timer = new MojoTimer("one", "artifactId:goal2", 2, 4);
        mojoTiming.put(goal1Timer.getName(), goal1Timer);
        mojoTiming.put(goal2Timer.getName(), goal2Timer);

        existingProjects.put("one", oneProject);

        logReporter.performReport(logger, sessionEndEvent, sessionTimer);

        String dividerLine = LogReporter.DIVIDER;

        InOrder inOrder = inOrder(logger);
        inOrder.verify(logger).info(dividerLine);
        inOrder.verify(logger).info("Build Time Summary:");
        inOrder.verify(logger).info(dividerLine);
        inOrder.verify(logger).info("one [0.003s]");
        inOrder.verify(logger).info("  artifactId:goal1 ......................................... [0.001s]");
        inOrder.verify(logger).info("  artifactId:goal2 ......................................... [0.002s]");
    }

    @Test
    public void testResultsOrderedByStartTime() {
        MojoTimer goal1Timer = new MojoTimer("one", "artifactId:goal1", 2, 5);
        MojoTimer goal2Timer = new MojoTimer("one", "artifactId:goal2", 1, 3);
        mojoTiming.put(goal1Timer.getName(), goal1Timer);
        mojoTiming.put(goal2Timer.getName(), goal2Timer);

        existingProjects.put("one", oneProject);

        logReporter.performReport(logger, sessionEndEvent, sessionTimer);

        String dividerLine = LogReporter.DIVIDER;

        InOrder inOrder = inOrder(logger);
        inOrder.verify(logger).info(dividerLine);
        inOrder.verify(logger).info("Build Time Summary:");
        inOrder.verify(logger).info(dividerLine);
        inOrder.verify(logger).info("one [0.004s]");
        inOrder.verify(logger).info("  artifactId:goal2 ......................................... [0.002s]");
        inOrder.verify(logger).info("  artifactId:goal1 ......................................... [0.003s]");
    }

    @Test
    public void writeToOneProjectWithOnePlugin() {
        MojoTimer goal1Timer = new MojoTimer("one", "artifactId:goal1", 1, 2);
        MojoTimer goal2Timer = new MojoTimer("one", "artifactId:goal2", 2, 4);
        mojoTiming.put(goal1Timer.getName(), goal1Timer);
        mojoTiming.put(goal2Timer.getName(), goal2Timer);

        existingProjects.put("one", oneProject);

        csvReporter.writeTo(sessionTimer, printWriter);

        printWriter.flush();
        String output = outputStream.toString();
        String[] split = output.split("\r?\n");

        Assert.assertEquals(split[0], "\"Module\";\"Mojo\";\"Time\"");
        Assert.assertEquals(split[1], "\"one\";\"artifactId:goal1\";\"0.001\"");
        Assert.assertEquals(split[2], "\"one\";\"artifactId:goal2\";\"0.002\"");
    }

    @Test
    public void successfulMojoShouldStopTimer(){
        sessionTimer.mojoStarted(project, mojoExecution);
        sessionTimer.mojoSucceeded(project, mojoExecution);

        MojoTimer mojoTimer = sessionTimer.getMojoTimer(project, mojoExecution);

        assertEquals(new Long(100), mojoTimer.getDuration());
    }

    @Test
    public void failureMojoShouldStopTimer(){
        sessionTimer.mojoStarted(project, mojoExecution);
        sessionTimer.mojoFailed(project, mojoExecution);

        MojoTimer mojoTimer = sessionTimer.getMojoTimer(project, mojoExecution);

        assertEquals(new Long(100), mojoTimer.getDuration());
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
