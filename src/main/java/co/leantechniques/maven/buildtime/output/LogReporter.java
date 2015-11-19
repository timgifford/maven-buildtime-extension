package co.leantechniques.maven.buildtime.output;

import org.apache.maven.execution.ExecutionEvent;
import org.slf4j.Logger;

import co.leantechniques.maven.buildtime.Constants;
import co.leantechniques.maven.buildtime.LogOutput;
import co.leantechniques.maven.buildtime.MavenHelper;
import co.leantechniques.maven.buildtime.SessionTimer;

public class LogReporter implements Reporter {

    public void performReport(Logger logger, ExecutionEvent event, SessionTimer session) {
        LogOutput logOutput = new LogOutput(logger, Boolean.parseBoolean(MavenHelper.getExecutionProperty(event, Constants.BUILDTIME_OUTPUT_LOG, "false")));
        session.write(logOutput);
    }
}
