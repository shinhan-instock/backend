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
  - name: kaniko-core
    image: gcr.io/kaniko-project/executor:v1.23.2-debug
    imagePullPolicy: Always
    command:
    - /busybox/cat
    tty: true
    volumeMounts:
      - name: jenkins-docker-cfg
        mountPath: /kaniko/.docker
  - name: kaniko-community
    image: gcr.io/kaniko-project/executor:v1.23.2-debug
    imagePullPolicy: Always
    command:
    - /busybox/cat
    tty: true
    volumeMounts:
      - name: jenkins-docker-cfg
        mountPath: /kaniko/.docker
  - name: kaniko-stock
    image: gcr.io/kaniko-project/executor:v1.23.2-debug
    imagePullPolicy: Always
    command:
    - /busybox/cat
    tty: true
    volumeMounts:
      - name: jenkins-docker-cfg
        mountPath: /kaniko/.docker
  - name: kaniko-piggybank
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
                script {
                    def changedFiles = sh(script: 'git diff --name-only HEAD~1', returnStdout: true).trim().split("\n")
                    echo "Changed files: ${changedFiles}"

                    def modules = [
                        'core-module': false,
                        'community-module': false,
                        'stock-module': false,
                        'piggyBank-module': false
                    ]

                    changedFiles.each { file ->
                        modules.each { module, shouldBuild ->
                            if (file.startsWith(module)) {
                                modules[module] = true
                            }
                        }
                    }

                    modules.each { module, shouldBuild ->
                        if (shouldBuild) {
                            echo "Building ${module}..."
                            sh "./gradlew :${module}:clean :${module}:build --no-daemon"
                        } else {
                            echo "Skipping ${module}"
                        }
                    }
                }
            }
        }

        // ✅ 변경된 파일 목록을 미리 가져옴
        stage('Check Git Changes') {
            steps {
                script {
                    env.CHANGED_FILES = sh(script: 'git diff --name-only HEAD~1', returnStdout: true).trim()
                }
            }
        }

        stage('Build & Push Docker Images') {
            parallel {
            stage('Build & Push core-module') {
                steps {
                    container('kaniko-core') {
                        script {
                            def changedFiles = env.CHANGED_FILES.split("\n")
                            def shouldBuild = changedFiles.any { it.startsWith("core-module/") }

                            if (shouldBuild) {
                                echo "🔨 core-module 변경 감지됨, 빌드 시작!"
                                sh "/kaniko/executor --context ${WORKSPACE}/core-module/ \
                                    --destination ${registry}/core-module:v1.${BUILD_ID} \
                                    --insecure \
                                    --skip-tls-verify  \
                                    --cleanup \
                                    --dockerfile ${WORKSPACE}/core-module/Dockerfile \
                                    --ignore-path=${WORKSPACE} \
                                    --verbosity debug"
                            } else {
                                echo "✅ core-module 변경 없음, 빌드 스킵!"
                            }
                        }
                    }
                }
            }

            stage('Build & Push community-module') {
                steps {
                    container('kaniko-community') {
                        script {
                            // 변경된 파일 목록 가져오기
                            def changedFiles = env.CHANGED_FILES.split("\n")
                            def shouldBuild = changedFiles.any { it.startsWith("community-module/") }

                            if (shouldBuild) {
                                echo "🔨 community-module 변경 감지됨, 빌드 시작!"

                                // JAR 파일 경로 확인
                                sh 'ls -al ${WORKSPACE}/community-module/build/libs/'

                                // Docker 이미지 빌드 및 푸시
                                sh """
                                    /kaniko/executor --context ${WORKSPACE}/community-module/ \
                                    --destination ${registry}/community-module:v1.${BUILD_ID} \
                                    --insecure \
                                    --skip-tls-verify  \
                                    --cleanup \
                                    --dockerfile ${WORKSPACE}/community-module/Dockerfile \
                                    --ignore-path=${WORKSPACE} \
                                    --verbosity debug
                                """
                            } else {
                                echo "✅ community-module 변경 없음, 빌드 스킵!"
                            }
                        }
                    }
                }
            }

            stage('Build & Push stock-module') {
                steps {
                    container('kaniko-stock') {
                        script {
                            // 변경된 파일 목록 가져오기
                            def changedFiles = env.CHANGED_FILES.split("\n")
                            def shouldBuild = changedFiles.any { it.startsWith("stock-module/") }

                            if (shouldBuild) {
                                echo "🔨 stock-module 변경 감지됨, 빌드 시작!"

                                // JAR 파일 경로 확인
                                sh 'ls -al ${WORKSPACE}/stock-module/build/libs/'

                                // Docker 이미지 빌드 및 푸시
                                sh """
                                    /kaniko/executor --context ${WORKSPACE}/stock-module/ \
                                    --destination ${registry}/stock-module:v1.${BUILD_ID} \
                                    --insecure \
                                    --skip-tls-verify  \
                                    --cleanup \
                                    --dockerfile ${WORKSPACE}/stock-module/Dockerfile \
                                    --ignore-path=${WORKSPACE} \
                                    --verbosity debug
                                """
                            } else {
                                echo "✅ stock-module 변경 없음, 빌드 스킵!"
                            }
                        }
                    }
                }
            }

            stage('Build & Push piggyBank-module') {
                steps {
                    container('kaniko-piggybank') {
                        script {
                            // 변경된 파일 목록 가져오기
                            def changedFiles = env.CHANGED_FILES.split("\n")
                            def shouldBuild = changedFiles.any { it.startsWith("piggyBank-module/") }

                            if (shouldBuild) {
                                echo "🔨 piggyBank-module 변경 감지됨, 빌드 시작!"

                                // JAR 파일 경로 확인
                                sh 'ls -al ${WORKSPACE}/piggyBank-module/build/libs/'

                                // Docker 이미지 빌드 및 푸시
                                sh """
                                    /kaniko/executor --context ${WORKSPACE}/piggyBank-module/ \
                                    --destination ${registry}/piggybank-module:v1.${BUILD_ID} \
                                    --insecure \
                                    --skip-tls-verify  \
                                    --cleanup \
                                    --dockerfile ${WORKSPACE}/piggyBank-module/Dockerfile \
                                    --ignore-path=${WORKSPACE} \
                                    --verbosity debug
                                """
                            } else {
                                echo "✅ piggyBank-module 변경 없음, 빌드 스킵!"
                            }
                        }
                    }
                }
            }
            }
        }

        stage('Checkout argocd & Modify deployment.yaml') {
            steps {
                script {
                    def changedFiles = sh(script: 'git diff --name-only HEAD~1', returnStdout: true).trim().split("\n")
                    echo "Changed files: ${changedFiles}"

                    def modules = [
                        'core-module': false,
                        'community-module': false,
                        'stock-module': false,
                        'piggyBank-module': false
                    ]

                    changedFiles.each { file ->
                        modules.each { module, shouldBuild ->
                            if (file.startsWith(module)) {
                                modules[module] = true
                            }
                        }
                    }
                    
                    modules.each { module, shouldBuild ->
                        if (shouldBuild) {
                            def lowerModule = module.toLowerCase()
                            withCredentials([gitUsernamePassword(credentialsId: 'github-jiwon', gitToolName: 'Default')]) {
                                sh "git config --global user.email 'belle021202@naver.com'"
                                sh 'git config --global user.name "jiwonchoe12"'
                                sh 'git checkout argocd'
                                sh 'git pull origin argocd'
                                sh 'git merge origin/main'
                                sh "sed -i 's|image: jiwonchoe/${lowerModule}:v1.*|image: jiwonchoe/${lowerModule}:v1.${BUILD_ID}|' ${module}/deployment.yaml"
                                sh "sed -i 's|image: jiwonchoe/${lowerModule}:v1.*|image: jiwonchoe/${lowerModule}:v1.${BUILD_ID}|' ${module}/gateway.yaml"
                                sh "sed -i 's|image: jiwonchoe/${lowerModule}:v1.*|image: jiwonchoe/${lowerModule}:v1.${BUILD_ID}|' ${module}/service.yaml"
                                sh "sed -i 's|image: jiwonchoe/${lowerModule}:v1.*|image: jiwonchoe/${lowerModule}:v1.${BUILD_ID}|' ${module}/virtualservice.yaml"
                                sh 'git add .'
                                sh 'git commit -m "Update Docker Image Version"'
                                sh 'git push origin argocd'
                            }
                        } else {
                            echo "Skipping ${module}"
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
            echo "🚨 빌드 실패! 로그를 확인하세요..."
        }
    }
}
