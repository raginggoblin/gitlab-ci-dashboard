#!/usr/bin/env bash

OPENAPI_VERSION="v7.20.0"
OPENAPI_DOCKER_IMAGE="openapitools/openapi-generator-cli"

OPENAPI_OUTPUT_DIR="./backend"
OPENAPI_IGNORE_SOURCE="./openapi/.openapi-generator-ignore"
OPENAPI_IGNORE_TARGET="./backend/.openapi-generator-ignore"
OPENAPI_GENERATOR_FILES="./backend/.openapi-generator"
OPENAPI_GRADLE_FILES="./backend/gradle"

function echoBuildStep() {
  echo "#############################################################################################################"
  echo "#######################################   ${buildstep}   ####################################################"
  echo "#############################################################################################################"
}

buildstep='Openapi generate'
echoBuildStep
cp ${OPENAPI_IGNORE_SOURCE} ${OPENAPI_IGNORE_TARGET}

docker run --rm -i -t -v $(pwd):$(pwd) -w $(pwd) -u $(id -u) ${OPENAPI_DOCKER_IMAGE}:${OPENAPI_VERSION} batch --clean --fail-fast --verbose openapi/config/*

rm -rf ${OPENAPI_IGNORE_TARGET}
rm -rf ${OPENAPI_GENERATOR_FILES}
rm -rf ${OPENAPI_GRADLE_FILES}
