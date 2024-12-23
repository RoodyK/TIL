# 젠킨스 파이프라인 정리 및 깃허브 SSH 연동

젠킨스 파이프라인을 정리하고, 이전에 도커로 젠킨스를 설치해서 깃허브를 SSH로 연동했었는데, 이번에는 젠킨스의 파이프라인을 사용해서 깃허브와 SSH 연동을 해볼것이다.    

<br/>

도커로 젠킨스 설치 및 깃허브의 SSH 설정 및 Credentials 설정은 <a href="https://dawncode.tistory.com/20" target="_blank">이전 포스팅</a>을 참조한다.  

<br/>

## 젠킨스 파이프라인이란?

젠킨스 파이프라인(Jenkins Pipeline)은 지속적인 전달 파이프라인을 구현하고 통합하는 것을 지원하는 플러그인의 모음이다.    
젠킨스 파이프라인을 통해 CI/CD 작업을 코드로 정의하며 빌드, 테스트, 배포 등의 과정을 자동화할 수 있다.  

파이프라인은 파이프라인 DSL 구문을 통해 간단한 것부터 복잡한 전달 파이프라인까지 코드로 모델링할 수 있는 확장 가능한 도구 모음을 제공한다.  

<br/>

이전에 젠킨스를 깃허브와 SSH 연동을 Freestyle Project를 통해서 진행했는데, 이 방법은 UI로 대부분의 설정을 하기 때문에 코드 작성없이 빌드 작업을 구성할 수 있고, 직관적이기 때문에 처음 사용할 때 접근성이 좋다.  

젠킨스 파이프라인을 사용하면 Jenkinsfile을 통해서 빌드부터 배포까지의 과정을 코드로 작성하며 각 구간별 복잡한 작업 설정을 구체적으로 할 수 있고, 조건부 실행이나 여러 작업을 병렬로 처리해서 전체 빌드 시간을 단축할 수 있다. 그리고 파이프라인은 젠킨스의 UI를 통해 빌드과정을 시각적으로 확인하면서 전체 프로세스의 흐름을 이해하기 쉽다.  

<br/>
<br/>

## 왜 파이프라인인가?

젠킨스는 기본적으로 여러 자동화 패턴을 지원하는 자동화 엔진이다. 파이프라인은 젠킨스에 강력한 자동화 도구 세트를 추가해 단순한 지속적인 통합부터 포괄적인 CD 파이프라인에 이르는 사용 사례를 지원한다. 일련의 관련된 작업을 모델링함으로써 사용자는 파이프라인의 많은 기능을 활용할 수 있다.

- 코드: 파이프라인은 코드로 구현되며 보통 소스 제어에 체크인되므로 팀이 배포 파이프라인을 편집하고, 검토하며, 반복할 수 있는 능력을 제공한다.
- 내구성: 파이프라인은 Jenkins 컨트롤러의 계획된 재시작 및 계획되지 않은 재시작을 모두 견딜 수 있다.
- 일시 중지 가능: 파이프라인은 선택적으로 중단되어 사용자 입력이나 승인을 기다린 후 파이프라인 실행을 계속할 수 있다.
- 다재다능: 파이프라인은 포크/조인, 루프, 병렬 작업 수행 등의 복잡한 실제 CD 요구 사항을 지원한다.
- 확장성: 파이프라인 플러그인은 DSL에 대한 맞춤 확장을 지원하며 다른 플러그인과의 통합을 위한 여러 옵션을 제공한다.  

아래 흐름도는 젠킨스 파이프라인에서 쉽게 모델링된 하나의 CD 시나리오 예이다.

![파이프라인 흐름도](/Jenkins/images/pipeline/01_젠킨스파이프라인_흐름도.png)

<br/>
<br/>

## 선언형 파이프라인과 스크립트형 파이프라인

젠킨스 파이프라인은 선언형 파이프라인(Declarative Pipeline)과 스크립트형 파이프라인(Scripted Pipeline)이 있다. 두 가지 문법은 호환되지 않으므로 주의해야 한다.

<br/>

### 선언형 파이프라인

선언형 파이프라인은 간결하고 구조화된 구문을 사용해서 파이프라인을 정의하는 방식이다. 코드가 직관적이고, 빌드 프로세스의 각 단계가 이해하기 쉽게 구조화되어 있다.  

- `pipeline {}` 블록이 최상위 레벨에 존재해야 한다.
- 각 단계는 stages와 steps 블록으로 구조화한다.
- 복잡한 스크립트 작성 없이 파이프라인의 주요 기능을 쉽게 정의 가능하다.
- 재시도, 시간 제한, 조건부 실행 등 다양한 기능을 기본으로 제공한다.

<br/>
<br/>

### 스크립트형 파이프라인

스크립트형 파이프라인은 그루비(Groovy) 스크립트 언어를 사용해서 파이프라인을 정의하는 방식이다. 그루비 언어에서 제공하는 대부분의 기능은 스크립트형 파이프라인에서 사용자에게 제공된다.  
선언형 파이프라인과 달리, 스크립트형 파이프라인은 코드를 작성하는 개발자가 원하는대로 자유롭게 작성할 수 있어 유연하게 작성가능하며 복잡한 빌드 로직이나 조건 처리를 할 수 있지만, 그루비 문법을 알아야 한다는 점이 오히려 복잡성을 증가시킬 수 있다.  

- `node {}` 블록이 최상위 레벨에 존재해야 한다.
- Groovy 언어의 모든 기능을 사용할 수 있다.
- 다양한 상황에 맞춰 파이프라인을 코드를 세밀하게 작성할 수 있다.
- 복잡한 코드로 가독성이 떨어지고, 표준화된 구조가 없으므로 유지보수성이 낮아진다.

<br/>
<br/>

젠킨스에서는 두 가지 파이프라인 방식이 있지만 여기서는 선언형 파이프라인 코드로 작성하고 스크립트형 파이프라인 코드는 비교를 위해 마지막에 추가해놓을 것이다.  

<br/>
<br/>

## 젠킨스 파이프라인으로 깃허브 SSH 연동

젠킨스 파이프라인은 젠킨스 메인 페이지(대시보드)에서 New Item의 Pipeline 메뉴를 통해서 생성할 수 있다.    

![파이프라인 생성](/Jenkins/images/pipeline/02_젠킨스파이프라인_생성.png)

<br/>
<br/>

### 젠킨스 파이프라인 설정

**General** 설정에서 파이프라인의 빌드가 동시에 실행되지 않게 설정, 중단된 파이프라인이 재시작되지 않게 설정, 깃허브 프로젝트 명시, 파이프라인 속도와 내구성을 조정 등의 기능들을 설정할 수 있다.  

**Build Triggers** 설정에서는 다른 프로젝트가 완료 됐을 때 현재 프로젝트의 빌드를 트리거 하거나, 리눅스 Cron을 통한 빌드 주기 설정, 깃허브의 웹훅 설정 등의 트리거들을 설정할 수 있다.  

<br/>
<br/>

### 파이프라인 코드 작성

**Pipeline** 설정에서 파이프라인 코드를 작성하면 된다. 우선 선언형 파이프라인 코드를 작성한다.  

```groovy
pipeline {
    agent any

    environment {
        CREDENTIALS_ID = "github-ssh"
        REPO_URL = "git@github.com:RoodyK/jenkins-practice.git"
        GIT_BRANCH = "main"
    }

    stages {
        stage("Checkout") {
            steps {
                echo "Checkout Github Project"
                git branch: "${GIT_BRANCH}", credentialsId: "${CREDENTIALS_ID}", url: "${REPO_URL}"
            }
        }
        stage("Build") {
            steps {
                echo "Project Build"
                sh "./gradlew clean build"
            }
        }
        stage("Deploy") {
            steps {
                sh "echo $WORKSPACE"
                sh "ls -arlth '$WORKSPACE/build/libs'"
            }
        }
    }
    post {
        always {
            echo "Always Run After Build"
        }
        success {
            echo "Build Success"
        }
        failure {
            echo "Build Fail"
        }
    }
}
```

파이프라인 코드를 간단히 설명한다. 

- `pipeline {}` 블록을 최상위에 작성하며, 모든 파이프라인 코드는 이 안에 작성된다.
- `agent` 파이프라인은 특정 스테이지가 실행될 노드나 환경을 정의하는데 사용되며 any값은 어떤 환경도 가능하다는 의미다.
- `environment` 파이프라인 블록안에는 전체 파이프라인에서 사용될 환경 변수를 정의한다.
- `stages`는 파이프라인의 주요 단계를 정의하는 블록으로 각 단계를 `stage`로 정의하고 `steps`로 각 단계에서 수행할 작업을 정의한다.
- `stage` 에서 "Checkout", "Build", "Deploy"는 정해진 값이 아니며, 관례를 정해서 사용하는 것이 좋다. 
- "Checkout" 단계에서 깃허브에서 소스코드를 가져오고, "Build" 단계에서 프로젝트를 빌드하고, "Deploy" 단계에서 프로젝트의 빌드된 파일이 존재하는지 확인한다.
- `post` 블록에서 스테이지를 실행 후에 수행할 작업을 정의한다. always 블록은 항상 실행되고, success 블록은 파이프라인이 성공하는 경우에만 실행되고, failure 블록은 파이프라인이 실패했을 경우에 실행된다.

이제 파이프라인 코드를 작성하고 저장한다.  

<br/>

### 파이프라인 빌드 결과 확인

저장한 파이프라인 코드를 이미지와 같이 빌드한 뒤 빌드한 결과를 클릭한다.  

![파이프라인 빌드](/Jenkins/images/pipeline/03_파이프라인_빌드.png)  

<br/>

빌드한 파이프라인의 결과를 확인하면 "Console Output" 메뉴로 전체 결과를 확인할 수도 있고, "Pipeline Console"에서 각 단계별 결과를 구분해서 확인할 수 도 있다. 그리고 Workspace 메뉴로 빌드 결과로 생성된 파일도 확인할 수 있다.  

![파이프라인 빌드결과](/Jenkins/images/pipeline/04_빌드결과.png) 

<br/>

아래는 "Pipeline Console" 메뉴로 확인한 각 단계별 결과로 파이프라인 코드에서 정의한 stage명과 단계별로 실행된 결과 코드를 확인할 수 있다.  

![파이프라인 단계별결과](/Jenkins/images/pipeline/05_단계별_빌드결과.png)  

<br/>

### 스크립트형 파이프라인 코드 

위에서 작성한 선언형 파이프라인 코드를 스크립트형 파이프라인 코드로 작성한다. 그루비 문법을 사용하며 자세한 설명은 생략한다.  
스크립트형 파이프라인은 try-catch-finally 절을 사용해서 코드를 작성하고 예외를 처리할 수 있다.  

```groovy
node {
    // workspace clean
    deleteDir()
    
    def gitUrl = "git@github.com:RoodyK/jenkins-practice.git"
    def credentialsId = "github-ssh"
    
    try {
        stage("Checkout") {
            echo "Git SSH Checkout"
            checkout([
                $class: "GitSCM",
                branches: [[name: "*/main"]],
                userRemoteConfigs: [[
                    url: gitUrl,
                    credentialsId: credentialsId
                ]]
            ])
        }
        stage("Build") {
            echo "Project Build"
            sh "./gradlew clean build"
        }
        stage("Deploy") {
            sh "echo $WORKSPACE"
            sh "ls -arlth '$WORKSPACE/build/libs'"
        }
    } catch (Exception e) {
        sh "echo 'Build Fail: ${e.message}'"
    }
}
```

코드에서 사용된 checkout() 함수는 SCM(Source Code Management) 플러그인을 통해 동작하며, 일반적으로 Git SCM 플러그인을 통해 Git 리포지토리와 상호작용할 때 주로 사용된다.   

<br/>

지금까지 젠킨스 파이프라인을 사용해서 깃허브와 연동하는 방법을 설명했다. 다음에 파이프라인 문법과 예외처리를 한번 정리할 것이다.

<br/>
<br/>