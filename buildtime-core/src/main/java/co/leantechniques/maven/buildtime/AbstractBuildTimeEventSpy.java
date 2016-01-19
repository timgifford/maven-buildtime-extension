package co.leantechniques.maven.buildtime;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;

@Component(role = EventSpy.class, hint = "timing")
public abstract class AbstractBuildTimeEventSpy extends AbstractEventSpy {

    @Requirement
    protected Logger logger;

    public AbstractBuildTimeEventSpy(Logger logger) {
        this.logger = logger;
    }

    public AbstractBuildTimeEventSpy() {
    }

    private final SessionTimer session = new SessionTimer();

    @Override
    public void init(Context context) throws Exception {
        super.init(context);
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
                doReport(event, session);
                break;

            default:
                //Ignore other events
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    protected abstract void doReport(ExecutionEvent event, SessionTimer session);
}