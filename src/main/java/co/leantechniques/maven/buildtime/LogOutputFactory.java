package co.leantechniques.maven.buildtime;

import co.leantechniques.maven.buildtime.publisher.CsvPublisher;
import co.leantechniques.maven.buildtime.publisher.MavenPublisher;
import org.apache.maven.execution.ExecutionEvent;
import org.slf4j.Logger;

public class LogOutputFactory {
    public LogOutput create(ExecutionEvent event, Logger logger) {
        return new LogOutput(logger, Boolean.parseBoolean(CsvPublisher.getExecutionProperty(event, MavenPublisher.BUILDTIME_OUTPUT_LOG, "false")));
    }
}
