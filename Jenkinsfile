pipeline {
    environment {
        registry = 'docker.io/jiwonchoe' // DockerHub 계정
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
  nodeSelector:
    kubernetes.io/hostname: k8s-cicd
  tolerations:
  - key: "no-kafka"
    operator: "Equal"
    value: "true"
    effect: "NoSchedule"
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:v1.23.2-debug
    imagePullPolicy: Always
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
        stage ('Git Clone') {
            steps {
                checkout scmGit(branches: [[name: 'main']], userRemoteConfigs: [[credentialsId: 'jiwonchoe12', url: 'https://github.com/shinhan-instock/backend.git']])
            }
        }

        stage('Build JAR') {
            steps {
                sh './gradlew :core-module:clean :core-module:build --no-daemon'

                // 현재 작업 디렉토리 확인
                sh 'pwd'

                // 빌드된 JAR 파일 목록 확인
                sh 'ls -al ./core-module/build/libs/'

                // JAR 파일이 없으면 빌드 실패 처리
                script {
                    def jarExists = sh(script: 'ls ./core-module/build/libs/*.jar | wc -l', returnStdout: true).trim()
                    if (jarExists == "0") {
                        error "🚨 JAR 파일이 생성되지 않았습니다! build.gradle 설정을 확인하세요."
                    }
                }
            }
        }

        stage('Build & Push Docker Images') {
            parallel {
                stage('Build & Push core-module') {
                    steps {
                        container('kaniko') {
                            script {
                                // JAR 파일 경로 확인
                                sh 'ls -al ${WORKSPACE}/core-module/build/libs/'

                                // Docker 이미지 빌드 및 푸시
                                sh "/kaniko/executor --context ${WORKSPACE}/core-module \
                                    --destination ${registry}/core-module:latest \
                                    --insecure \
                                    --skip-tls-verify  \
                                    --cleanup \
                                    --dockerfile ${WORKSPACE}/core-module/Dockerfile \
                                    --verbosity debug"
                            }
                        }
                    }
                }
            }
        }

    } // **stages 블록 닫기**

    post {
        success {
            echo "🎉 성공적으로 빌드 & 푸시 완료!"
        }
        failure {
            echo "🚨 빌드 실패! 로그를 확인하세요..."
        }
    }
}
