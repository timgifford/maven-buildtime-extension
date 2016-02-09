package co.leantechniques.maven.buildtime.output;

import org.apache.maven.execution.ExecutionEvent;
import org.slf4j.Logger;

import co.leantechniques.maven.buildtime.SessionTimer;

public interface Reporter {
    void performReport(Logger logger, ExecutionEvent event, SessionTimer session);
}
