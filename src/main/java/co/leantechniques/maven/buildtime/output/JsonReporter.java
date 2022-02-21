package co.leantechniques.maven.buildtime.output;

import co.leantechniques.maven.buildtime.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.apache.maven.execution.ExecutionEvent;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonReporter extends FileReporter {
    @Override
    public void performReport(Logger logger, ExecutionEvent event, SessionTimer session) {
        if (Boolean.parseBoolean(MavenHelper.getExecutionProperty(event, Constants.BUILDTIME_OUTPUT_JSON_PROPERTY, "false"))) {
            // json output has been enabled
            File file = getOutputFile(event, Constants.BUILDTIME_OUTPUT_JSON_FILE_PROPERTY, Constants.BUILDTIME_OUTPUT_JSON_FILE);
            // as long as a file is defined, let's attempt to write to it
            if (null != file) {
                try (OutputStream stream = new FileOutputStream(file)) {
                    writeTo(session, stream, logger);
                } catch (IOException e) {
                    logger.error("Could not write report", e);
                }
            }
        }
    }

    /**
     * Write the session data out to the stream.
     *
     * @param session The session data to be written
     * @param stream The stream to write the session data to
     * @param logger The logger to use if there are issues
     */
    private void writeTo(SessionTimer session, OutputStream stream, Logger logger) {
        JsonReportVisitor visitor = new JsonReportVisitor();
        session.accept(visitor);
        Map<String, Object> statsMap = visitor.getStatsMap();
        Gson gson = new Gson();
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8))) {
            gson.toJson(statsMap, Map.class, writer);
        } catch (IOException e) {
            logger.error("Could not write report", e);
        }
    }

    private static class JsonReportVisitor extends AbstractTimerVisitor {
        private final Map<String, Object> statsMap = new ConcurrentHashMap<>();
        private static final String STEPS_LABEL = "steps";

        @Override
        public void visit(ProjectTimer projectTimer) {
            statsMap.put("projectName", projectTimer.getProjectName());
            statsMap.put("totalDuration", projectTimer.getDuration() / 1000d);
        }

        @Override
        public void visit(MojoTimer mojoTimer) {
            // if the steps label doesn't exist, create the element and initialize it to an empty map
            statsMap.computeIfAbsent(STEPS_LABEL, k -> new ConcurrentHashMap<String, Double>());
            // put the timer name as the key and the duration in seconds as the value
            ((ConcurrentHashMap<String, Double>) statsMap.get(STEPS_LABEL)).put(mojoTimer.getName(), mojoTimer.getDuration() / 1000d);
        }

        public Map<String, Object> getStatsMap() {
            return statsMap;
        }
    }
}
