package co.leantechniques.maven.buildtime;

import org.codehaus.plexus.logging.Logger;
import java.util.LinkedHashMap;

public class ProjectTimer {

    private LinkedHashMap<String, MojoTimer> dataStore = new LinkedHashMap<String, MojoTimer>();

    public ProjectTimer() {
        this(new LinkedHashMap<String, MojoTimer>());
    }

    public ProjectTimer(LinkedHashMap<String, MojoTimer> dataStore) {
        this.dataStore = dataStore;
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

    private MojoTimer getMojoTimer(MojoExecutionName name) {
        if(!dataStore.containsKey(name.getName()))
            dataStore.put(name.getName(), new MojoTimer(name.getName()));
        return dataStore.get(name.getName());
    }
}
