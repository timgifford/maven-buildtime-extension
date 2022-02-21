package co.leantechniques.maven.buildtime;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MojoExecutionNameTest {

    @Test
    void getName() {
        Plugin plugin = new Plugin();
        plugin.setArtifactId("artifact");
        MojoExecution execution = new MojoExecution(plugin, "goal", "id");

        MojoExecutionName executionName = new MojoExecutionName(execution);

        assertEquals("artifact:goal (id)", executionName.getName());
    }
}