## Getting the code

```
git clone https://github.com/houglum/maven-multimodule-google-cloud-java
cd maven-multimodule-google-cloud-java
git submodule update --init --recursive

# Install our fork of google-cloud-java as a local dependency for this project.
mvn install -DskipTests
```

## Running the main module

```
cd main-module

BKT_NAME="your-bucket-name-here"
OBJ_NAME="your-object-name-here"

mvn compile && \
mvn exec:java -D exec.mainClass=com.houglum.signurlexample.App \
    -Dexec.args="${BKT_NAME:?must be set} ${OBJ_NAME:?must be set}"
```
