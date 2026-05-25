pipeline {
    agent any

    tools {
        jdk 'JDK_26' // Matches your Java 26 requirement from pom.xml
        maven 'Maven_3.9'
    }

    environment {
        HEADLESS = 'true'
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Clean & Compile') {
            steps {
                // If your Jenkins agent runs on Linux/Mac, swap 'bat' for 'sh'
                bat 'mvn clean test-compile'
            }
        }

        stage('Execute UI Automation Tests') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    // Runs our Cucumber UI tests via the standard TestRunner
                    bat "mvn test -Dtest=TestRunner -Dheadless=${env.HEADLESS}"
                }
            }
        }

        stage('Execute Karate API Tests') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    // Runs our Karate JUnit 5 runner
                    bat 'mvn test -Dtest=ApiTestRunner'
                }
            }
        }
    }

    post {
        always {
            // Automatically publish test results from both Cucumber and Karate runs
            junit 'target/surefire-reports/*.xml'

            // Archive UI reports, Karate reports, screenshots, and logs
            archiveArtifacts artifacts: 'target/cucumber-reports/**/*, target/karate-reports/**/*, target/*.log', allowEmptyArchive: true
        }
    }
}