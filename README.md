# Launch JDK via JNI from Native Image

[![CI](https://github.com/JaroslavTulach/LaunchJdkViaJni/actions/workflows/ni.yml/badge.svg)](https://github.com/JaroslavTulach/LaunchJdkViaJni/actions/workflows/ni.yml)

This project investigates how to launch a JVM inside of a running _native image_ executable. 
Useful for embedding JVM into _native image_ compiled application. Most of the problems
are related to launching on Windows, so this project run _Windows CI_ pipeline.


### Native Image Version

There is a Maven project in this repository that provided access to JNI interface
for GraalVM's `native-image` tool. Use:
```bash
$ export JAVA_HOME=/graalvm-jdk-24+36.1/
$ mvn clean install -Pnative
$ ./target/LaunchJdkViaJni java/lang/Short
...
clazz: 107175425579104
```
to load any JVM class via JNI. The same code works on Mac and with a little bit
of tweaks it could work on Windows as well:
```
LaunchJdkViaJni> .\target\LaunchJdkViaJni.exe java/lang/Short -XX:-InstallSegfaultHandler
...
clazz: 2183191488672
```


### Original C Version

When it doubts about _native image_ it is useful to return back to lauching from inside of plain old C application.
There is a lot of tutorials to do so and having it working proofs we are on the right track:
```bash
$ export JAVA_HOME=/graalvm-jdk-24+36.1/
$ gcc src/main/c/launch.cpp -I $JAVA_HOME/include -I $JAVA_HOME/include/linux -L $JAVA_HOME/lib/server/ -l jvm
$ LD_LIBRARY_PATH=$JAVA_HOME/lib/server ./a.out java/lang/Short
```
The example loads specified class and tries to invoke its static method `test`.
