#!/bin/bash
set -e

echo "=== Initializing Workspace Harness ==="

# Check Java version
if command -v java >/dev/null 2>&1; then
    JAVA_VER=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2 | cut -d '.' -f 1)
    echo "Found Java version: $JAVA_VER"
    if [ "$JAVA_VER" -lt 21 ]; then
        echo "WARNING: Currently using Java $JAVA_VER, but modernization target is Java 21."
    else
        echo "Java version looks good ($JAVA_VER)."
    fi
else
    echo "WARNING: Java is not installed or not in PATH."
fi

# Ensure Maven Wrapper is executable
if [ -f "./mvnw" ]; then
    chmod +x ./mvnw
fi

# Clean and compile to verify basic sanity
echo "Compiling project to verify build state..."
./mvnw clean compile

# Run tests to check current verification status
echo "Running tests..."
./mvnw test

# Check for uncommitted changes (Chain of Small Steps)
echo "Checking Git status..."
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    if [[ -n $(git status -s) ]]; then
        echo "WARNING: You have uncommitted changes. Consider committing verified steps (Chain of Small Steps) before proceeding."
    else
        echo "Git working directory is clean. Good to proceed with the next step."
    fi
else
    echo "Not a Git repository. Consider initializing one for the Chain of Small Steps pattern."
fi

echo "=== Initialization Complete ==="
