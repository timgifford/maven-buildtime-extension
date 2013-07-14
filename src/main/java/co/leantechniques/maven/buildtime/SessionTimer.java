package co.leantechniques.maven.buildtime;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

public class SessionTimer {

    static final String DIVIDER = "------------------------------------------------------------------------";
    private Map<String, ProjectTimer> projects;

    public SessionTimer() {
        this(new LinkedHashMap<String, ProjectTimer>());
    }

    public SessionTimer(Map<String, ProjectTimer> projects) {
        this.projects = projects;
    }

    private ProjectTimer getProject(MavenProject project) {
        return getProject(project.getArtifactId());
    }

    public ProjectTimer getProject(String projectArtifactId) {
        if(!projects.containsKey(projectArtifactId)) projects.put(projectArtifactId, new ProjectTimer());
        return projects.get(projectArtifactId);
    }

    public void write(Logger logger) {
        logger.info("Build Time Summary:");
        logger.info("");
        for(String projectName : projects.keySet()) {
            logger.info(String.format("%s", projectName));
            ProjectTimer projectTimer = projects.get(projectName);
            projectTimer.write(logger);
        }
        logger.info(DIVIDER);
    }

    public void mojoStarted(ExecutionEvent event) {
        getProject(event.getProject()).startTimerFor(new MojoExecutionName(event.getMojoExecution()));
    }

    public void mojoSucceeded(ExecutionEvent event) {
        getProject(event.getProject()).stopTimerFor(new MojoExecutionName(event.getMojoExecution()));
    }
}
