package co.leantechniques.maven.buildtime;

import co.leantechniques.maven.buildtime.publishers.MavenPublisher;
import org.apache.maven.execution.ExecutionEvent;

public class ConsoleEventSpy extends AbstractBuildTimeEventSpy {
    @Override
    protected void doReport(ExecutionEvent event, SessionTimer session) {
        new MavenPublisher(event.getSession().getTopLevelProject(), logger).publish(event, session);
    }
}
