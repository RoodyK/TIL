# Dockerfile

이미지를 생성하고, 최적화하기 위한 Dockerfile에 대해 알아본다.  

<br/>

## Dockerfile이란?

Dockerfile은 도커 이미지를 생성하기 위해 명령어들을 정의한 텍스트(스크립트) 파일이다. 도커 파일에 정의된 명령어들은 이미지의 빌드 과정에서 실행되며, 도커 CLI를 통해 빌드해서 이미지를 생성할 수 있다.

도커파일은 인스트럭션과 명령어(스크립트), 주석으로 구성된다. 도커파일에서 인스트럭션이 이미지에 포함된 각 이미지 레이어가 되며, 인스트럭션을 실행한 결과로 이미지가 만들어진다.  

<br/>

### Dockerfile 만들기

예제에서 사용할 자바스크립트 파일 server.js는 다음과 같다.
```js
const express = require("express");

const PORT = 9090;

const app = express();

app.get("/", (req, res) => {
    res.send("Hello World");
});

app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});
```
<br/>

간단한 Dockerfile을 구성해보자. 

```bash
# 베이스 이미지를 Node.js 16 버전으로 지정한다.
FROM node:16

# 작업 디렉토리를 '/app'으로 설정한다.
WORKDIR '/app'

# 현재 디렉토리의 모든 파일을 컨테이너의 현재 작업 디렉토리로 복사한다.
COPY . .

# 의존성을 설치한다.
RUN npm install

# 'server.js' 스크립트로 애플리케이션을 시작한다.
CMD ["node", "server.js"]
```

도커파일의 인스트럭션에 대한 설명은 주석으로 남겼다.  

도커파일의 인스트럭션은 파일을 작성하면서 다른 인스트럭션들도 사용할 것이고, 다양한 인스트럭션을 후에 설명할 것이다.  그리고 캐싱을 통한 도커파일 최적화까지 알아볼 것이다.

<br/>

### 도커 이미지 빌드

이제 작성 도커 파일로 이미지를 한 번 만들 것이다. 이미지를 빌드한다.
```bash
docker image build -t sample .
```

- t 또는 --tag: 이 옵션은 이미지에 지정할 태그를 설정한다. 이 값은 보통 이미지의 버전을 지정할 때 사용한다. 기본값은 latest다.
- 명령어는 현재 디렉토리(.)에 있는 도커파일을 읽어서 이미지 이름은 sample 로 이미지를 빌드한다는 의미다.

build 명령을 실행하면 도커파일 스크립트에 포함된 인스트럭션이 차례되로 실행되면서 그 결과가 출력된다.  

<br/>

```bash
[+] Building 14.4s (11/11) FINISHED                                                                    docker:desktop-linux
 => [internal] load build definition from dockerfile                                                                   0.0s
 => => transferring dockerfile: 262B                                                                                   0.0s
 => [internal] load metadata for docker.io/library/node:16                                                             2.0s
 => [auth] library/node:pull token for registry-1.docker.io                                                            0.0s
 => [internal] load .dockerignore                                                                                      0.0s
 => => transferring context: 2B                                                                                        0.0s
 => [1/5] FROM docker.io/library/node:16@sha256:f77a1aef2da8d83e45ec990f45df50f1a286c5fe8bbfb8c6e4246c6389705c0b      10.4s
 => => resolve docker.io/library/node:16@sha256:f77a1aef2da8d83e45ec990f45df50f1a286c5fe8bbfb8c6e4246c6389705c0b       0.0s
 ...
 => [internal] load build context                                                                                      0.0s
 => => transferring context: 26.52kB                                                                                   0.0s
 => [2/5] WORKDIR /usr/src/app                                                                                         0.1s
 => [3/5] COPY package.json .                                                                                          0.0s
 => [4/5] RUN npm install                                                                                              1.7s
 => [5/5] COPY . .                                                                                                     0.0s
 => exporting to image                                                                                                 0.1s
 => => exporting layers                                                                                                0.1s
 => => writing image sha256:71d67ca3d8c6cb3994ee0016cbc59d630fec0df05067d0dfbac22eb7f334f554                           0.0s
 => => naming to docker.io/library/sample                                                                              0.0s
```

도커 이미지를 빌드한 후 이미지를 생성하는 코드를 가져온 것이다. 작성한 인스트럭션이 Step별로 실행됨을 확인할 수 있다.   

<br/>

```bash
docker image ls "sample"
```

도커 이미지 목록 중 sample 이라는 이름으로 생성된 이미지도 확인할 수 있으며, 빌드된 이미지는 도커 허브에서 내려 받은 이미지와 똑같이 사용할 수 있다.  

이제 컨테이너를 생성해서 만든 이미지를 사용한다.  

```bash
docker container run -d -p 9090:9090 --name sample
```

<br/>

### 현재 Dockerfile의 문제점

지금까지 작성한 도커파일은 매우 기본적인 작업만 했으며 여러 문제가 있다.  

도커파일을 빌드했었던 이미지를 다시 빌드해서 이미지를 생성할 때 도커엔진은 캐싱을 하는데 위에서 작성한 도커파일은 캐싱을 사용할 수 없다.  
도커파일은 인스트럭션이 변경되지 않거나, 인스트럭션에서 사용하는 파일(COPY 명령어에서 복사하는 파일)의 변경이 없으면 캐시를 사용해서 실행한다. 하지만 위 코드에서 모든 파일 수정없이 이미지를 빌드할 때는 상관없지만, 예를 들어 server.js 파일이 수정하거나 새로운 스크립트 파일을 생성했을 때는 캐시를 사용하지 않는다.  

다음으로 server.js 파일이 수정돼도 프로젝트의 의존성을 설정하는 package.json 파일은 변경이 없을 수 있다. 지금 상태로는 매번 `npm install`로 프로젝트의 의존성을 생성하는 작업을 계속 할 것이고, node_modules 디렉토리를 생성하는 작업은 무거운 작업이다.  
이 문제점들을 해결해볼 것이다.

<br/>
<br/>

## Dockerfile 캐시 메커니즘


