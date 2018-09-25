[![Build Status](https://travis-ci.org/timgifford/maven-buildtime-extension.svg?branch=master)](https://travis-ci.org/timgifford/maven-buildtime-extension)
[![license](http://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/timgifford/maven-buildtime-extension/blob/upgrades/LICENSE.txt)
# Overview
Maven doesn't give you detailed information about where your build is taking the most time. This build time extension
displays the duration for each goal that is run during your build.

# Usage
Put this in your pom.xml to find out why you are slow.
By default this will log only to maven debug `-X,--debug`, however debug is quite noisy, so you can enable output to normal log
via the property `buildtime.output.log` in your pom or via commandline `-Dbuildtime.output.log=true`.

## Capture the results to a CSV file
Thanks to [@leonard84](https://github.com/leonard84) for the contribution. You'll need to set a property to enable this feature. 
Eventually, we can enhance this functionality to include other output formats. 
I personally would like to publish the results to an InfluxDb instance.

```
mvn compile -Dbuildtime.output.csv=true -Dbuildtime.output.csv.file=classes\out.csv
```

Maven introduced slf4j in version 3.2 which broke this build extension for Maven 3.0 users. 
Version 2.0+ will work with the latest version of Maven.

## Maven 3.3 - Use version 3.0+

With version 3.0.0 we switched to Maven's `EventSpy` API which increases the compatibility with other tools, e.g. Jenkins which
also use this API to instrument Maven. However this requires a different way of loading the extension. This also requires at
least Maven 3.3+. 

Create the following file `<projectroot>/.mvn/extensions.xml` with this as content

```
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
    <extension>
        <groupId>co.leantechniques</groupId>
        <artifactId>maven-buildtime-extension</artifactId>
        <version>3.0.0</version>
    </extension>
</extensions>
```
Due to [MNG-5786](https://issues.apache.org/jira/browse/MNG-5786) you might need to use Maven 3.3.9 if 
`<projectroot>/.mvn/extensions.xml` is not picked up. You can also check the build log for the presence of the string
`BuildTimeEventSpy is registered.` which is logged if the extension was successfully activated.

## Maven 3.2 - Use version 2.0+ ```Deprecated```


```
<build>
    <extensions>
        <extension>
            <groupId>co.leantechniques</groupId>
            <artifactId>maven-buildtime-extension</artifactId>
            <version>2.0.3</version>
        </extension>
    </extensions>
</build>
```

## Maven 3.0 - Use version 1.0 ```Deprecated```
Version 1 works with Maven 3.0. All future improvements and bug fixes will occur on the 2.0 version 
of this build extension

```
<build>
    <extensions>
        <extension>
            <groupId>co.leantechniques</groupId>
            <artifactId>maven-buildtime-extension</artifactId>
            <version>1.0.1</version>
        </extension>
    </extensions>
</build>
```

## One-off execution

Instead of modifying the POM you can also perform a one-off execution. In this
case you must manually download the extension (only once, before the first
execution) and then explicitly provide it as a command line argument:

```
VERSION=2.0.3
mvn -q dependency:get -Dartifact="co.leantechniques:maven-buildtime-extension:$VERSION"
mvn compile -Dmaven.ext.class.path="$HOME/.m2/repository/co/leantechniques/maven-buildtime-extension/$VERSION/maven-buildtime-extension-$VERSION.jar"
```

# Example

```
[INFO] Reactor Summary:
[INFO]
[INFO] CoEfficient ....................................... SUCCESS [0.103s]
[INFO] coefficient-core .................................. SUCCESS [0.903s]
[INFO] coefficient-scm ................................... SUCCESS [0.314s]
[INFO] coefficient-heatmap ............................... SUCCESS [0.202s]
[INFO] coefficient-test-ratios ........................... SUCCESS [0.114s]
[INFO] coefficient-mvn ................................... SUCCESS [0.769s]
[INFO] coefficient-acceptance-test ....................... SUCCESS [0.305s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3.490s
[INFO] Finished at: Sun Jul 14 16:10:34 CDT 2013
[INFO] Final Memory: 12M/81M
[INFO] ------------------------------------------------------------------------
[INFO] Build Time Summary:
[INFO]
[INFO] coefficient
[INFO]   maven-clean-plugin:clean ................................. [0.079s]
[INFO] coefficient-core
[INFO]   maven-clean-plugin:clean ................................. [0.007s]
[INFO]   maven-resources-plugin:resources ......................... [0.261s]
[INFO]   maven-compiler-plugin:compile ............................ [0.569s]
[INFO] coefficient-scm
[INFO]   maven-clean-plugin:clean ................................. [0.007s]
[INFO]   maven-resources-plugin:resources ......................... [0.002s]
[INFO]   maven-compiler-plugin:compile ............................ [0.281s]
[INFO] coefficient-heatmap
[INFO]   maven-clean-plugin:clean ................................. [0.003s]
[INFO]   maven-resources-plugin:resources ......................... [0.002s]
[INFO]   maven-compiler-plugin:compile ............................ [0.172s]
[INFO] coefficient-test-ratios
[INFO]   maven-clean-plugin:clean ................................. [0.005s]
[INFO]   maven-resources-plugin:resources ......................... [0.002s]
[INFO]   maven-compiler-plugin:compile ............................ [0.085s]
[INFO] coefficient-mvn
[INFO]   maven-clean-plugin:clean ................................. [0.005s]
[INFO]   maven-plugin-plugin:descriptor ........................... [0.628s]
[INFO]   maven-resources-plugin:resources ......................... [0.002s]
[INFO]   maven-compiler-plugin:compile ............................ [0.099s]
[INFO] coefficient-acceptance-test
[INFO]   maven-clean-plugin:clean ................................. [0.002s]
[INFO]   maven-resources-plugin:resources ......................... [0.002s]
[INFO]   maven-compiler-plugin:compile ............................ [0.002s]
[INFO] ------------------------------------------------------------------------
```

# Release Process
Sonatype requires all published components to be signed. These commands prompt for a `gpg passphase`. (Reminder for me: It is in your password vault)
```
./mvnw release:prepare

./mvnw release:perform
```

2018-05-30
Had to add this command to the my console to get it signed:
```bash
GPG_TTY=$(tty)
export GPG_TTY
```
