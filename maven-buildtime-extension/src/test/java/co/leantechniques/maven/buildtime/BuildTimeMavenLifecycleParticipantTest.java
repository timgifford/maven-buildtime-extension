package co.leantechniques.maven.buildtime;

import org.apache.maven.cli.ExecutionTimingExecutionListener;
import org.apache.maven.execution.MavenSession;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BuildTimeMavenLifecycleParticipantTest {
    @Test
    public void registerWithMavenSessionAfterProjectRead() {
        MavenSession session = mock(MavenSession.class);
        ExecutionTimingExecutionListener listener = mock(ExecutionTimingExecutionListener.class);
        new BuildTimeMavenLifecycleParticipant().afterProjectsRead(session, listener);

        verify(listener).registerListenerOn(session);
    }
}
