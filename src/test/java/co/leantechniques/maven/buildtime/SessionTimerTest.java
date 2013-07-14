package co.leantechniques.maven.buildtime;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static junit.framework.Assert.*;

public class SessionTimerTest {

    private HashMap<String,ProjectTimer> existingProjects;
    private SessionTimer sessionTimer;
    private ProjectTimer oneProject;
    private LinkedHashMap<String, MojoTimer> mojoTiming;

    @Before
    public void setUp() throws Exception {
        existingProjects = new HashMap<String, ProjectTimer>();
        sessionTimer = new SessionTimer(existingProjects);
        mojoTiming = new LinkedHashMap<String, MojoTimer>();
        oneProject = new ProjectTimer(mojoTiming);
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

        FakeLogger fakeLogger = new FakeLogger();
        sessionTimer.write(fakeLogger);

        String newLine = "\n";

        String dividerLine = SessionTimer.DIVIDER;
        assertEquals("Build Time Summary:" + newLine +
                     newLine +
                     "one" + newLine +
                     "  artifactId:goal1 [1]" + newLine +
                     "  artifactId:goal2 [2]" + newLine +
                     dividerLine

                , fakeLogger.output());
    }


}
