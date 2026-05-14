#!/bin/sh
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export PATH=$JAVA_HOME/bin:$PATH
export ANDROID_HOME=/opt/android-sdk
export GRADLE_HOME=/root/.local/share/mise/installs/gradle/8.14.4/gradle-8.14.4
exec $GRADLE_HOME/bin/gradle "$@"
