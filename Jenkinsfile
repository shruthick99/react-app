pipeline {
    agent any

    environment {
        EC2_HOST = '3.132.187.127' //  your EC2 IP
        SSH_CREDENTIALS_ID = 'ec2-ssh-id' //  the actual Jenkins credentials ID
        GITHUB_REPO = 'https://github.com/shruthick99/react-app.git'
        APP_DIR = '/home/ec2-user/my-app' //  your desired project folder
    }

    stages {
        stage('Clone Repository on EC2') {
            steps {
                sshagent(credentials: [SSH_CREDENTIALS_ID]) {
                    sh """
                    ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} << 'EOF'
                        # Check if the directory exists
                        if [ ! -d "${APP_DIR}" ]; then
                            git clone -b master ${GITHUB_REPO} ${APP_DIR}
                        else
                            cd ${APP_DIR}
                            git config pull.rebase false
                            git pull origin master --allow-unrelated-histories
                        fi
EOF
                    """
                }
            }
        }

        stage('Build Backend') {
            steps {
                sshagent(credentials: [SSH_CREDENTIALS_ID]) {
                    sh """
                    ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} << 'EOF'
                        # Navigate to the backend directory
                        cd ${APP_DIR}/check-in

                        # Set execute permission for the Maven wrapper
                        chmod +x ./mvnw

                        # Run Maven build to generate the .war file
                        ./mvnw clean package
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
