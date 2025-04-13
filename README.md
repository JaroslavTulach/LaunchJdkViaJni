# Launch JDK via JNI

This project investigates how to launch a JVM inside of a running executable. Useful for
embedding JVM into native image compiled application.

### Plain C Version

First of all, let's perform the lauching from inside of C application. There is a lot of
tutorials to do so and having it working proofs we are on the right track:
```bash
$ export JAVA_HOME=/graalvm-jdk-24+36.1/
$ gcc src/main/c/launch.cpp -I $JAVA_HOME/include -I $JAVA_HOME/include/linux -L $JAVA_HOME/lib/server/ -l jvm
$ LD_LIBRARY_PATH=$JAVA_HOME/lib/server ./a.out java/lang/Short
```
The example loads specified class and tries to invoke its static method `test`.

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
to load any JVM class via JNI.
