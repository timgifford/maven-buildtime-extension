package co.leantechniques;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo( name = "timer", defaultPhase = LifecyclePhase.INITIALIZE)
public class BuildtimeMojo extends AbstractMojo {


    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private String outputDirectory;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.printf("OutputDirectory: %s", outputDirectory);
    }
}
