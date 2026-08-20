pipeline {
  options {
    quietPeriod(env.BRANCH_NAME == 'main' ? 60 : 5)
    timeout(time: 15, unit: 'MINUTES')
    buildDiscarder(logRotator(numToKeepStr: env.BRANCH_NAME == 'main' ? '10' : '2'))
    disableConcurrentBuilds()
  }
  agent {
    label 'basic'
  }
  tools {
    maven 'apache-maven-latest'
    jdk 'temurin-jdk21-latest'
  }

  environment {
    PUBLISH_LOCATION = 'cbi/updates/p2-aggregator'
  }

  parameters {
    choice(
      name: 'BUILD_TYPE',
      choices: ['nightly', 'milestone', 'release'],
      description: '''
        Choose the type of build.
        Note that a release build will not promote the build, but rather will promote the most recent milestone build.
        '''
    )

    booleanParam(
      name: 'PROMOTE',
      defaultValue: env.BRANCH_NAME == 'main',
      description: 'Whether to promote the build to the download server.'
    )

    booleanParam(
      name: 'ARCHIVE',
      defaultValue: false,
      description: 'Whether to archive the workspace.'
    )
  }

  stages {
    stage('Display Parameters') {
      steps {
        script {
          def description = """
BUILD_TYPE=${params.BUILD_TYPE}
PROMOTE=${params.PROMOTE}
ARCHIVE=${params.ARCHIVE}
""".trim()
          env.BUILD_TYPE = params.BUILD_TYPE
          env.PROMOTE = params.PROMOTE
          echo description
          currentBuild.description = description.replace("\n", "<br/>")
        }
      }
    }

    stage('Build Tools and Products') {
      steps {
        sshagent(['projects-storage.eclipse.org-bot-ssh']) {
          sh '''
            pwd
            if [[ $PROMOTE == true ]]; then
              sign_argument='-Peclipse-sign'
            else
              promotion_argument='-Dorg.eclipse.justj.p2.manager.args='
              sign_argument=''
            fi
            mvn \
              --no-transfer-progress\
              $promotion_argument \
              -Dorg.eclipse.storage.user=genie.cbi \
              -Dorg.eclipse.justj.p2.manager.build.url=$JOB_URL \
              -Dorg.eclipse.download.location.relative=$PUBLISH_LOCATION \
              -Dorg.eclipse.justj.p2.manager.relative= \
              -Dbuild.type=$BUILD_TYPE \
              -Dgit.commit=$GIT_COMMIT \
              -Dbuild.id=$BUILD_NUMBER \
              -DskipTests=false \
              $sign_argument \
              clean \
              verify
            '''
        }
      }
    }

    stage('Archive Results') {
      when {
        expression {
          params.ARCHIVE
        }
      }
      steps {
        archiveArtifacts '**'
      }
    }
  }

  post {
    failure {
      sendBuildStatusMail('Build Failure')
    }

    fixed {
      sendBuildStatusMail('Back to normal')
    }

  }
}  

def sendBuildStatusMail(String summary) {
  if (env.BRANCH_NAME == 'main') {
    mail to: 'ed.merks@gmail.com',
    subject: "[CBI p2 Aggregator] ${summary} ${currentBuild.fullDisplayName}",
    mimeType: 'text/html',
    body: "Project: ${env.JOB_NAME}<br/>Build Number: ${env.BUILD_NUMBER}<br/>Build URL: ${env.BUILD_URL}<br/>Console: ${env.BUILD_URL}/console"
  }
}