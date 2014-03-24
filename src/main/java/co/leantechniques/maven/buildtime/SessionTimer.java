package co.leantechniques.maven.buildtime;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.StatsDClient;

public class SessionTimer {

    static final String DIVIDER = "------------------------------------------------------------------------";
    private Map<String, ProjectTimer> projects;
    private StatsDClient stats;

    public SessionTimer() {
        this(new LinkedHashMap<String, ProjectTimer>(), null);
    }
    
    public SessionTimer(StatsDClient s) {
    	this(new LinkedHashMap<String, ProjectTimer>(), s);
    }
    
    public SessionTimer(Map<String, ProjectTimer> projects) {
    	this(projects, null);
    }

    public SessionTimer(Map<String, ProjectTimer> projects, StatsDClient s) {
        this.projects = projects; 
        this.stats = s == null ? new NoOpStatsDClient() : s;
    }

    private ProjectTimer getProject(MavenProject project) {
        return getProject(project.getArtifactId());
    }

    public ProjectTimer getProject(String projectArtifactId) {
    	String prefix = projectArtifactId.replaceAll("\\.", "_");
        if(!projects.containsKey(projectArtifactId)) projects.put(projectArtifactId, new ProjectTimer(stats, prefix));
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
