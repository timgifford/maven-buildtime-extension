package org.apache.maven.cli;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;

import co.leantechniques.maven.buildtime.SessionTimer;
import co.leantechniques.maven.buildtime.output.CsvReporter;
import co.leantechniques.maven.buildtime.output.LogReporter;
import co.leantechniques.maven.buildtime.output.Reporter;

@Component(role = EventSpy.class, hint = "timing")
public class BuildTimeEventSpy extends AbstractEventSpy {

    @Requirement
    private Logger logger;

    private List<Reporter> reporters;

    public BuildTimeEventSpy(Logger logger, Reporter... reporters) {
        this.logger = logger;
        this.reporters = Arrays.asList(reporters);
    }

    public BuildTimeEventSpy() {
        reporters = Arrays.asList(new LogReporter(), new CsvReporter());
    }

    private final SessionTimer session = new SessionTimer();

    @Override
    public void init(Context context) throws Exception {
        super.init(context);
        logger.info("BuildTimeEventSpy is registered.");
    }

    @Override
    public void onEvent(Object event) throws Exception {
        if (event instanceof ExecutionEvent) {
            onEvent((ExecutionEvent) event);
        }
    }

    private void onEvent(ExecutionEvent event) throws Exception {
        switch (event.getType()) {
        case MojoStarted:
            session.mojoStarted(event.getProject(), event.getMojoExecution());
            break;

        case MojoFailed:
            session.mojoFailed(event.getProject(), event.getMojoExecution());
            break;

        case MojoSucceeded:
            session.mojoSucceeded(event.getProject(), event.getMojoExecution());
            break;

        case SessionEnded:
            doReport(event);
            break;

        default:
            //Ignore other events
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }


    private void doReport(ExecutionEvent event) {
        for (Reporter reporter : reporters) {
            reporter.performReport(logger, event, session);
        }
    }
}
