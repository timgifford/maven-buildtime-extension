#Overview
Maven doesn't give you detailed information about where you build is taking the most time. This build time extension
displays the duration for each goal that is ran during your build.

# Usage
Put this in your pom.xml to find out why you are slow.

```
<build>
    <extensions>
        <extension>
            <groupId>co.leantechniques.maven</groupId>
            <artifactId>maven-buildtime-extension</artifactId>
            <version>1.0-SNAPSHOT</version>
        </extension>
    </extensions>
</build>
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
