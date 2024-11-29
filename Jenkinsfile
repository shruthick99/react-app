pipeline {
    agent any

    environment {
        EC2_HOST = '3.132.187.127' // Replace with your EC2 IP
        SSH_CREDENTIALS_ID = 'ec2-ssh-id'
        GITHUB_REPO = 'https://github.com/shruthick99/react-app.git'
        APP_DIR = '/home/ec2-user/my-app' // Adjust to your desired project folder
    }

    stages {
        stage('Clone Repository on EC2') {
            steps {
                sshagent(credentials: [SSH_CREDENTIALS_ID]) {
                    sh """
                    ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} << 'EOF'
                        # Check if the directory exists
                        if [ ! -d "${APP_DIR}" ]; then
                            # Clone the repository if it doesn't exist
                            git clone ${GITHUB_REPO} ${APP_DIR}
                        else
                            # Navigate to the repository and pull latest changes
                            cd ${APP_DIR}
                            git pull origin main
                        fi
EOF
                    """
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sshagent(credentials: [SSH_CREDENTIALS_ID]) {
                    sh """
                    ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} << 'EOF'
                        # Navigate to the application directory
                        cd ${APP_DIR}

                        # Stop and remove existing containers
                        docker-compose down

                        # Build and start the application
                        docker-compose up -d --build
EOF
                    """
                }
            }
        }
    }
}
