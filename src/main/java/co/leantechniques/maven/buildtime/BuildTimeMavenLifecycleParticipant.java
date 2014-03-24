package co.leantechniques.maven.buildtime;

import java.util.Properties;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.cli.ExecutionTimingExecutionListener;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

@Component(role = AbstractMavenLifecycleParticipant.class)
public class BuildTimeMavenLifecycleParticipant extends AbstractMavenLifecycleParticipant {
	private static final String STATSD_HOST_PROPERTY = "buildtime.statsd.host";
	private static final String STATSD_PORT_PROPERTY = "buildtime.statsd.port";
	private static final String STATSD_PREFIX         = "buildtime.statsd.prefix";
	
    @Requirement
    private Logger logger;
    
    private StatsDClient stats;

    @Override
    public void afterProjectsRead(MavenSession session) throws MavenExecutionException {    
    	stats = getStats(session.getTopLevelProject().getProperties());    	
    	afterProjectsRead(session, new ExecutionTimingExecutionListener(stats, logger));
    }

    void afterProjectsRead(MavenSession session, ExecutionTimingExecutionListener listener) {
        listener.registerListenerOn(session);
    }
}
