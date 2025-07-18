
def ecrLoginHelper = "docker-credential-ecr-login"

// 젠킨스의 선언형 파이프라인 정의부 시작 (그루비 언어)
pipeline {
    agent any // 어느 젠킨스 서버에서나 실행이 가능
    environment {
        SERVICE_DIRS = "back/config-service,back/discovery-service,back/gateway-service,back/user-service,back/review-service,back/point-service,back/restaurant-service"
        ECR_URL = "221082190600.dkr.ecr.ap-northeast-2.amazonaws.com"
        REGION = "ap-northeast-2"
    }
    stages {
        // 각 작업 단위를 스테이지로 나누어서 작성 가능.
        stage('Pull Codes from Github') {
            steps {
                checkout scm // 젠킨스와 연결된 소스 컨트롤 매니저(git 등)에서 코드를 가져오는 명령어
            }
        }
        stage('Detect Changes') {
            steps {
                script {
                    // rev-list: 특정 브랜치나 커밋을 기준으로 모든 이전 커밋 목록을 나열
                    // --count: 목록 출력 말고 커밋 개수만 숫자로 반환
                    def commitCount = sh(script: "git rev-list --count HEAD", returnStdout: true)
                                        .trim()
                                        .toInteger()
                    def changedServices = []
                    def serviceDirs = env.SERVICE_DIRS.split(",")
                    if (commitCount == 1) {
                        // 최초 커밋이라면 모든 서비스 빌드
                        echo "Initial commit detected. All services will be built."
                        changedServices = serviceDirs // 변경된 서비스는 모든 서비스다
                    } else {
                        // 변경된 파일 감지
                        def changedFiles = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true)
                                            .trim()
                                            .split('\n') // 변경된 파일을 줄 단위로 분리

                        echo "Changed files: ${changedFiles}"

                        changedServices = []
                        serviceDirs = env.SERVICE_DIRS.split(",")

                        serviceDirs.each { service ->
                            if (changedFiles.any { it.startsWith(service + "/") }) {
                                changedServices.add(service)
                            }
                        }
                    }


                    env.CHANGED_SERVICES = changedServices.join(",")
                    if (env.CHANGED_SERVICES == "") {
                        echo "No changes detected in service directories. Skipping build and deployment"
                        // 성공 상태로 파이프라인을 종료
                        currentBuild.result = 'SUCCESS'
                    }
                }
            }
        }
        stage('Build Changed Services') {
            // 이 스테이지는 빌드되어야 할 서비스가 존재한다면 실행되는 스테이지.
            // 이전 스테이지에서 세팅한 CHANGED_SERVICES라는 환경변수가 비어있지 않아야만 실행.
            when {
                expression { env.CHANGED_SERVICES != "" }
            }
            steps {
                script {
                    def changedServices = env.CHANGED_SERVICES.split(",")
                    changedServices.each{ service ->
                        sh """
                        echo "Building ${service}..."
                        cd ${service}
                        chmod +x ./gradlew
                        ./gradlew clean build -x test
                        ls -al ./build/libs
                        cd ../..
                        """
                    }
                }
            }
        }
        stage('Build Docker Image & Push to AWS ECR') {
            when {
                expression { env.CHANGED_SERVICES != "" }
            }
            steps {
                script {
                    withAWS(region: "${REGION}", credentials: "aws-key") {
                        def changedServices = env.CHANGED_SERVICES.split(",")
                        changedServices.each { service ->
                            def serviceName = service.replaceFirst(/^back\//, "")
                            sh """
                            curl -O https://amazon-ecr-credential-helper-releases.s3.us-east-2.amazonaws.com/0.4.0/linux-amd64/${ecrLoginHelper}
                            chmod +x ${ecrLoginHelper}
                            mv ${ecrLoginHelper} /usr/local/bin/

                            mkdir -p ~/.docker
                            echo '{"credHelpers": {"${ECR_URL}": "ecr-login"}}' > ~/.docker/config.json

                            docker build -t ${service}:latest ${service}
                            docker tag ${service}:latest ${ECR_URL}/${serviceName}:latest
                            docker push ${ECR_URL}/${serviceName}:latest
                            """
                        }
                    }

                }
            }
        }
//         stage('Deploy Changeed Services to AWS EC2') {
//             when {
//                 expression { env.CHANGED_SERVICES != "" }
//             }
//             steps {
//
//             }
//         }
    }
}