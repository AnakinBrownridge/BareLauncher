#!/usr/bin/env bash

# Gradle Wrapper script for building BareLauncher
# Auto-generated wrapper

set -e

GRADLE_VERSION="8.4"
GRADLE_SHA256="3e1af3ae886920c3ac87f7a91f816c0c7c1db60ee3a7aedf41aadb61002bedf9"

if [ ! -f gradle/wrapper/gradle-wrapper.jar ]; then
    mkdir -p gradle/wrapper
    
    echo "Downloading Gradle $GRADLE_VERSION..."
    curl -L "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
        -o /tmp/gradle.zip
    
    unzip -q /tmp/gradle.zip -d /tmp/
    mv /tmp/gradle-${GRADLE_VERSION}/* gradle/
    rm -rf /tmp/gradle*
fi

exec gradle "$@"
