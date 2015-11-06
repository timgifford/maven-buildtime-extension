package org.apache.maven.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.maven.cli.event.ExecutionEventLogger;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;

import co.leantechniques.maven.buildtime.LogOutput;
import co.leantechniques.maven.buildtime.SessionTimer;

public class ExecutionTimingExecutionListener extends ExecutionEventLogger {

    public static final String BUILDTIME_OUTPUT_CSV_FILE_PROPERTY = "buildtime.output.csv.file";
    public static final String BUILDTIME_OUTPUT_CSV_FILE = "buildtime.csv";
    public static final String BUILDTIME_OUTPUT_CSV_PROPERTY = "buildtime.output.csv";
    public static final String BUILDTIME_OUTPUT_LOG = "buildtime.output.log";

    private final Logger logger;
    private final SessionTimer session = new SessionTimer();

	public ExecutionTimingExecutionListener(final Logger logger) {
		super(logger);
		this.logger = logger;
	}

	@Override
	public void mojoStarted(final ExecutionEvent event) {
		super.mojoStarted(event);
        session.mojoStarted(event.getProject(), event.getMojoExecution());
    }

    @Override
    public void mojoFailed(final ExecutionEvent event) {
        super.mojoFailed(event);
        session.mojoFailed(event.getProject(), event.getMojoExecution());
    }

    @Override
	public void mojoSucceeded(final ExecutionEvent event) {
        super.mojoSucceeded(event);
        session.mojoSucceeded(event.getProject(), event.getMojoExecution());
    }

    @Override
    public void sessionEnded(final ExecutionEvent event) {
        super.sessionEnded(event);
        LogOutput logOutput = new LogOutput(logger, Boolean.parseBoolean(getExecutionProperty(event, BUILDTIME_OUTPUT_LOG, "false")));
        session.write(logOutput);
        csvOutput(event);

    }

    private void csvOutput(final ExecutionEvent event) {
        if (Boolean.parseBoolean(getExecutionProperty(event, BUILDTIME_OUTPUT_CSV_PROPERTY, "false"))) {
            File file = getOutputFile(event);
            if (file != null) {
                PrintWriter printWriter = null;
                try {
                    printWriter = new PrintWriter(file);
                    writeTo(printWriter);
                } catch (FileNotFoundException e) {
                    logger.error("Could not write report", e);
                } finally {
                    if (printWriter != null) {
                        printWriter.close();
                    }
                }
            }
        }
    }

    private File getOutputFile(final ExecutionEvent event) {
        String output = getExecutionProperty(event, BUILDTIME_OUTPUT_CSV_FILE_PROPERTY, BUILDTIME_OUTPUT_CSV_FILE);
        if (output != null) {
            File file = new File(output);
            if (file.isAbsolute()) {
                file.getParentFile().mkdirs();
            } else {
                file = new File(new File("target"), output);
            }
            return file;
        }
        return null;
    }

    private void writeTo(PrintWriter printWriter) {
        printWriter.println("\"Module\";\"Mojo\";\"Time\"");
        session.writeTo(printWriter);
    }

    private String getExecutionProperty(final ExecutionEvent event, final String property, final String def) {
        MavenSession mavenSession = event.getSession();
        Properties systemProperties = mavenSession.getSystemProperties();
        Properties userProperties = mavenSession.getUserProperties();
        String output = userProperties.getProperty(property);
        output = output == null ? systemProperties.getProperty(property) : output;
        return output == null ? def : output;
    }

    public void registerListenerOn(MavenSession session) {
        session.getRequest().setExecutionListener(this);
    }
}
