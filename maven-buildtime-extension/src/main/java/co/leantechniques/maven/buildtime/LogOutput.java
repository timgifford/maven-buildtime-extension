package co.leantechniques.maven.buildtime;

import org.slf4j.Logger;

public class LogOutput {

    private Logger logger;
    private boolean logToInfo;

    public LogOutput(Logger logger, boolean logToInfo) {
        this.logger = logger;
        this.logToInfo = logToInfo;
    }

    public void log(String string) {
        if (logToInfo) {
            logger.info(string);
        } else {
            logger.debug(string);
        }
    }
}
