package co.leantechniques;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo( name = "timer", defaultPhase = LifecyclePhase.INITIALIZE)
public class BuildtimeMojo extends AbstractMojo {

    /**
     * Internal Maven's project
     *
     * @parameter default-value="${project}"
     * @readonly
     */
    protected MavenProject project;

    @Parameter( defaultValue="output.csv", property = "file", required = true)
    private File file;

    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private String outputDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info("OutputDirectory: " + outputDirectory);
    }
}
