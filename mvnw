#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper script for POSIX systems
# ----------------------------------------------------------------------------

set -e

[ -z "$MAVEN_WRAPPER_SKIP_BOOTSTRAP" ] || exec mvn "$@"

WRAPPER_DIR="$(cd "$(dirname "$0")" && pwd)"
MAVEN_PROJECTBASEDIR="$WRAPPER_DIR"
export MAVEN_PROJECTBASEDIR

WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

# Extract distributionUrl
DISTRIBUTION_URL=$(grep "^distributionUrl=" "$WRAPPER_PROPERTIES" | cut -d'=' -f2)

MAVEN_USER_HOME="${MAVEN_USER_HOME:-$HOME/.m2}"
WRAPPER_DIR_CACHE="$MAVEN_USER_HOME/wrapper/dists"

# Check if mvn is already available in PATH or cache
if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
fi

EXISTING_MVN=$(find "$WRAPPER_DIR_CACHE" -name "mvn" -type f 2>/dev/null | head -n 1)
if [ -n "$EXISTING_MVN" ] && [ -x "$EXISTING_MVN" ]; then
    exec "$EXISTING_MVN" "$@"
fi

# Filename from url
ZIP_NAME=$(basename "$DISTRIBUTION_URL")
DIR_NAME="${ZIP_NAME%.zip}"
TARGET_DIR="$WRAPPER_DIR_CACHE/$DIR_NAME"
MVN_EXEC="$TARGET_DIR/apache-maven-3.9.6/bin/mvn"

if [ ! -x "$MVN_EXEC" ]; then
    echo "Downloading Maven from $DISTRIBUTION_URL ..."
    mkdir -p "$TARGET_DIR"
    TEMP_ZIP="$TARGET_DIR/$ZIP_NAME"
    if command -v curl >/dev/null 2>&1; then
        curl -fsSL "$DISTRIBUTION_URL" -o "$TEMP_ZIP"
    elif command -v wget >/dev/null 2>&1; then
        wget -q "$DISTRIBUTION_URL" -O "$TEMP_ZIP"
    else
        echo "Error: Neither curl nor wget found to download Maven wrapper." >&2
        exit 1
    fi
    unzip -q -o "$TEMP_ZIP" -d "$TARGET_DIR"
    rm -f "$TEMP_ZIP"
    chmod +x "$TARGET_DIR"/*/bin/mvn
    MVN_EXEC=$(find "$TARGET_DIR" -name "mvn" -type f -perm +111 2>/dev/null | head -n 1)
fi

exec "$MVN_EXEC" "$@"
