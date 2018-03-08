package co.leantechniques.maven.buildtime;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;

public class SessionTimer {
    private ConcurrentMap<String, ProjectTimer> projects;
    private SystemClock systemClock;

    public SessionTimer() {
        this(new ConcurrentHashMap<String, ProjectTimer>(), new SystemClock());
    }

    public SessionTimer(ConcurrentMap<String, ProjectTimer> projects, SystemClock systemClock) {
        this.projects = projects;
        this.systemClock = systemClock;
    }

    public ProjectTimer getProject(MavenProject project) {
        return getProject(project.getArtifactId());
    }

    public ProjectTimer getProject(String projectArtifactId) {
        if (!projects.containsKey(projectArtifactId)) 
            projects.putIfAbsent(projectArtifactId, new ProjectTimer(projectArtifactId, systemClock));

        return projects.get(projectArtifactId);
    }

    public void accept(TimerVisitor visitor) {
        visitor.visit(this);

        final List<ProjectTimer> projectTimers = new ArrayList<ProjectTimer>(projects.values());
        Collections.sort(projectTimers);

        for (ProjectTimer projectTimer : projectTimers) {
            projectTimer.accept(visitor);
        }
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
