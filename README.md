## Getting the code

```
git clone https://github.com/houglum/maven-multimodule-google-cloud-java
cd maven-multimodule-google-cloud-java
git submodule update --init --recursive
```

## Building/Running

### Setting up credentials

The main module uses Google Application Default Credentials. If you have a JSON
keyfile, run this command so that the program knows where to find your keyfile:

```
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/keyfile.json"
```

If you're using a GCE service account, make sure that it has been granted an IAM
role that grants the `iam.serviceAccounts.signBlob` permission.

### Providing your resource names and building/running

```
BKT_NAME="your-bucket-name-here"
GET_OBJ_NAME="your-object-name-here"
PUT_OBJ_NAME="another-object-name-here"

mvn install -DskipTests && \
mvn exec:java --projects main-module \
    -Dexec.args="${BKT_NAME:?must be set} ${GET_OBJ_NAME:?must be set} ${PUT_OBJ_NAME:?must be set}"


# NOTE: If you'd like to re-build the project after altering the main module, it
# is much faster to only recompile that module. This can be accomplished using
# maven's `--projects` flag, as shown below:

mvn install --projects main-module -DskipTests && \
mvn exec:java --projects main-module \
    -Dexec.args="${BKT_NAME:?must be set} ${GET_OBJ_NAME:?must be set} ${PUT_OBJ_NAME:?must be set}"
```
