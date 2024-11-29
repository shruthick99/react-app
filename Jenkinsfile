pipeline {
    agent any

    environment {
        EC2_USER = 'ec2-user'               // Default EC2 username
        EC2_HOST = '3.132.187.127' // Replace with your EC2 public IP
        DOCKER_COMPOSE_FILE = './docker-compose.yml'
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo "Cloning repository..."
                git branch: 'main', url: 'https://github.com/shruthick99/react-app.git'
            }
        }

        stage('Deploy to EC2') {
            steps {
                echo "Deploying application on EC2..."
                sshagent(['ec2-ssh-id']) { // Replace with your Jenkins SSH credential ID
                    sh """
                    ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} '
                        cd /home/ec2-user/app || mkdir -p /home/ec2-user/app && cd /home/ec2-user/app &&
                        rm -rf * &&
                        git clone https://github.com/shruthick99/react-app.git . &&
                        docker-compose down &&
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
