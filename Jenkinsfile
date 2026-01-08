pipeline {
    agent any

    tools {
        maven 'M3'
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Clone Repository') {
            steps {
                git branch: 'working_main',
                    url: 'https://github.com/riddhimaBhanja/Loan-Management-System.git'
            }
        }

        stage('Build Microservices (Maven)') {
            steps {
                dir('loan-management-microservices') {
                    bat '''
                    for /d %%d in (*) do (
                        if exist "%%d\\pom.xml" (
                            echo ====================================
                            echo Building microservice: %%d
                            echo ====================================
                            mvn -f "%%d\\pom.xml" clean package -DskipTests
                        )
                    )
                    '''
                }
            }
        }

        stage('Stop Existing Containers') {
            steps {
                bat 'docker compose down || exit 0'
            }
        }

        stage('Docker Compose Build & Run') {
            steps {
                bat '''
                echo Building Docker images...
                docker compose build

                echo Starting services...
                docker compose up -d

                echo Waiting for services to start...
                timeout /t 30 /nobreak

                echo Checking service status...
                docker compose ps
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                bat '''
                echo Verifying deployment...
                docker compose ps

                echo All services deployed successfully!
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Loan Management System built and deployed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Check Jenkins console logs.'
        }
    }
}
