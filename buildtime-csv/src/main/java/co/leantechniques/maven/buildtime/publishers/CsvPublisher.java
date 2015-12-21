package co.leantechniques.maven.buildtime.publishers;

import co.leantechniques.maven.buildtime.MojoTimer;
import co.leantechniques.maven.buildtime.ProjectTimer;
import co.leantechniques.maven.buildtime.SessionTimer;
import co.leantechniques.maven.buildtime.publishers.Publisher;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class CsvPublisher implements Publisher {
    public static final String BUILDTIME_OUTPUT_CSV_FILE_PROPERTY = "buildtime.output.csv.file";
    public static final String BUILDTIME_OUTPUT_CSV_FILE = "buildtime.csv";
    public static final String BUILDTIME_OUTPUT_CSV_PROPERTY = "buildtime.output.csv";

    private final Logger logger;

    public CsvPublisher(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void publish(ExecutionEvent event, SessionTimer session) {
//        if (Boolean.parseBoolean(CsvPublisher.getExecutionProperty(event, BUILDTIME_OUTPUT_CSV_PROPERTY, "false"))) {
            outpuCsv(event, session);
//        }
    }

    private void outpuCsv(ExecutionEvent event, SessionTimer session) {
        File file = getOutputFile(event);
        if (file != null) {
            PrintWriter printWriter = null;
            try {
                printWriter = new PrintWriter(file);
                writeTo(printWriter, session);
            } catch (FileNotFoundException e) {
                logger.error("Could not write report", e);
            } finally {
                if (printWriter != null) {
                    printWriter.close();
                }
            }
        }
    }

    private static File getOutputFile(final ExecutionEvent event) {
//        String output = getExecutionProperty(event, BUILDTIME_OUTPUT_CSV_FILE_PROPERTY, BUILDTIME_OUTPUT_CSV_FILE);
        List<Plugin> buildPlugins = event.getSession().getTopLevelProject().getBuildPlugins();
        Xpp3Dom configuration;
        String fileName = "output.csv";
        for (Plugin plugin : buildPlugins){
            if(plugin.getArtifactId().equalsIgnoreCase("buildtime-maven-plugin")){
                fileName = ((Xpp3Dom)plugin.getConfiguration()).getChildren("file")[0].getValue();
                break;
            }
        }

        String output = fileName;
        if (output != null) {
            File file = new File(event.getSession().getTopLevelProject().getBasedir(), output);
            if (file.isAbsolute()) {
                file.getParentFile().mkdirs();
            } else {
                file = new File(new File("target"), output);
            }
            return file;
        }
        return null;
    }

    public void writeTo(PrintWriter printWriter, SessionTimer session) {
        printWriter.println("\"Module\";\"Mojo\";\"Time\"");
        for(String projectName : session.getProjects().keySet()) {
            ProjectTimer projectTimer = session.getProject(projectName);
            for (MojoTimer mojo : projectTimer.getDataStore().values()){
                printWriter.format(Locale.ENGLISH, "\"%s\";\"%s\";\"%.3f\"%n", projectName, mojo.getName(), (double)mojo.getDuration()/1000);
            }
        }
    }

    public static String getExecutionProperty(final ExecutionEvent event, final String property, final String def) {
        MavenSession mavenSession = event.getSession();
        Properties systemProperties = mavenSession.getSystemProperties();
        Properties userProperties = mavenSession.getUserProperties();
        String output = userProperties.getProperty(property);
        output = output == null ? systemProperties.getProperty(property) : output;
        return output == null ? def : output;
    }

}
