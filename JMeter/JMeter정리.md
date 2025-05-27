# Apache JMeter

![jmeter-logo](/JMeter/images/01_jmeter.png)

<br/>

## JMeter란?

JMeter는 Apache 재단에서 만든 오픈 소스 소프트웨어로, 부하 테스트 및 성능 측정을 위해 설계된 자바 애플리케이션이다.

JMeter는 정적 및 동적 리소스, 웹 애플리케이션의 성능 테스트에 사용할 수 있다. 서버, 서버 그룹, 네트워크 또는 객체에 과부하를 시뮬레이션해서 그 강도를 테스트하거나 다양한 부하 유형에서의 전반적인 성능을 분석하는데 활용된다.

<br/> 

### JMeter의 주요 특징

JMeter는 다양한 애플리케이션 서버, 프로토콜에 대한 성능 테스트가 가능하다.

- HTTP, HTTPS(Java, node.js, PHP 등), SOAP, REST API, FTP, SMTP, POP3, TCP 등 다양한 프로토콜을 지원한다.
- JMeter는 GUI를 제공하므로, 복잡할 수 있는 테스트 설정 및 실행, 결과를 시각적으로 확인할 수 있다.
- 분산 환경에서 성능 테스트를 실행할 수 있다.
- 다양한 리스너, 변수, 파라미터 등이 가능하기 때문에 복잡한 성능 테스트 시나리오를 구현할 수 있다.
- 여러 플러그인을 사용해서 추가적인 기능을 사용 가능하다. 원하는 기능을 <a href=”https://jmeter-plugins.org/wiki/Start/” target=”_blank”>JMeter Plugin Manager</a> 페이지에서 찾아 플로긍니을 설치하면 된다.

JMeter는 프로토콜 수준에서 작동하는 도구로, 브라우저가 아니다. 웹 서비스와 원격 서비스의 경우 브라우저처럼 보이지만, 브라우저가 지원하는 모든 동작을 수행하지는 않는다.

JMeter에서는 성능 테스트를 포함하는 여러 하위의 테스트를 진행할 수 있다.

- 성능 테스트는 시스템이 일정한 부하 아래에서 얼마나 효율적으로 동작하는지 측정하며, 웹 서버, DB 서버 등 여러 애플리케이션의 응답 시간을 측정하고 분석할 수 있다.
- 부하 테스트는 시스템이 정상적으로 동작할 수 있는 최대 부하(사용량)을 측정하며, 시스템에 가해지는 부하를 시뮬레이션해서 성능 문제를 찾아낼 수 있다.
- 스트레스 테스트는 시스템의 한계를 측정하며, 예상보다 높은 트래픽이나 부하가 걸렸을 때 시스템이 어떻게 반응하는지 확인할 수 있다.
- 사용자의 요청을 서버에 보내고 응답이 예상대로 오는지 확인하는 기능 테스트를 진행할 수도 있다.
- JMeter는 여러 대의 시스템에서 부하를 분산시켜 실행할 수도 있다.분산 테스트는 여러 대의 JMeter 클러스터를 구성해서 수행한다.

<br/>
<br/>

## JMeter 사용

<a href=”https://jmeter.apache.org/download_jmeter.cgi” target=”_blank”>JMeter 공식문서</a>에서 JMeter 바이너리 파일을 다운로드 해준다.

다운로드한 압축 파일을 해제하고 bin 디렉토리의 jmeter를 OS 환경에 맞게 실행해준다.

![jmeter-logo](/JMeter/images/02_jmeter-start.png)

<br/>

### Test Plan

성능 테스트를 진행하기 위해서 테스트 계획을 설정해야 한다. 스레드 및 HTTP Request 설정을 통해서 원하는 사용자의 수가 HTTP 요청을 보낼 수 있도록 할 것이다.

<br/>

### Thread Group

1. Test Plan을 우클릭
2. Add → Threads (Users) → Thread Group을 선택한다.

**Thread Group**은 테스트를 진행할 “사용자(스레드)”를 정의하고, 서버에 요청을 보내는 방식을 조정할 수 있다.

![스레드 그룹](/JMeter/images/03_thread-group.png)

- **Number of Threads**: 사용자(스레드) 수를 설정한다. 각 스레드는 독립적으로 서버에 요청을 보내는 가상 사용자로, 이 값이 클수록 더 많은 사용자가 동시에 요청을 보낸다.
- **Ramp-up Period**: 설정한 스레드가 테스트 시작 후 일정 시간 동안 점진적으로 실행되도록 설정한다. 10개의 스레드를 설정하고 Ramp-up 시간을 100초로 설정하면 10개의 스레드가 100초에 걸쳐 점진적으로 실행된다.  
  Ramp-up 시간이 짧으면 스레드가 급격히 증가해 서버에 큰 부하를 주게 되므로, 부하를 점진적으로 주려면 시간을 늘리면 된다.
- **Loop Count**: 각 스레드가 실행할 횟수를 설정한다. Infinite를 선택하면 무한으로 반복되며, Loop Count를 10으로 설정하면 각 스레드는 10번씩 요청을 보낸다.

<br/>

### Http Request

1. Thread Group 우클릭
2. Add → Sampler → Http Request 선택

**HTTP Request**는 JMeter에서 웹 서버에 요청을 보내는 샘플로, 웹 애플리케이션을 테스트할 때 HTTP 요청을 정의하는 데 사용된다.

![HTTP 요청](/JMeter/images/04_http-request.png)

- **Protocol**: 요청을 보낼 프로토콜을 선택한다.
- **Server Name or IP**: 요청을 보낼 대상 서버의 호스트명 또는 IP 주소를 설정한다. `www.google.com` 또는 `127.0.0.1`과 같은 값을 설정할 수 있다.
- **Port Number**: 서버의 포트 번호를 설정한다.
- **Method**: HHTP 요청 메서드를 설정한다.
- **Path**: 요청을 보낼 리소스의 경로를 설정한다.
- **Parameters**: HTTP 요청에 전달할 매개변수를 설정한다. QueryParameter, Form Value를 설정할 수 있다.
- **Body Data**: Post, Put, Patch 등의 요청 시 본문에 포함할 데이터를 설정한다.

<br/>

### 결과 확인하기

결과를 확인할 때는 기존에 제공되어있는 리스너를 통해 확인하거나 플러그인으로 원하는 결과 리포트들을 추가할 수 있다.

우선 기본적으로 제공하는 Summary Report와 View Results Tree를 확인한다.

1. Thread Group 우클릭
2. Add → Listener → Summary Report, View Results Tree 선택

이제 위에서 설정한 Thread Group과 HTTP Request 설정을 끝냈다면 상단 옵션들에서 Start 버튼을 선택한다. 그리고 테스를 진행하면서 리스너를 보면서 결과를 확인해주면 된다.

JMeter가 제공하는 결과 리포트 외에도 에러가 발생한 부분이나 애플리케이션이 제대로 동작하지 않는 부분은 애플리케이션 모니터링 시스템을 구축했으면 모니터링 시스템을 확인하고, 로그 파일을 확인해서 상세한 내용을 확인하면 된다.

<br/>

**Summary Report**

![Summary Report](/JMeter/images/05_summary-report.png)

<br/>

**View Result Tree**

![Summary Report](/JMeter/images/06_view-results-tree.png)

<br/>

지금까지 JMeter를 사용해서 성능 테스트를 진행했다. 지금은 가장 기본적인 방법으로 GET 요청만 테스트하고 결과를 확인했는데, Timer 설정이나 플로그인에서 제공되는 여러 추가 리스너들을 확인해서 결과를 확인할 수 있으니 이 부분은 공식 문서를 참조해서 확인하면 좋을 것 같다.

<br/>