#!/usr/bin/env bash

OPENAPI_VERSION="v7.20.0"
DOCKER_IMAGE="openapitools/openapi-generator-cli"

OPENAPI_OUTPUT_DIR="./backend/target/generated-sources/openapi"
OPENAPI_IGNORE_SOURCE="./openapi/.openapi-generator-ignore"
OPENAPI_IGNORE_TARGET="./backend/target/generated-sources/openapi/"

function echoBuildStep() {
  echo "#############################################################################################################"
  echo "#######################################   ${buildstep}   ######################################################"
  echo "#############################################################################################################"
}

buildstep='Backend clean'
echoBuildStep
cd backend
mvn clean
cd ..

buildstep='Openapi generate'
echoBuildStep
rm -rf ${OPENAPI_OUTPUT_DIR}
mkdir -p ${OPENAPI_IGNORE_TARGET}
cp ${OPENAPI_IGNORE_SOURCE} ${OPENAPI_IGNORE_TARGET}
docker run --rm -i -t -v $(pwd):$(pwd) -w $(pwd) -u $(id -u) ${DOCKER_IMAGE}:${OPENAPI_VERSION} batch --clean --fail-fast --verbose openapi/config/*

buildstep='Backend build'
echoBuildStep
cd backend
mvn -U -DskipTests -Dmaven.test.skip compile
cd ..

buildstep='Frontend'
echoBuildStep
cd frontend
npm ci
npm run build
cd ..
