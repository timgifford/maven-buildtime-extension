package co.leantechniques.maven.buildtime;

import org.codehaus.plexus.logging.Logger;

public class MojoTimer {

    private String name;
    private long startTime = 0;
    private long endTime = 0;

    public MojoTimer(String name) {
        this(name, 0,0);
    }

    public MojoTimer(String name, long startTime, long endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getDuration() {
        return endTime - startTime;
    }

    public String getName() {
        return name;
    }

    public void stop() {
        this.endTime = System.currentTimeMillis();
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void write(Logger logger) {
        logger.info(String.format("  %s [%s]",getName(), getDuration()));
    }
}
