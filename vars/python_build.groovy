def call(serviceDirectory, dockerRepoName, imageName) {
    pipeline {
        agent any

        // parameters {
        // booleanParam(defaultValue: false, description: 'Deploy the App', name: 'DEPLOY')
        // }

        stages {
            stage('Build') {
                steps {
                    dir(serviceDirectory) {
                        sh 'pwd'
                        // Install dependencies from requirements.txt
                        sh 'pip install -r requirements.txt --break-system-packages'
                        sh 'pip install --upgrade flask --break-system-packages'
                    }
                }
            }
            
            
            stage('Python Lint') {
                steps {
                    dir(serviceDirectory) {
                        sh 'pylint --fail-under=5 *.py'
                    }
                }
            }
            // stage('Security Check') {
            // steps {
            //     script {
            //         // Install safety (Make sure you have safety installed on your Jenkins server)
                    
            //         dir(serviceDirectory) {
            //             // Scan Python dependencies for vulnerabilities
            //             sh 'safety check --full-report'
            //         }
            //         // Fail the build if vulnerabilities are found (adjust the severity as needed)
            //         catchError(buildResult: 'UNSTABLE') {
            //             sh 'safety check --full-report --fail-below 2 -r requirements.txt'
            //         }
            //     }
            // }
            // }

            // stage('Test and Coverage') {
            //     steps {
            //         script {
            //             def test_reports_exist = fileExists 'test-reports'
            //             if (test_reports_exist) {
            //                 sh 'rm test-reports/*.xml || true'
            //             }
            //             def api_test_reports_exist = fileExists 'api-test-reports'
            //             if (api_test_reports_exist) {
            //                 sh 'rm api-test-reports/*.xml || true'
            //             }
            //         }
            //         script {
            //             def tests = findFiles(glob: 'test*.py')
            //             tests.each {
            //                 sh "coverage run --omit */site-packages/*,*/dist-packages/* ${it.name}"
            //             }
            //         }
            //         sh 'coverage report'
            //     }
            //     post {
            //         always {
            //             script {
            //                 def test_reports_exist = fileExists 'test-reports'
            //                 if (test_reports_exist) {
            //                     junit 'test-reports/*.xml'
            //                 }
            //                 def api_test_reports_exist = fileExists 'api-test-reports'
            //                 if (api_test_reports_exist) {
            //                     junit 'api-test-reports/*.xml'
            //                 }
            //             }
            //         }
            //     }
            // }

            stage('Package') {
                when {
                    expression { env.GIT_BRANCH == 'origin/main' }
                }
                steps {
                    dir(serviceDirectory) {
                        withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                            sh "docker login -u 'aashaybharadwaj' -p '$TOKEN' docker.io"
                            sh "docker build -t ${dockerRepoName}:latest --tag aashaybharadwaj/${dockerRepoName}:${imageName} ."
                            sh "docker push aashaybharadwaj/${dockerRepoName}:${imageName}"
                        }
                    }
                }
            }

            
            
            // stage('Zip Artifacts') {
            //     steps {
            //         sh 'zip app.zip *.py'
            //     }
            //     post {
            //         always {
            //             archiveArtifacts artifacts: 'app.zip', onlyIfSuccessful: true
            //         }
            //     }
            // }

            // stage('Deliver') {
            //     when {
            //         expression { params.DEPLOY }
            //     }
            //     steps {
            //         script {
            //             echo "Delivering the Python application..."
            //             // Stop and remove the existing container (if any)
            //             sh "docker stop ${dockerRepoName} || true"
            //             sh "docker rm ${dockerRepoName} || true"
                        
            //             // Run a new container with specified port
            //             sh "docker run -d -p ${portNum}:${portNum} --name ${dockerRepoName} ${dockerRepoName}:latest"
            //         }
            //     }
            // }
        }
    }
}
