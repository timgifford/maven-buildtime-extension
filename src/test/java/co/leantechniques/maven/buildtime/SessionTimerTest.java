package co.leantechniques.maven.buildtime;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionTimerTest {

    private HashMap<String,ProjectTimer> existingProjects;
    private SessionTimer sessionTimer;
    private ProjectTimer oneProject;
    private LinkedHashMap<String, MojoTimer> mojoTiming;
    private FakeLogger fakeLogger;
    private MojoExecution mojoExecution;
    private MavenProject project;

    @Before
    public void setUp() throws Exception {
        fakeLogger = new FakeLogger();
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

        sessionTimer.write(fakeLogger);

        String newLine = "\n";

        String dividerLine = SessionTimer.DIVIDER;
        assertEquals("Build Time Summary:" + newLine +
                     newLine +
                     "one" + newLine +
                     "  artifactId:goal1 ......................................... [0.001s]" + newLine +
                     "  artifactId:goal2 ......................................... [0.002s]" + newLine +
                     dividerLine

                , fakeLogger.output());
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
