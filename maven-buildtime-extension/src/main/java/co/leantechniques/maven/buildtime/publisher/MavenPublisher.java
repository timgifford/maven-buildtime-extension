package co.leantechniques.maven.buildtime.publisher;

import co.leantechniques.maven.buildtime.*;
import org.apache.maven.execution.ExecutionEvent;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;

import java.util.Locale;

public class MavenPublisher implements Publisher {
    public static final int MAX_NAME_LENGTH = 58;
    public static final String DIVIDER = "------------------------------------------------------------------------";
    public static final String BUILDTIME_OUTPUT_LOG = "buildtime.output.log";

    private Logger logger;
    private LogOutputFactory logOutputFactory;

    public MavenPublisher(Logger logger) {
        this(logger, new LogOutputFactory());
    }

    public MavenPublisher(Logger logger, LogOutputFactory logOutputFactory) {
        this.logger = logger;
        this.logOutputFactory = logOutputFactory;
    }

    public void publish(ExecutionEvent event, SessionTimer session) {
        LogOutput logOutput = logOutputFactory.create(event, logger);
        logOutput.log("Build Time Summary:");
        logOutput.log("");
        for(String projectName : session.getProjects().keySet()) {
            logOutput.log(String.format("%s", projectName));
            ProjectTimer projectTimer = session.getProject(projectName);

            for (MojoTimer mojo : projectTimer.getDataStore().values()){
                logOutput.log(getFormattedMojoTimeLine(mojo));
            }
        }
        logOutput.log(DIVIDER);
    }

    public static String getFormattedMojoTimeLine(MojoTimer mojo) {
        return String.format(Locale.ENGLISH, "  %s [%.3fs]", getDisplayName(mojo.getName()), (double) mojo.getDuration() / 1000);
    }

    public static String getDisplayName(String name) {
        String truncatedName = name.length() >= MAX_NAME_LENGTH ? StringUtils.substring(name, 0, MAX_NAME_LENGTH) : name + " ";
        return StringUtils.rightPad(truncatedName, MAX_NAME_LENGTH, ".");
    }
}
