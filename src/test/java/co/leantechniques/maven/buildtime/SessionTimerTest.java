package co.leantechniques.maven.buildtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import co.leantechniques.maven.buildtime.output.CsvReporter;
import co.leantechniques.maven.buildtime.output.LogReporter;

@ExtendWith(MockitoExtension.class)
class SessionTimerTest {

    private ConcurrentMap<String,ProjectTimer> existingProjects;
    private SessionTimer sessionTimer;
    private ProjectTimer oneProject;
    private ProjectTimer anotherProject;
    private ConcurrentMap<String, MojoTimer> mojoTiming;
    private ConcurrentMap<String, MojoTimer> anotherMojoTiming;

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

    private final Properties userProperties = new Properties();

    private final Properties systemProperties = new Properties();

    @BeforeEach
    public void setUp() {
        logReporter = new LogReporter();
        csvReporter = new CsvReporter();
        existingProjects = new ConcurrentHashMap<>();
        SystemClock mockClock = mock(SystemClock.class);
        lenient().when(mockClock.currentTimeMillis())
                .thenReturn(100L)
                .thenReturn(200L);

        sessionTimer = new SessionTimer(existingProjects, mockClock);

        mojoTiming = new ConcurrentHashMap<>();
        anotherMojoTiming = new ConcurrentHashMap<>();
        oneProject = new ProjectTimer("one", mojoTiming, mockClock);
        anotherProject = new ProjectTimer("two", anotherMojoTiming, mockClock);
        mojoExecution = createMojoExecution();
        project = createMavenProject();
        outputStream = new ByteArrayOutputStream();
        printWriter = new PrintWriter(outputStream);

        userProperties.setProperty(Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "true");
        lenient().when(sessionEndEvent.getSession()).thenReturn(session);
        lenient().when(session.getSystemProperties()).thenReturn(systemProperties);
        lenient().when(session.getUserProperties()).thenReturn(userProperties);
        lenient().when(sessionEndEvent.getType()).thenReturn(ExecutionEvent.Type.SessionEnded);
    }

    @Test
    void getProjectReturnsNewWhenNotExists(){
        ProjectTimer actual = sessionTimer.getProject("not existing");

        assertNotNull(actual);
    }

    @Test
    void getProjectReturnsSameWhenExists() {
        existingProjects.put("one", oneProject);

        ProjectTimer actual = sessionTimer.getProject("one");

        assertSame(oneProject, actual);
    }

    @Test
    void writeOneProjectWithOnePlugin() {
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
    void verifyResultsOrderedByStartTime() {
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
    void writeToOneProjectWithOnePlugin() {
        MojoTimer goal1Timer = new MojoTimer("one", "artifactId:goal1", 1, 2);
        MojoTimer goal2Timer = new MojoTimer("one", "artifactId:goal2", 2, 4);
        mojoTiming.put(goal1Timer.getName(), goal1Timer);
        mojoTiming.put(goal2Timer.getName(), goal2Timer);

        existingProjects.put("one", oneProject);

        csvReporter.writeTo(sessionTimer, printWriter);

        printWriter.flush();
        String output = outputStream.toString();
        String[] split = output.split("\r?\n");

        assertEquals("\"Module\";\"Mojo\";\"Time\"", split[0]);
        assertEquals("\"one\";\"artifactId:goal1\";\"0.001\"", split[1]);
        assertEquals("\"one\";\"artifactId:goal2\";\"0.002\"", split[2]);
    }

    @Test
    void verifyThatProjectsAreOrderedByStartTime() {
        MojoTimer goal1Timer = new MojoTimer("one", "artifactId:goal1", 6, 9);
        MojoTimer goal2Timer = new MojoTimer("one", "artifactId:goal2", 5, 7);
        mojoTiming.put(goal1Timer.getName(), goal1Timer);
        mojoTiming.put(goal2Timer.getName(), goal2Timer);

        existingProjects.put("one", oneProject);

        MojoTimer goal3Timer = new MojoTimer("two", "artifactId:goal3", 1, 2);
        MojoTimer goal4Timer = new MojoTimer("two", "artifactId:goal4", 2, 4);
        anotherMojoTiming.put(goal3Timer.getName(), goal3Timer);
        anotherMojoTiming.put(goal4Timer.getName(), goal4Timer);

        existingProjects.put("two", anotherProject);

        csvReporter.writeTo(sessionTimer, printWriter);

        printWriter.flush();
        String output = outputStream.toString();
        String[] split = output.split("\r?\n");

        assertEquals("\"Module\";\"Mojo\";\"Time\"", split[0]);
        assertEquals("\"two\";\"artifactId:goal3\";\"0.001\"", split[1]);
        assertEquals("\"two\";\"artifactId:goal4\";\"0.002\"", split[2]);
        assertEquals("\"one\";\"artifactId:goal2\";\"0.002\"", split[3]);
        assertEquals("\"one\";\"artifactId:goal1\";\"0.003\"", split[4]);
    }

    @Test
    void successfulMojoShouldStopTimer(){
        sessionTimer.mojoStarted(project, mojoExecution);
        sessionTimer.mojoSucceeded(project, mojoExecution);

        MojoTimer mojoTimer = sessionTimer.getMojoTimer(project, mojoExecution);

        assertEquals(Long.valueOf(100), mojoTimer.getDuration());
    }

    @Test
    void failureMojoShouldStopTimer(){
        sessionTimer.mojoStarted(project, mojoExecution);
        sessionTimer.mojoFailed(project, mojoExecution);

        MojoTimer mojoTimer = sessionTimer.getMojoTimer(project, mojoExecution);

        assertEquals(Long.valueOf(100), mojoTimer.getDuration());
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
