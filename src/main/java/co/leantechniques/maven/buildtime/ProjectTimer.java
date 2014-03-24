package co.leantechniques.maven.buildtime;

import org.codehaus.plexus.logging.Logger;
import java.util.LinkedHashMap;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.StatsDClient;

public class ProjectTimer {

    private LinkedHashMap<String, MojoTimer> dataStore = new LinkedHashMap<String, MojoTimer>();
    
    private StatsDClient stats;
    private String statPrefix;
    
    public ProjectTimer() {
        this(new LinkedHashMap<String, MojoTimer>(), null, null);
    }
    
    public ProjectTimer(StatsDClient s, String prefix) {
    	this(new LinkedHashMap<String, MojoTimer>(), s, prefix);
    }    

    public ProjectTimer(LinkedHashMap<String, MojoTimer> dataStore) {
        this(dataStore, null, null);
    }
    
    public ProjectTimer(LinkedHashMap<String, MojoTimer> dataStore, StatsDClient s, String statPrefix) {
        this.dataStore = dataStore;
        this.stats = s == null ? new NoOpStatsDClient() : s;
        this.statPrefix = statPrefix == null ? "" : statPrefix;
    }    

    public void write(Logger logger) {
        for (MojoTimer mojo : dataStore.values()){
        	String metricName = statPrefix + "." + mojo.getName().replaceAll(":", ".");
           	stats.time(metricName, mojo.getDuration().intValue());           
            mojo.write(logger);
        }
    }

    public void stopTimerFor(MojoExecutionName me) {
        getMojoTimer(me).stop();
    }

    public void startTimerFor(MojoExecutionName name) {
        getMojoTimer(name).start();
    }

    private MojoTimer getMojoTimer(MojoExecutionName name) {
        if(!dataStore.containsKey(name.getName()))
            dataStore.put(name.getName(), new MojoTimer(name.getName()));
        return dataStore.get(name.getName());
    }
}
