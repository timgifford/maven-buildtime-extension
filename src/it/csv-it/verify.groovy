def buildLogText = new File(basedir, 'build.log').text
assert buildLogText.contains('BuildTimeEventSpy is registered.')

def buildTimeCsv =  new File(basedir, 'target/buildtime.csv')
assert buildTimeCsv.exists()
def buildTimeCsvText = buildTimeCsv.text

assert buildTimeCsvText ==~ $/(?m)"Module";"Mojo";"Time"\s*
"minimal-pom";"maven-compiler-plugin:testCompile \(default-testCompile\)";"\d{1,3}\.\d{3}"\s*
"minimal-pom";"maven-surefire-plugin:test \(default-test\)";"\d{1,3}\.\d{3}"\s*
"minimal-pom";"maven-jar-plugin:jar \(default-jar\)";"\d{1,3}\.\d{3}"\s*
"minimal-pom";"maven-compiler-plugin:compile \(default-compile\)";"\d{1,3}\.\d{3}"\s*
"minimal-pom";"maven-resources-plugin:resources \(default-resources\)";"\d{1,3}\.\d{3}"\s*
"minimal-pom";"maven-resources-plugin:testResources \(default-testResources\)";"\d{1,3}\.\d{3}"\s*
/$
