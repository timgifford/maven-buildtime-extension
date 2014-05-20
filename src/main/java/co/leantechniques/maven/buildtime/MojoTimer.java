package co.leantechniques.maven.buildtime;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;

public class MojoTimer {

    private String name;
    private long startTime = 0;
    private long endTime = 0;
    private SystemClock systemClock;

    public MojoTimer(String name, SystemClock systemClock) {
        this(name, 0,0, systemClock);
    }

    public MojoTimer(String name, long startTime, long endTime){
        this(name, startTime, endTime, new SystemClock());
    }

    public MojoTimer(String name, long startTime, long endTime, SystemClock systemClock) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.systemClock = systemClock;
    }

    public Long getDuration() {
        return endTime - startTime;
    }

    public String getName() {
        return name;
    }

    public void stop() {
        this.endTime = systemClock.currentTimeMillis();
    }

    public void start() {
        this.startTime = systemClock.currentTimeMillis();
    }

    public void write(Logger logger) {
        // 68 char width: coefficient-core .................................. SUCCESS [0.846s]
        logger.info(String.format("  %s %s [%.3fs]", getName(), StringUtils.repeat(".", calculateLineLength()), (double)getDuration()/1000));
    }

    private int calculateLineLength() {
        return 57 - name.length();
    }
}
