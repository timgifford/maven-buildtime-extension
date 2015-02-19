package co.leantechniques.maven.buildtime;

import java.util.Locale;

import org.apache.maven.plugin.MojoExecution;

public class MojoExecutionName {

    private final String name;

    public MojoExecutionName(MojoExecution mojoExecution) {
        name = String.format(Locale.ENGLISH,
                "%s:%s (%s)",
                mojoExecution.getArtifactId(),
                mojoExecution.getGoal(),
                mojoExecution.getExecutionId());
    }

    public String getName() {
        return name;
    }
}
