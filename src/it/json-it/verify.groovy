def buildLogText = new File(basedir, 'build.log').text
assert buildLogText.contains('BuildTimeEventSpy is registered.')

def buildTimeJson =  new File(basedir, 'target/buildtime.json')
assert buildTimeJson.exists()
def buildTimeJsonText = buildTimeJson.text

assert buildTimeJsonText.contains('"projectName":"minimal-pom"')
assert buildTimeJsonText.contains('"totalDuration":')
assert buildTimeJsonText.contains('"steps":{')
assert buildTimeJsonText.contains('"maven-resources-plugin:resources (default-resources)":')
assert buildTimeJsonText.contains('"maven-compiler-plugin:testCompile (default-testCompile)":')
assert buildTimeJsonText.contains('"maven-surefire-plugin:test (default-test)":')
assert buildTimeJsonText.contains('"maven-jar-plugin:jar (default-jar)":')
assert buildTimeJsonText.contains('"maven-compiler-plugin:compile (default-compile)":')
assert buildTimeJsonText.contains('"maven-resources-plugin:testResources (default-testResources)":')
