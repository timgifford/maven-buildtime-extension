package co.leantechniques.maven.buildtime;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;

public class SessionTimer {

    private Map<String, ProjectTimer> projects;
    private SystemClock systemClock;

    public SessionTimer() {
        this(new ConcurrentHashMap<String, ProjectTimer>(), new SystemClock());
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

    public Map<String, ProjectTimer> getProjects() {
        return projects;
    }
}
