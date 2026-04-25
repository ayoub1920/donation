pipeline {
    agent any

    tools {
        maven 'Maven 3.9'
    }

    environment {
        SPRING_DATASOURCE_URL = 'jdbc:postgresql://postgres:5432/donation'
        SPRING_DATASOURCE_USERNAME = 'postgres'
        SPRING_DATASOURCE_PASSWORD = 'postgres'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/ayoub1920/donation.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh 'mvn verify sonar:sonar -Dsonar.projectKey=my-backend -Dsonar.login=sqp_81e10c6d8a75e9da8172e0935d743fbc4adb6f2e'
                }
            }
        }
    }
}
