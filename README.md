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
