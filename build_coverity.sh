# build_coverity.sh
#!/bin/bash
COV_BUILD_PATH=/home1/irteam/apps/cov-analysis-linux64-2017.07/bin
PROJECT_NAME=coverity-naverid_3rdparty_android
PROJECT_HOME=/home1/irteam/deploy/jenkins_ndeploy/jobs/${PROJECT_NAME}/workspace
ANDROID_HOME=/home1/irteam/apps/android-sdk
INTERMEDIATE_DIR=${PROJECT_HOME}/intdir
STREAM_NAME=ID_naverid_3rdparty_android

HOST=10.114.241.156
USER=user_01
PASSWORD=ghldnjsvmf

cd ${PROJECT_HOME}
${COV_BUILD_PATH}/cov-build --dir ${INTERMEDIATE_DIR} ${PROJECT_HOME}/gradlew clean NaverOAuthLibrary:assemble
${COV_BUILD_PATH}/cov-analyze --dir ${INTERMEDIATE_DIR} --all
${COV_BUILD_PATH}/cov-commit-defects --dir ${INTERMEDIATE_DIR} --stream ${STREAM_NAME} --host ${HOST} --port 8080 --user ${USER} --password ${PASSWORD}