package org.apache.maven.cli;

import co.leantechniques.maven.buildtime.SessionTimer;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.logging.Logger;

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
        session.mojoStarted(event);
    }

	@Override
	public void mojoSucceeded(final ExecutionEvent event) {
        super.mojoSucceeded(event);
        session.mojoSucceeded(event);
    }

    @Override
    public void sessionEnded(ExecutionEvent event) {
        super.sessionEnded(event);
        session.write(logger);
    }

    public void registerListenerOn(MavenSession session) {
        session.getRequest().setExecutionListener(this);
    }
}
