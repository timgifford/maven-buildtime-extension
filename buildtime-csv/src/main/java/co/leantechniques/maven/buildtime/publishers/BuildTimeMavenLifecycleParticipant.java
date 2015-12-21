package co.leantechniques.maven.buildtime.publishers;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;

@Component(role = AbstractMavenLifecycleParticipant.class)
public class BuildTimeMavenLifecycleParticipant extends AbstractMavenLifecycleParticipant {
    @Requirement
    private Logger logger;

    @Override
    public void afterProjectsRead(MavenSession session) throws MavenExecutionException {
        afterProjectsRead(session, new ExecutionTimingExecutionListener(logger));
    }

    void afterProjectsRead(MavenSession session, ExecutionTimingExecutionListener listener) {
        listener.registerListenerOn(session);
    }
}
