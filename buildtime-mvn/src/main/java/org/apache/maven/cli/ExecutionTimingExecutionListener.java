package org.apache.maven.cli;

import co.leantechniques.maven.buildtime.SessionTimer;
import co.leantechniques.maven.buildtime.publishers.MavenPublisher;
import org.apache.maven.cli.event.ExecutionEventLogger;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;

public class ExecutionTimingExecutionListener extends ExecutionEventLogger {
    private final Logger logger;
    private final SessionTimer session = new SessionTimer();

	public ExecutionTimingExecutionListener(final Logger logger) {
		super(logger);
		this.logger = logger;
	}

	@Override
	public void mojoStarted(final ExecutionEvent event) {
		super.mojoStarted(event);
        session.mojoStarted(event.getProject(), event.getMojoExecution());
    }

    @Override
    public void mojoFailed(final ExecutionEvent event) {
        super.mojoFailed(event);
        session.mojoFailed(event.getProject(), event.getMojoExecution());
    }

    @Override
	public void mojoSucceeded(final ExecutionEvent event) {
        super.mojoSucceeded(event);
        session.mojoSucceeded(event.getProject(), event.getMojoExecution());
    }

    @Override
    public void sessionEnded(final ExecutionEvent event) {
        super.sessionEnded(event);
        new MavenPublisher(event.getSession().getTopLevelProject(), logger).publish(event, session);
//        new CsvPublisher(logger).publish(event, session);
    }

    public void registerListenerOn(MavenSession session) {
        session.getRequest().setExecutionListener(this);
    }
}
