package co.leantechniques.maven.buildtime.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Locale;

import org.apache.maven.execution.ExecutionEvent;
import org.slf4j.Logger;

import co.leantechniques.maven.buildtime.AbstractTimerVisitor;
import co.leantechniques.maven.buildtime.Constants;
import co.leantechniques.maven.buildtime.MavenHelper;
import co.leantechniques.maven.buildtime.MojoTimer;
import co.leantechniques.maven.buildtime.SessionTimer;

public class CsvReporter implements Reporter {

    private File getOutputFile(final ExecutionEvent event) {
        String output = MavenHelper.getExecutionProperty(event, Constants.BUILDTIME_OUTPUT_CSV_FILE_PROPERTY, Constants.BUILDTIME_OUTPUT_CSV_FILE);
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

    public void performReport(Logger logger, ExecutionEvent event, SessionTimer session) {
        if (Boolean.parseBoolean(MavenHelper.getExecutionProperty(event, Constants.BUILDTIME_OUTPUT_CSV_PROPERTY, "false"))) {
            File file = getOutputFile(event);
            if (file != null) {
                PrintWriter printWriter = null;
                try {
                    printWriter = new PrintWriter(file);
                    writeTo(session, printWriter);
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

    public void writeTo(SessionTimer session, PrintWriter printWriter) {
        printWriter.println("\"Module\";\"Mojo\";\"Time\"");
        session.accept(new CsvReportVisitor(printWriter));
    }

    public static class CsvReportVisitor extends AbstractTimerVisitor {
        private PrintWriter printWriter;

        public CsvReportVisitor(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        @Override
        public void visit(MojoTimer mojoTimer) {
            printWriter.format(Locale.ENGLISH, "\"%s\";\"%s\";\"%.3f\"%n",
                    mojoTimer.getProjectName(), mojoTimer.getName(), mojoTimer.getDuration() / 1000d);
        }
    }
}
