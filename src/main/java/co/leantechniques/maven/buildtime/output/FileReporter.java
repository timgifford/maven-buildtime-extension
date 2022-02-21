package co.leantechniques.maven.buildtime.output;

import co.leantechniques.maven.buildtime.MavenHelper;
import org.apache.maven.execution.ExecutionEvent;

import java.io.File;

public abstract class FileReporter implements Reporter {

    protected File getOutputFile(final ExecutionEvent event, final String fileProperty, final String defaultFileName) {
        String output = MavenHelper.getExecutionProperty(event, fileProperty, defaultFileName);
        if (null != output) {
            File file = new File(output);
            if (file.isAbsolute()) {
                file.getParentFile().mkdirs();
            } else {
                File parent = new File("target");
                parent.mkdirs();
                file = new File(parent, output);
            }
            return file;
        }
        return null;
    }
}
