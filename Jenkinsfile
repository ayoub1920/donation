pipeline {
    agent any

    tools {
        maven 'Maven 3.9'
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
        sh '''mvn test \
          -Dspring.datasource.url=jdbc:h2:mem:testdb \
          -Dspring.datasource.driver-class-name=org.h2.Driver \
          -Dspring.datasource.username=sa \
          -Dspring.datasource.password= \
          -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect \
          -Dspring.jpa.hibernate.ddl-auto=create-drop \
          -Deureka.client.enabled=false \
          -Deureka.client.register-with-eureka=false \
          -Deureka.client.fetch-registry=false \
          -Dspring.cloud.discovery.enabled=false \
          -Dspring.main.lazy-initialization=true \
          -Dmanagement.metrics.export.prometheus.enabled=false \
          -Dspring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration'''
    }
}

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh '''mvn verify sonar:sonar \
                      -DskipTests \
                      -Dsonar.projectKey=my-backend \
                      -Dsonar.login=sqp_81e10c6d8a75e9da8172e0935d743fbc4adb6f2e'''
                }
            }
        }
    }
}
