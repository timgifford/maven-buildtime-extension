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

File defaultFilename = new File( basedir, "output.csv" );

assert !defaultFilename.isFile(): "output.csv exists, but should NOT exist."
assert !buildSummaryFound: "Expected no output, but the build time summary was printed."
assert buildIsSuccess: "The build failed for an unknown reason."