package co.leantechniques.maven.buildtime;

import org.codehaus.plexus.logging.Logger;
import java.util.LinkedHashMap;

public class ProjectTimer {

    private LinkedHashMap<String, MojoTimer> dataStore = new LinkedHashMap<String, MojoTimer>();
    private SystemClock systemClock;

    public ProjectTimer(LinkedHashMap<String, MojoTimer> dataStore, SystemClock systemClock) {
        this.dataStore = dataStore;
        this.systemClock = systemClock;
    }

    public ProjectTimer(SystemClock systemClock) {
        this(new LinkedHashMap<String, MojoTimer>(), systemClock);
    }

    public void write(Logger logger) {
        for (MojoTimer mojo : dataStore.values()){
            mojo.write(logger);
        }
    }

    public void stopTimerFor(MojoExecutionName me) {
        getMojoTimer(me).stop();
    }

    public void startTimerFor(MojoExecutionName name) {
        getMojoTimer(name).start();
    }

    public MojoTimer getMojoTimer(MojoExecutionName name) {
        if(!dataStore.containsKey(name.getName()))
            dataStore.put(name.getName(), new MojoTimer(name.getName(),systemClock));
        return dataStore.get(name.getName());
    }
}
