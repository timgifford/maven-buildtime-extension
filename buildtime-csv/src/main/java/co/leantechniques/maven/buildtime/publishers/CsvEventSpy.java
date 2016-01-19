package co.leantechniques.maven.buildtime.publishers;

import co.leantechniques.maven.buildtime.AbstractBuildTimeEventSpy;
import co.leantechniques.maven.buildtime.SessionTimer;
import org.apache.maven.execution.ExecutionEvent;

public class CsvEventSpy extends AbstractBuildTimeEventSpy {
    @Override
    protected void doReport(ExecutionEvent event, SessionTimer session) {
        new CsvPublisher(logger).publish(event, session);
    }
}
