package co.leantechniques.maven.buildtime;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class SessionTimerTest {

    private HashMap<String,ProjectTimer> existingProjects;
    private SessionTimer sessionTimer;
    private ProjectTimer oneProject;
    private LinkedHashMap<String, MojoTimer> mojoTiming;
    @Mock
    private Logger logger;
    private MojoExecution mojoExecution;
    private MavenProject project;
    private PrintWriter printWriter;

    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() throws Exception {
        existingProjects = new HashMap<String, ProjectTimer>();
        SystemClock mockClock = mock(SystemClock.class);
        when(mockClock.currentTimeMillis())
                .thenReturn(100L)
                .thenReturn(200L);

        sessionTimer = new SessionTimer(existingProjects, mockClock);

        mojoTiming = new LinkedHashMap<String, MojoTimer>();
        oneProject = new ProjectTimer(mojoTiming, mockClock);
        mojoExecution = createMojoExecution();
        project = createMavenProject();
        outputStream = new ByteArrayOutputStream();
        printWriter = new PrintWriter(outputStream);
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
        MojoTimer goal1Timer = new MojoTimer("artifactId:goal1", 1, 2);
        MojoTimer goal2Timer = new MojoTimer("artifactId:goal2", 1, 3);
        mojoTiming.put(goal1Timer.getName(), goal1Timer);
        mojoTiming.put(goal2Timer.getName(), goal2Timer);

        existingProjects.put("one", oneProject);

        sessionTimer.write(logger);

        String dividerLine = SessionTimer.DIVIDER;
        verify(logger).info("Build Time Summary:");
        verify(logger).info("");
        verify(logger).info("one");
        verify(logger).info("  artifactId:goal1 ......................................... [0.001s]");
        verify(logger).info("  artifactId:goal2 ......................................... [0.002s]");
        verify(logger).info(dividerLine);
    }

    @Test
    public void writeToOneProjectWithOnePlugin() {
        MojoTimer goal1Timer = new MojoTimer("artifactId:goal1", 1, 2);
        MojoTimer goal2Timer = new MojoTimer("artifactId:goal2", 1, 3);
        mojoTiming.put(goal1Timer.getName(), goal1Timer);
        mojoTiming.put(goal2Timer.getName(), goal2Timer);

        existingProjects.put("one", oneProject);

        sessionTimer.writeTo(printWriter);

        printWriter.flush();
        String output = outputStream.toString();
        String[] split = output.split("\r?\n");

        Assert.assertEquals(split[0], "\"one\";\"artifactId:goal1\";\"0.001\"");
        Assert.assertEquals(split[1], "\"one\";\"artifactId:goal2\";\"0.002\"");
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
