package co.leantechniques.maven.buildtime;

import java.io.PrintWriter;
import java.util.Locale;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;

public class MojoTimer {

    public static final int MAX_NAME_LENGTH = 58;
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
        logger.info(String.format(Locale.ENGLISH, "  %s [%.3fs]", getDisplayName(), (double)getDuration()/1000));
    }

    private String getDisplayName() {
        String truncatedName = name.length() >= MAX_NAME_LENGTH ? StringUtils.substring(name, 0, MAX_NAME_LENGTH) : name + " ";
        return StringUtils.rightPad(truncatedName, MAX_NAME_LENGTH, ".");
    }

    public void write(PrintWriter printWriter, String projectName) {
        printWriter.format(Locale.ENGLISH, "\"%s\";\"%s\";\"%.3f\"%n", projectName, name, (double)getDuration()/1000);
    }
}
