import java.io.File;

public class Check {

    public static boolean fileExists(File basedir, String filename){
        File defaultFilename = new File( basedir, filename );
        return defaultFilename.isFile();
    }

}
