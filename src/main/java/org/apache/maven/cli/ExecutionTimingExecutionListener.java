package org.apache.maven.cli;

import java.util.Date;

import org.apache.maven.cli.event.ExecutionEventLogger;
import org.apache.maven.execution.BuildSummary;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;

import co.leantechniques.maven.buildtime.SessionTimer;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.StatsDClient;

public class ExecutionTimingExecutionListener extends ExecutionEventLogger {

	private final Logger logger;
    private final SessionTimer session;
    
    private final StatsDClient stats;
   
	public ExecutionTimingExecutionListener(final Logger logger) {
		this(null, logger);
	}
	
	public ExecutionTimingExecutionListener(final StatsDClient stats, final Logger logger) {
		super(logger);
		this.logger = logger;	
		this.stats = stats == null ? new NoOpStatsDClient() : stats;
		
		session = new SessionTimer(stats);
	}	

	@Override
	public void mojoStarted(final ExecutionEvent event) {
		super.mojoStarted(event);
        session.mojoStarted(event);
    }

	@Override
	public void mojoSucceeded(final ExecutionEvent event) {
        super.mojoSucceeded(event);
        session.mojoFinished(event);
    }
	
	@Override
	public void mojoFailed(ExecutionEvent event) {
		super.mojoFailed(event);
		session.mojoFinished(event);
	}

    @Override
    public void sessionEnded(ExecutionEvent event) {
        super.sessionEnded(event);
        
        recordTotalTime(event);
        
        session.write(logger);     
    }
    
    private void recordTotalTime(ExecutionEvent e) {
        Date finish = new Date();
        
        MavenSession mavenbuild = e.getSession();
        
        // Total build timing
		int totalTime = (int) (finish.getTime() - mavenbuild.getRequest().getStartTime().getTime());                     
        stats.time("_total", totalTime);        
                
        // Per module build timing
        MavenExecutionResult result = mavenbuild.getResult();
        for (MavenProject project : mavenbuild.getProjects()) {
            BuildSummary buildSummary = result.getBuildSummary(project);
            if(buildSummary != null) {
            	String prefix = project.getArtifactId().replaceAll("\\.", "_");            	
            	stats.time(prefix + "._total", (int) buildSummary.getTime());  
            }
        }        
    }

    public void registerListenerOn(MavenSession session) {
        session.getRequest().setExecutionListener(this);
    }
}
