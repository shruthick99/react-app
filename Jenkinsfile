pipeline {
    agent any

    environment {
        EC2_USER = 'ec2-user'
        EC2_HOST = '3.132.187.127'
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo "Cloning repository into Jenkins workspace..."
                git branch: 'master', url: 'https://github.com/shruthick99/react-app.git'
                sh "ls -l ${WORKSPACE}"
            }
        }

        stage('Build Backend') {
            steps {
                echo "Building backend application..."
                sh """
                cd ${WORKSPACE}/check-in
                chmod +x mvnw
                ./mvnw clean package -DskipTests
                """
            }
        }

        stage('Build Frontend') {
            steps {
                echo "Building frontend application..."
                sh """
                cd ${WORKSPACE}/IBOB
                npm install
                npm run build
                """
            }
        }

        stage('Transfer Files to EC2') {
            steps {
                echo "Transferring files to EC2 instance..."
                sshagent(['ec2-ssh-id']) {
                    sh """
                    scp -o StrictHostKeyChecking=no -r ${WORKSPACE}/* ${EC2_USER}@${EC2_HOST}:/home/ec2-user/app
                    """
                }
            }
        }

        stage('Deploy on EC2') {
            steps {
                echo "Deploying application on EC2 using Docker Compose..."
                sshagent(['ec2-ssh-id']) {
                    sh """
                    ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} '
                        cd /home/ec2-user/app &&
                        docker-compose down || true &&
                        docker-compose up -d --build
                    '
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Application deployed successfully on EC2!"
        }
        failure {
            echo "Deployment failed. Check logs for details."
        }
    }
}
