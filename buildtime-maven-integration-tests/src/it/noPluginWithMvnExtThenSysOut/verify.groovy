import java.io.*

boolean buildSummaryFound=false
boolean buildIsSuccess = true
new File(basedir, "build.log").eachLine {
    line ->
        if (line.startsWith("[INFO] Build Time Summary:")) {
            buildSummaryFound=true
        }
        if (line.contains("BUILD SUCCESS")){
            buildIsSuccess = true
        }
}

/*
[INFO] Build Time Summary:
[INFO]
[INFO] simple-it
[INFO]   maven-clean-plugin:clean (default-clean) ................. [0.093s]
[INFO]   maven-compiler-plugin:testCompile (default-testCompile) .. [0.009s]
[INFO]   maven-compiler-plugin:compile (default-compile) .......... [0.351s]
[INFO]   maven-resources-plugin:resources (default-resources) ..... [0.212s]
[INFO]   maven-resources-plugin:testResources (default-testResource [0.006s]
*/

assert (buildSummaryFound && buildIsSuccess):"The build should have succeeded, but it failed."