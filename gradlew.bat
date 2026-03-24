@echo off

set DEFAULT_JVM_OPTS="-Xmx1024m" "-Xms256m"

if "%JAVA_HOME%"=="" (
    set JAVA_EXE=java
) else (
    set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
