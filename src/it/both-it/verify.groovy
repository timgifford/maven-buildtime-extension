def buildLogText = new File(basedir, 'build.log').text
assert buildLogText.contains('BuildTimeEventSpy is registered.')
assert buildLogText.contains('maven-compiler-plugin:testCompile (default-testCompile) ..')
assert buildLogText.contains('maven-surefire-plugin:test (default-test) ................')
assert buildLogText.contains('maven-jar-plugin:jar (default-jar) .......................')
assert buildLogText.contains('maven-compiler-plugin:compile (default-compile) ..........')
assert buildLogText.contains('maven-resources-plugin:resources (default-resources) .....')
assert buildLogText.contains('maven-resources-plugin:testResources (default-testResource')

def buildTimeCsv =  new File(basedir, 'target/buildtime.csv')
assert buildTimeCsv.exists()
def buildTimeCsvText = buildTimeCsv.text

assert buildTimeCsvText ==~ $/(?ms)\s*"Module";"Mojo";"Time".*/$
assert buildTimeCsvText ==~ $/(?ms).*"minimal-pom";"maven-compiler-plugin:testCompile \(default-testCompile\)";"\d{1,3}\.\d{3}".*/$
assert buildTimeCsvText ==~ $/(?ms).*"minimal-pom";"maven-surefire-plugin:test \(default-test\)";"\d{1,3}\.\d{3}".*/$
assert buildTimeCsvText ==~ $/(?ms).*"minimal-pom";"maven-jar-plugin:jar \(default-jar\)";"\d{1,3}\.\d{3}".*/$
assert buildTimeCsvText ==~ $/(?ms).*"minimal-pom";"maven-compiler-plugin:compile \(default-compile\)";"\d{1,3}\.\d{3}".*/$
assert buildTimeCsvText ==~ $/(?ms).*"minimal-pom";"maven-resources-plugin:resources \(default-resources\)";"\d{1,3}\.\d{3}".*/$
assert buildTimeCsvText ==~ $/(?ms).*"minimal-pom";"maven-resources-plugin:testResources \(default-testResources\)";"\d{1,3}\.\d{3}".*/$

