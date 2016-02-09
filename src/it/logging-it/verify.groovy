def buildLogText = new File(basedir, 'build.log').text
assert buildLogText.contains('BuildTimeEventSpy is registered.')
assert buildLogText.contains('maven-compiler-plugin:testCompile (default-testCompile) ..')
assert buildLogText.contains('maven-surefire-plugin:test (default-test) ................')
assert buildLogText.contains('maven-jar-plugin:jar (default-jar) .......................')
assert buildLogText.contains('maven-compiler-plugin:compile (default-compile) ..........')
assert buildLogText.contains('maven-resources-plugin:resources (default-resources) .....')
assert buildLogText.contains('maven-resources-plugin:testResources (default-testResource')
