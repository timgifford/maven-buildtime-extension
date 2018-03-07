package co.leantechniques.maven.buildtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProjectTimer {

    private final String projectName;

    private final ConcurrentMap<String, MojoTimer> dataStore;
    private final SystemClock systemClock;

    public ProjectTimer(String projectName, ConcurrentMap<String, MojoTimer> dataStore, SystemClock systemClock) {
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
        if(!dataStore.containsKey(name.getName())) {
            dataStore.putIfAbsent(name.getName(), new MojoTimer(projectName, name.getName(), systemClock));
        }
        return dataStore.get(name.getName());
    }

    public Long getDuration() {
        long startTime = Long.MAX_VALUE;
        long endTime = 0;

        for (MojoTimer mojoTimer : dataStore.values()) {
            startTime = Math.min(startTime, mojoTimer.getStartTime());
            endTime = Math.max(endTime, mojoTimer.getEndTime());
        }

        return endTime - startTime;
    }

    public void accept(TimerVisitor visitor){
        visitor.visit(this);

        final List<MojoTimer> mojoTimers = new ArrayList<MojoTimer>(dataStore.values());
        Collections.sort(mojoTimers);

        for (MojoTimer mojoTimer : mojoTimers) {
            mojoTimer.accept(visitor);
        }
    }

    public String getProjectName() {
        return projectName;
    }
}
