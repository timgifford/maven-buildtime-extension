package co.leantechniques.maven.buildtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

public class ProjectTimer {

    private Map<String, MojoTimer> dataStore = new ConcurrentHashMap<String, MojoTimer>();
    private SystemClock systemClock;

    public ProjectTimer(Map<String, MojoTimer> dataStore, SystemClock systemClock) {
        this.dataStore = dataStore;
        this.systemClock = systemClock;
    }

    public ProjectTimer(SystemClock systemClock) {
        this(new ConcurrentHashMap<String, MojoTimer>(), systemClock);
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
