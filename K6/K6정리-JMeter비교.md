# k6 개념 및 사용법, JMeter와 비교

<a href="https://dawncode.tistory.com/27" target="_blank">이전 포스팅</a>에서 JMeter를 사용한 성능 테스트를 진행했다. 이번에는 k6에 대해서 알아보고 k6의 사용방법 및 JMeter와 어떤 차이가 있는 확인할 것이다.

<br/>

## k6란?

![01_k6-logo.png](/K6/images/01_k6-logo.png)

<br/>

k6는 현재 Grafana Labs의 제품 중 하나로 오픈 소스이며, 개발자 친화적이고 확장 가능한 부하 테스트 도구이다.

k6를 사용해서 애플리케이션과 인프라의 성능을 테스트할 수 있으며, 자바스크립트 기반의 스크립트를 통해 시나리오를 작성하고, CLI 환경에서 실행하여 HTTP 요청에 대한 부하를 시뮬레이션할 수 있다.

k6는 성능 테스트를 "가상 사용자"로 시뮬레이션한다. 각 가상 사용자는 지정된 시간 동안 웹 애플리케이션에 대한 요청을 반복하거나 병렬로 보내며, 이를 통해 시스템의 성능과 반응 시간을 측정한다.

<br/>

### k6의 사용 목적

- **부하 및 성능 테스트**: k6는 최소한의 리소스를 사용해서 최적화되어 있으며 스파이크, 스트레스, soak 테스트와 같은 고부하 성능 테스트를 실행하기 위해 설계됐다.
- 브라우저 성능 테스트: k6 브라우저 API를 통해 브라우저 기반 성능 테스트를 진행하고 브라우저와 관련된 성능 문제를 식별할 수 있는 브라우저 메트릭을 수집할 수 있다. 또한 브라우저 테스트를 다른 성능 테스트와 혼합해서 웹 성능을 포괄적으로 살펴볼 수 있다.
- 성능 및 종합 모니터링: 테스트를 최소 부하로 매우 빈번하게 실행해서 프로덕션 환경의 성능과 가용성을 지속적으로 검증할 수 있다. 이를 위해 Grafana Cloud Synthetic Monitoring을 사용할 수 있으며, 이 서비스는 k6 스크립트 실행을 지원한다.
- 성능 테스트 자동화: k6는 CI/CD 및 자동화 도구와 원화하게 통합되어 엔지니어링 팀이 개발 및 배포 주기의 일부로 성능 테스트를 자동화할 수 있도록 한다.
- 장애 내성 및 회복력 테스트: 장애 실험의 일환으로 트래픽을 시뮬레이션하고, xk6-disruptor를 사용하여 Kubernetes에 다양한 종류의 오류(결함)를 주입할 수 있다.
- 인프라 테스트: k6 extentions을 통해 새로운 프로토콜을 지원하거나 특정 클라이언트를 사용해 인프라 내의 개별 시스템을 직접 테스트할 수 있다.

<br/>

### k6의 특징

- k6는 자바스크립트로 테스트 시나리오를 작성한다.
- CLI 기반이기 때문에 터미널에서 명령어를 통해 테스트를 실행할 수 있다.
- 단일 시스템뿐만 아니라 여러 시스템에서 부하 테스트를 실행할 수 있는 분산 환경을 지원하며, 클라우드 환경에서 대규모 부하 테스트를 실행하여 성능 저하나 병목 현상을 확인할 수 있다.
- 테스트 실행 중 실시간으로 여러 성능 지표를 모니터링할 수 있다.
- k6는 가볍다. CPI와 메모리 자원을 적게 사용하면서 성능 테스트를 실행할 수 있다.
- CI/CD 환경과 쉽게 통합될 수 있다. GitHub Actions, Jenkins, GitLab CI 등의 도구와 연동하여, 코드 변경 시 자동으로 성능 테스트가 실행되도록 설정하여 성능 테스트를 자동화할 수 있다.
- 테스트 결과를 콘솔 출력, JSON 외의 다양한 형식으로 출력할 수 있다.

<br/>

## k6와 JMeter 비교

### 사용성

- JMeter는 GUI 기반의 인터페이스를 제공하여 사용자에게 편리함을 제공하여, 프로그래밍 언어를 몰라도 편하게 사용할 수 있다. 하지만 GUI를 사용해도 JMeter를 설정하는 파일은 XML 이므로 설정 정보만을 확인할 때 가독성이 떨어지기 때문에 불편함이 있을 수 있다.

- k6는 자바스크립트를 기반으로 테스트 시나리오를 작성하며, CLI에서 실행하기 때문에 자바스크립트 언어 및 CLI를 사용할 줄 알아야 한다. 자바스크립트 언어를 안다면 보다 깔끔한 코드로 테스트 설정을 구성하고 확인할 수 있다.

<br/>

### 성능

- JMeter는 Java 언어로 개발되어 JVM에서 실행되기 때문에 메모리 사용량이 많아 대규모 부하 테스트에서 시스템의 리소스를 많이 소비할 수 있다. CLI 모드를 사용하면 GUI 모드보다는 아니지만 결국 JVM 에서 실행되므로, 성능에 영향을 미칠 수 있다.

- k6는 Go 언어로 개발된 경량화된 도구로, JVM을 사용하지 않기 때문에 상대적으로 낮게 시스템 리소스를 사용한다. 대규모 테스트에서도 시스템 자원을 효율적으로 사용하며, 보다 적은 부하로 테스트를 진행할 수 있다. 또한 CLI로 테스트를 하기 때문에 GUI보다 부하가 적다.

<br/>

### 확장성

- JMeter는 분산 테스트를 지원한다. 여러 대의 머신으로 부하를 분산시킬 수 있지만 복잡한 설정을 구성해야 한다.

- k6는 클라우드 기반의 분산 테스트를 지원한다. k6 cloud 서비스를 통해 쉽게 확장할 수 있으며, 대규모 부하 테스트를 클라우드 환경에서 쉽게 실행할 수 있다.

<br/>

### 테스트 결과

- JMeter는 GUI를 통해 테스트 결과를 실시간으로 그래프, 테이블, 로그 등의 형시으로 제공하며 CSV, XML 형식으로 결과를 저장할 수 있다.

- k6는 CLI 기반으로 콘솔에 결과를 실시간으로 출력할 수 있고, 프로메테우스나 그라파나와 연동하여 시각화, 분석을 할 수 있다.

<br/>

결론적으로 규모가 크지 않은 환경에서 GUI를 사용한 편리한 테스트를 한다면 JMeter를 사용해도 무리가 없고, 보다 적은 자원으로 스크립트 기반의 대규모 테스트가 필요하면 k6를 사용하는 것이 유리할 것이다.

<br/>

## k6 사용하기

k6를 설치하는 과정은 <a href="https://grafana.com/docs/k6/latest/set-up/install-k6/" target="_blank">k6 공식문서의 설치 방법</a>을 확인한다. 맥 사용자는 Homebrew나 도커를 사용하면 편할 것이다.

<br/>

### 테스트 스크립트 생성

테스트를 진행하기 위해서 자바스크립트 코드를 작성해야 한다. 스크립트 파일은 직접 만들거나 `k6 new` 명령어를 사용하면 `script.js` 파일이 생성된다.

`k6 new` 명령어로 스크립트 파일을 생성하면 기본 코드가 들어가 있는데 기본 동작을 사용하려면 url 경로를 수정해서 테스트 해봐도 된다.

```jsx
import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  vus: 10,
  duration: '30s',
};

export default function() {
  let res = http.get('https://quickpizza.grafana.com');
  check(res, { "status is 200": (res) => res.status === 200 });
  sleep(1);
}
```

<br/>

이제 스크립트 파일에 원하는 옵션과 함수를 정의해서 성능 테스트를 진행해본다.

<br/>

### 스크립트 작성

```javascript
import http from "k6/http"; // HTTP 요청을 위한 http 모듈을 가져옴
import { sleep, check } from "k6"; // 테스트 스크립트에 지연을 위한 sleep 함수

export const options = {
  vus: 5, // 가상 사용자 수
  duration: "30s", // 테스트 진행 시간
  iterations: 10, // 기본 함수의 총 반복 횟수를 지정
  
  // ramp-up 기간 설정
  // 테스트 시작 시 가상 사용자가 점진적으로 변하는 시간
  // stages: [
  //   { duration: "10s", target: 30 }, // 10초 동안 30명까지 증가
  //   { duration: "1m", target: 30 }, // 1분 동안 30명 유지
  //   { duration: "10s", target: 50 },  // 10초 동안 50명까지 증가
  // ],
  
  // 테스트 측정 항목의 통과, 실패 기준치
  thresholds: {
    // 테스트 실패율은 10% 미만이어야 한다.
    "http_req_failed": ["rate<0.01"],
  },
};

// export default로 내보내진 함수는 k6에 의해서 테스트 스크립트의 진입점으로 선택된다. 테스트가 진행되는 동안 설정된 사항에 따라서 실행된다.
export default function() {
    // GET 요청 전송
    const response = http.get('http://localhost:8080/v1/posts');

    const checkResult = check(response, {
      // HTTP 응답 상태 코드가 200인지 확인
      "is status 200": (res) => res.status === 200,
      // 응답 시간이 2000ms 이하인지 확인한다.
      "response time is less than 2000ms": (res) => res.timings.duration < 2000,  
    });

    // check는 true/false를 반환하므로 필요 시 로직에 활용 가능
    if (!checkResult) {
      console.log("$checkResult: ${checkResult}");
    }

    sleep(1); // 1초 대기
}


/*
// post 요청 참조
export default function () {
  const url = "http://localhost.com/v1/posts";
  const payload = JSON.stringify({
    title: "글 제목",
    content: "글 내용",
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  http.post(url, payload, params);
}
*/
```

<br/>

k6는 내부적으로 자바스크립트 모듈 시스템을 사용하여 설정과 실행 로직을 분리한다.   
k6는 실행 시 `options` 객체와 `default`로 지정된 함수를 찾아 실행하므로, 반드시 `export`를 사용해서 설정 및 함수를 외부에서 인식할 수 있게 해야 한다.  

<br/>

### options 객체 설정

스크립트의 **`options` 객체**에서 여러 옵션을 설정할 수 있다. 옵션은 CLI 플래그, 환경 변수, `—-config` 플래그로 설정 파일에 설정된 옵션을 사용할 수도 있다.

우선 순위는 CLI 플래그(가장 높음) → 환경 변수 → 스크립트의 `options` 객체 → `—-config` 플래그로 설정 파일에 설정된 옵션 → 기본 값(가장 낮음) 순서이다.

<br/>

코드에 작성된 옵션 외의 더 많은 옵션 설정은 <a href="https://grafana.com/docs/k6/latest/using-k6/k6-options/reference/" target="_blank">k6 옵션 설정</a>을 참조한다.

- **`vus`**: 동시에 최대 몇 명의 가상 사용자(Virtual Users)가 요청을 보낼지 설정한다.
- **`duration`**: 테스트를 수행할 총 시간(duration)을 설정한다. 이 시간 동안 `vus` 수만큼의 가상 유저가 동시에 시나리오를 반복 실행한다.
  - k6는 지정된 `duration` 동안 `vus`가 요청을 보내는데, `duration`이 `vus`보다 작으면 `vus`가 요청을 보내는 동안 지속할 수 없기 때문에, 자연스럽게 `duration`은 `vus`보다 작은 값일 수 없다.
- **`iterations`**: 가상 사용자가 얼마나 요청을 반복할지 설정한다.
- **stages**: ramp-up 기간을 설정하며, 시간에 따라 사용자 수를 어떻게 변화시킬지를 설정한다.
  - stages 옵션을 코드에서 주석 처리한 이유는 iterations, duration 옵션과 함께 사용할 수 없으므로 주의해야 한다. 같이 사용하면 스크립트 실행 시 에러가 발생한다. 
    
<br/>

### export default function()  

http 모듈을 통해서 HTTP Request 메서드를 사용할 수 있다. get, post, put, del 등의 메서드가 있다.  

k6의 Check 기능은 성능 테스트 중에 시스템이 기대한 대로 응답하는지를 검증하는 데 사용된다. 
테스트 프레임워크의 `assert`와 비슷하지만, 중요한 차이점은 Check가 실패하더라도 테스트가 중단되거나 실패 상태로 종료되지 않는다는 점이다. 
대신, 실패한 체크의 비율이 기록되고 테스트는 계속 실행된다.  
각 체크는 rate metric을 생성하여 실패율을 추적할 수 있게 한다. 만약 체크 실패 시 테스트를 실패 처리하고 싶다면, Thresholds(임계값) 기능과 함께 사용해야 한다.  

`function()`에 작성된 기능들은 주석으로 설명했다. 위 설명과 함께 확인하면 이해하는데 어려움이 없을 것이다.

그 밖에도 `options` 객체에서 사용가능한 옵션들과 `function()` 함수에도 다양한 테스트를 진행할 수 있다. <a href="https://grafana.com/docs/k6/latest/using-k6/" target="_blank">k6 공식 문서</a>를 확인해서 자신이 테스트하고자 하는 결과를 위한 요소들을 찾아서 적용하면 된다.  

<br/>

### 테스트 실행하기 

테스트 설정을 위한 자바스크립트 파일을 작성했다면 이제 CLI 명령어를 통해서 테스트를 실행할 수 있다.

`k6 run script.js` 가장 기본적은 명령어로, 명령어를 사용하면 지정한 테스트 스크립트를 실행한다.

![result](/K6/images/02_스크립트-결과.png)

<br/>

테스트에서 요청을 보냈을 때 응답으로 오는 response 값을 JSON으로 파싱한 값이다. 

![03_response-data.png](/K6/images/03_response-data.png)  

<br/>

그 밖의 CLI 명령어를 몇가지만 소개한다.

```bash
# k6 테스트 스크립트를 초기화한다.
# 새로운 테스트를 시작할 때 유용하며, 이를 통해 K6의 기본 구조를 빠르게 만들 수 있다.
k6 init

# k6에서는 Summary Report를 기본적으로 제공하며, 테스트 종료 후 상세한 데이터를 JSON 형식으로 출력할 수 있다. 
# 이는 --summary-export 옵션을 사용하여 별도의 파일로 저장할 수 있다.
k6 run --summary-export=result.json script.js

# 테스트 결과를 특정 출력 형식으로 저장하거나 보내기 위해 사용한다.
# 아래 명령어는 테스트 결과를 JSON 파일 형태로 results.json에 저장하는 명령어이다.
k6 run --out json=results.json script.js

# K6 Cloud에서 성능 테스트를 실행한다.
k6 cloud script.js

# 실행 중인 성능 테스트의 실시간 통계를 확인한다.
k6 stats

# 실행 중인 K6 테스트를 종료한다.
k6 stop

# K6의 버전 정보를 확인한다.
k6 version
```

<br/>

지금까지 k6의 사용방법을 확인했다. 스크립트 파일에 적용되는 옵션들과 함수에 작성 가능한 내용 그리고 CLI 명령어들은 k6를 사용해보면서 추가해보면 좋을 것 같다.  
