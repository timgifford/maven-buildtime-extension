package co.leantechniques.maven.buildtime;

import java.util.Properties;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.cli.ExecutionTimingExecutionListener;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;

import com.timgroup.statsd.NoOpStatsDClient;
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
    
    @Override
    public void afterSessionEnd(MavenSession session) throws MavenExecutionException {
    	stats.stop();
    	super.afterSessionEnd(session);
    }
    
    private StatsDClient getStats(Properties props) {
    	String host = props.getProperty(STATSD_HOST_PROPERTY);
    	String port = props.getProperty(STATSD_PORT_PROPERTY);
    	String prefix = props.getProperty(STATSD_PREFIX);
    	
    	if(StringUtils.isNotBlank(host)) {
	        return new NonBlockingStatsDClient(
	        		prefix == null ? "buildtime" : prefix, 
	        		host, 
	        		port == null ? 8125 : Integer.valueOf(port));
    	}
    	
    	return new NoOpStatsDClient();
    }
}
