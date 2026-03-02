pipeline {
    agent any
    
    tools {
        maven 'Maven_3'
    }

    triggers {
        pollSCM('H/30 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Stage 1: Build & Unit Test') {
            steps {
                sh 'mvn clean verify -Dparallel=none'
            }
        }

        stage('Stage 2: Integration Tests') {
            steps {
                sh 'mvn verify -P integration -Dparallel=none'
            }
        }

        stage('Stage 3: Selenium E2E Tests') {
            steps {
                sh 'mvn verify -P e2e -Dparallel=none'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarServer') {
                    sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml,**/target/failsafe-reports/*.xml'

            recordCoverage(tools: [[parser: 'JACOCO', pattern: '**/jacoco.xml']])
            
            publishHTML(target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'Detailed JaCoCo Report'
            ])
        }
    }
}