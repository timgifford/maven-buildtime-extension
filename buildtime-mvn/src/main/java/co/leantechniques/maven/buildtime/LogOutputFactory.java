package co.leantechniques.maven.buildtime;

import org.apache.maven.execution.ExecutionEvent;
import org.slf4j.Logger;

public class LogOutputFactory {
    public LogOutput create(ExecutionEvent event, Logger logger) {
        boolean isEnabled = getIsEnabledFromPluginConfiguration(event);
        return new LogOutput(logger, isEnabled);
    }

    private boolean getIsEnabledFromPluginConfiguration(ExecutionEvent event) {
        return true;
//        return event.getProject().getPlugin("").getConfiguration();
    }
}
