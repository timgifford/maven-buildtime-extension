package co.leantechniques.maven.buildtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectTimer {

    private final String projectName;

    private final Map<String, MojoTimer> dataStore;
    private final SystemClock systemClock;

    public ProjectTimer(String projectName, Map<String, MojoTimer> dataStore, SystemClock systemClock) {
        this.dataStore = dataStore;
        this.systemClock = systemClock;
        this.projectName = projectName;
    }

    public ProjectTimer(String projectName, SystemClock systemClock) {
        this(projectName, new ConcurrentHashMap<String, MojoTimer>(), systemClock);
    }

    public void stopTimerFor(MojoExecutionName me) {
        getMojoTimer(me).stop();
    }

    public void startTimerFor(MojoExecutionName name) {
        getMojoTimer(name).start();
    }

    public MojoTimer getMojoTimer(MojoExecutionName name) {
        if(!dataStore.containsKey(name.getName()))
            dataStore.put(name.getName(), new MojoTimer(projectName, name.getName(),systemClock));
        return dataStore.get(name.getName());
    }

    public void accept(TimerVisitor visitor){
        visitor.visit(this);
        for (MojoTimer mojoTimer : dataStore.values()) {
            mojoTimer.accept(visitor);
        }
    }

    public String getProjectName() {
        return projectName;
    }
}
