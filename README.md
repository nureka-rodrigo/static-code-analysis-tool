# Ballerina Static Code Analysis Tool

## Overview

Static code analysis uses tools to examine code without executing the code. They are used for identifying potential issues like bugs, vulnerabilities, and style violations. Static code analysis improves software quality by detecting issues early, ensuring better maintainability, and providing enhanced security. Ballerina supports static code analysis using the Ballerina scan tool. The Ballerina scan tool provides the command-line functionality to statically analyze Ballerina files and report analysis results.

This repository consists of

- The Ballerina scan tool implementation.
- The core scan logic.
- The extension points for introducing additional analysis and reporting results to static code analysis platforms.

## Prerequisites

1. OpenJDK 21 ([Adopt OpenJDK](https://adoptium.net/temurin/releases/?version=21) or any other OpenJDK distribution)

2. [Ballerina](https://ballerina.io/)

## Building from the source

### For Linux/Mac

Execute the commands below to build from the source.

1. Export GitHub Personal access token with read package permissions as follows:

    ```bash
    export packageUser=<GitHub username>
    export packagePAT=<GitHub personal access token>
    ```

2. To build the package:

    ```bash
    ./gradlew clean build
    ```

> **Note**: The scan tool configurations will be appended to the contents of the `.ballerina/.config/bal-tools.toml` file during the build process.

3. To run the tests:

    ```bash
    ./gradlew clean test
    ```

4. To build the package without tests:

    ```bash
    ./gradlew clean build -x test
    ```

### For Windows

Execute the commands below to build from the source.

1. Set GitHub Personal access token with read package permissions as follows:

    ```cmd
    set packageUser=<GitHub username>
    set packagePAT=<GitHub personal access token>
    ```

2. To build the package:

    ```cmd
    ./gradlew.bat clean build
    ```

> **Note**: The scan tool configurations will be appended to the contents of the `.ballerina\.config\bal-tools.toml` file during the build process.

3. To run the tests:

    ```cmd
    ./gradlew.bat clean test
    ```

4. To build the package without tests:

    ```cmd
    ./gradlew.bat clean build -x test
    ```

## Testing the implementation

Once you have implemented, you can test if the tool is working properly either by:

- Testing the tool by publishing to the local repository
- Testing the tool by pushing to the Ballerina dev central repository

### Adding into the local repository

The local repository is intended to be used for testing purposes. It imitates the central repository locally to be tested.

1. Once the tool is packed with `bal pack`, run:

    ```bash
    bal push --repository=local
    ```

2. Pull the tool using:

    ```bash
    bal tool pull <tool-id>:<version> --repository=local
    ```

    **Note**: You can find the `tool-id` and `version` details from `.ballerina\.config\bal-tools.toml` file.

3. Execute the tool by calling:

    ```bash
    bal <tool-id>
    ```

### Pushing to Ballerina dev-central

Only `ballerina`, `ballerinax` organizations are allowed to push tools to the production central. You can use dev-central for development.

1. Export the environment variable to point to the dev central:

    For Linux/Mac:

    ```bash
    export BALLERINA_DEV_CENTRAL=true
    ```

    For Windows:

    ```cmd
    set BALLERINA_DEV_CENTRAL=true
    ```

2. Run:

    ```bash
    bal push
    ```

> **Note**: The tool ID is globally unique. Therefore, once you push a tool with an ID, it cannot be reused by anyone within the dev central. It is recommended to test with the local repository and ensure that you have a working tool before publishing to the dev-central.

## Contribute to Ballerina

As an open-source project, Ballerina welcomes contributions from the community.

For more information, go to the [contribution guidelines](https://github.com/ballerina-platform/ballerina-lang/blob/master/CONTRIBUTING.md).

## Code of conduct

All the contributors are encouraged to read the [Ballerina Code of Conduct](https://ballerina.io/code-of-conduct).

## Useful links

* Chat live with us via our [Discord server](https://discord.gg/ballerinalang).
* Post all technical questions on Stack Overflow with the [#ballerina](https://stackoverflow.com/questions/tagged/ballerina) tag.
