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
  - name: workspace-volume  # 이 부분 추가
    emptyDir: {}  # Jenkins의 workspace와 공유하도록 설정
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
                sh './gradlew :community-module:clean :community-module:build --no-daemon'

              
                // 현재 작업 디렉토리 확인
                sh 'pwd'
        
                // 빌드된 JAR 파일 목록 확인
                sh 'ls -al ./core-module/build/libs/'
        
                // JAR 파일을 core-module-latest.jar로 이름 변경
                sh 'cp ./core-module/build/libs/core-module-0.0.1-SNAPSHOT.jar ./core-module/build/libs/core-module-latest.jar'
                sh 'cp ./community-module/build/libs/community-module-0.0.1-SNAPSHOT.jar ./community-module/build/libs/community-module-latest.jar'
        
                // 변경된 파일 확인
                sh 'ls -al ./core-module/build/libs/'
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

                stage('Build & Push community-module') {
                    steps {
                        container('kaniko') {
                            script {
                                // JAR 파일 경로 확인
                                sh 'ls -al ${WORKSPACE}/community-module/build/libs/'

                                // Docker 이미지 빌드 및 푸시
                                sh "/kaniko/executor --context ${WORKSPACE}/community-module \
                                    --destination ${registry}/community-module:latest \
                                    --insecure \
                                    --skip-tls-verify  \
                                    --cleanup \
                                    --dockerfile ${WORKSPACE}/community-module/Dockerfile \
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
