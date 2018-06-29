package co.leantechniques.maven.buildtime;

import java.util.Properties;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;

public class MavenHelper {
    private MavenHelper(){}

    public static String getExecutionProperty(final ExecutionEvent event, final String property, final String def) {
        MavenSession mavenSession = event.getSession();
        Properties systemProperties = mavenSession.getSystemProperties();
        Properties userProperties = mavenSession.getUserProperties();
		Properties projectProperties = mavenSession.getTopLevelProject() == null ? null : mavenSession.getTopLevelProject().getProperties();
        String output = userProperties.getProperty(property);
        output = output == null ? projectProperties != null ? projectProperties.getProperty(property) : null : output;
        output = output == null ? systemProperties.getProperty(property) : output;
        return output == null ? def : output;
    }
}
