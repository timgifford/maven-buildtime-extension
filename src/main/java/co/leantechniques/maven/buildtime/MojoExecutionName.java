package co.leantechniques.maven.buildtime;

import org.apache.maven.plugin.MojoExecution;

public class MojoExecutionName {
    private final String artifactId;
    private final String goal;

    public MojoExecutionName(MojoExecution mojoExecution) {
        this.artifactId = mojoExecution.getArtifactId();
        this.goal = mojoExecution.getGoal();
    }

    public String getName() {
        return String.format("%s:%s", artifactId, goal);
    }
}
