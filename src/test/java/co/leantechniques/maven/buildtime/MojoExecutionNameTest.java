package co.leantechniques.maven.buildtime;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MojoExecutionNameTest {

    @Test
    public void getName() throws Exception {
        Plugin plugin = new Plugin();
        plugin.setArtifactId("artifact");
        MojoExecution execution = new MojoExecution(plugin, "goal", "id");

        MojoExecutionName executionName = new MojoExecutionName(execution);

        assertEquals("artifact:goal (id)", executionName.getName());
    }
}