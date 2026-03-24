R_ARGS:-}
#!/bin/sh

# Gradle start up script

# Set default JVM options
DEFAULT_JVM_OPTS='"-Xmx1024m" "-Xms256m"'

# Find java
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/bin/java" ] ; then
        JAVACMD="$JAVA_HOME/bin/java"
    else
        JAVACMD=java
    fi
else
    JAVACMD=java
fi

# Set classpath
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Execute gradle
exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain "$@"
