pipeline {
    environment {
        registry = 'docker.io/kmaster8' // DockerHub 계정
        registryCredential = 'dockerhub-jw' // Jenkins에 등록된 DockerHub 인증 정보
    }

    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins-build: app-build
spec:
  tolerations:
  - key: "no-kafka"
    operator: "Equal"
    value: "true"
    effect: "NoSchedule"
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:v1.5.1-debug
    imagePullPolicy: IfNotPresent
    command:
    - /busybox/cat
    tty: true
    volumeMounts:
      - name: jenkins-docker-cfg
        mountPath: /kaniko/.docker
  volumes:
  - name: jenkins-docker-cfg
    projected:
      sources:
      - secret:
          name: docker-credentials
          items:
            - key: .dockerconfigjson
              path: config.json
"""
        }
    }

    stages {
        stage ('git clone') {
            steps() {
                checkout scmGit(branches: [[name: 'main']], userRemoteConfigs: [[credentialsId: 'jiwonchoe12', url: 'https://github.com/shinhan-instock/backend.git']])
            }
        }
        // stage('Checkout') {
        //     steps {
        //         script {
        //             git url: 'https://github.com/shinhan-instock/backend.git', credentialsId: 'jiwonchoe12'
        //             sh 'ls -la'
        //         }
        //     }
        // }

        stage('Build JAR') {
            steps {
                sh './gradlew build'
            }
        }

        stage('Build & Push Docker Images') {
            parallel {
                stage('Build & Push core-module') {
                    steps {
                        container('kaniko') {
                            sh '/kaniko/executor --context `pwd`/core-module \
                                --destination $registry/core-module:latest \
                                --insecure \
                                --skip-tls-verify  \
                                --cleanup \
                                --dockerfile core-module/Dockerfile \
                                --verbosity debug'
                        }
                    }
                }

                stage('Build & Push community-module') {
                    steps {
                        container('kaniko') {
                            sh '/kaniko/executor --context `pwd`/community-module \
                                --destination $registry/community-module:latest \
                                --insecure \
                                --skip-tls-verify  \
                                --cleanup \
                                --dockerfile community-module/Dockerfile \
                                --verbosity debug'
                        }
                    }
                }

                stage('Build & Push stock-module') {
                    steps {
                        container('kaniko') {
                            sh '/kaniko/executor --context `pwd`/stock-module \
                                --destination $registry/stock-module:latest \
                                --insecure \
                                --skip-tls-verify  \
                                --cleanup \
                                --dockerfile stock-module/Dockerfile \
                                --verbosity debug'
                        }
                    }
                }

                stage('Build & Push piggyBank-module') {
                    steps {
                        container('kaniko') {
                            sh '/kaniko/executor --context `pwd`/piggyBank-module \
                                --destination $registry/piggyBank-module:latest \
                                --insecure \
                                --skip-tls-verify  \
                                --cleanup \
                                --dockerfile piggyBank-module/Dockerfile \
                                --verbosity debug'
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🎉 성공적으로 빌드 & 푸시 완료!"
        }
        failure {
            echo "🚨 빌드 실패! 로그를 확인하세요."
        }
    }
}
