package co.leantechniques.maven.buildtime;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

public class SessionTimer {

    static final String DIVIDER = "------------------------------------------------------------------------";
    private Map<String, ProjectTimer> projects;
    private SystemClock systemClock;

    public SessionTimer() {
        this(new LinkedHashMap<String, ProjectTimer>(), new SystemClock());
    }

    public SessionTimer(Map<String, ProjectTimer> projects, SystemClock systemClock) {
        this.projects = projects;
        this.systemClock = systemClock;
    }

    public ProjectTimer getProject(MavenProject project) {
        return getProject(project.getArtifactId());
    }

    public ProjectTimer getProject(String projectArtifactId) {
        if(!projects.containsKey(projectArtifactId)) projects.put(projectArtifactId, new ProjectTimer(systemClock));
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

    public void mojoStarted(MavenProject project, MojoExecution mojoExecution) {
        getProject(project).startTimerFor(new MojoExecutionName(mojoExecution));
    }

    public void mojoSucceeded(MavenProject project, MojoExecution mojoExecution) {
        getProject(project).stopTimerFor(new MojoExecutionName(mojoExecution));
    }

    public void mojoFailed(MavenProject project, MojoExecution mojoExecution) {
        getProject(project).stopTimerFor(new MojoExecutionName(mojoExecution));
    }

    public MojoTimer getMojoTimer(MavenProject project, MojoExecution mojoExecution) {
        return getProject(project).getMojoTimer(new MojoExecutionName(mojoExecution));
    }
}
