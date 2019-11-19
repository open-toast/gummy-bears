#!/usr/bin/env groovy

@Library('pipes') _

def doGradle(command) {
  sh "./gradlew $command --stacktrace --no-daemon"
}

def artifactory = 'https://artifactory.eng.toasttab.com/artifactory'

pipeline {
  agent {
    label 'tw-agent'
  }
  environment {
    JAVA_HOME = "${tool name: 'corretto8'}"
  }
  options {
    skipDefaultCheckout()
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '25'))
  }
  stages {
    stage('Setup') {
      steps {
        checkout scm
        doGradle 'clean'
      }
    }
    stage('Build Project') {
      steps {
        doGradle 'build assembleRelease'
      }
    }
    stage('Deploy') {
      when { expression { return !env.JOB_BASE_NAME.contains('PR') } }
      steps {
        echo 'Deploying to artifactory.'
        doGradle "publish -Ppublish.remote.url.snapshots=$artifactory/libs-snapshot-local -Ppublish.remote.url.releases=$artifactory/libs-release-local"
      }
    }
  }
}
