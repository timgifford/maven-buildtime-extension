package co.leantechniques.maven.buildtime.output;

import java.util.Locale;

import org.apache.maven.execution.ExecutionEvent;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;

import co.leantechniques.maven.buildtime.AbstractTimerVisitor;
import co.leantechniques.maven.buildtime.Constants;
import co.leantechniques.maven.buildtime.LogOutput;
import co.leantechniques.maven.buildtime.MavenHelper;
import co.leantechniques.maven.buildtime.MojoTimer;
import co.leantechniques.maven.buildtime.ProjectTimer;
import co.leantechniques.maven.buildtime.SessionTimer;

public class LogReporter implements Reporter {

    public static final int MAX_NAME_LENGTH = 58;

    public static final String DIVIDER = "------------------------------------------------------------------------";

    public void performReport(Logger logger, ExecutionEvent event, SessionTimer session) {
        LogOutput logOutput = new LogOutput(logger, Boolean.parseBoolean(
                MavenHelper.getExecutionProperty(event, Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "false")));

        session.accept(new LogReportVisitor(logOutput));
    }

    public static class LogReportVisitor extends AbstractTimerVisitor {

        private LogOutput logOutput;

        public LogReportVisitor(LogOutput logOutput) {
            this.logOutput = logOutput;
        }

        @Override
        public void visit(SessionTimer sessionTimer) {
            logOutput.log(DIVIDER);
            logOutput.log("Build Time Summary:");
            logOutput.log(DIVIDER);
        }

        @Override
        public void visit(ProjectTimer projectTimer) {
            logOutput.log(String.format("%s [%.3fs]", projectTimer.getProjectName(), projectTimer.getDuration() / 1000d));
        }

        @Override
        public void visit(MojoTimer mojoTimer) {
            // 68 char width: coefficient-core .................................. SUCCESS [0.846s]
            logOutput.log(String.format(Locale.ENGLISH, "  %s [%.3fs]",
                    getDisplayName(mojoTimer.getName()), mojoTimer.getDuration() / 1000d));
        }

        private String getDisplayName(String name) {
            String truncatedName = name.length() >= MAX_NAME_LENGTH ?
                    StringUtils.substring(name, 0, MAX_NAME_LENGTH) : name + " ";
            return StringUtils.rightPad(truncatedName, MAX_NAME_LENGTH, ".");
        }
    }
}
