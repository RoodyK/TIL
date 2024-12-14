# 젠킨스 깃허브 리포지토리 SSH 연결 실패 에러

도커 컨테이너에서 젠킨스를 실행 후 깃허브 리포지토리와 ssh-key를 사용해서 연동을 하려고 하는 중 에러가 발생했다.

![ssh-error](/Jenkins/images/No_ED25519_host_key.png)

<br/>

깃허브의 리포지토리의 연결이 실패했는데, `stderr: No ED25519 host key is known for github.com and you have requested strict checking. Host key verification failed.` 문구를 확인하면 호스트 키 검증에 실패했다는 것을 알 수 있다.  

젠킨스에서는 기본적으로 SSH 연결 시 호스트 키를 검증하는 Strict Host Key Checking 기능이 활성화되어 있다.  
이 문제는 SSH를 통해 깃허브 서버에 연결할 때, 깃허브 서버의 호스트 키가 로컬 머신의 known_hosts 파일에 등록되어 있지 않아서 발생한다. 결과적으로 젠킨스가 깃허브 서버의 호스트 키를 신뢰하지 못하므로 발생한다.  

<br/>  
<br/>

## 해결 방법

이 문제를 해결하려면 젠킨스 홈 디렉토리에 known_hosts 파일에 깃허브를 신뢰할 수 있도록 호스트 키를 등록해주면 된다. known_hosts파일은 SSH 클라이언트가 접속한 서버의 공캐 키를 저장하는 파일로, 이 파일에 저장된 서버의 키는 이 후에 연결 시 신뢰성을 확인하는데 사용된다.     

방법은 다음 두 가지 방법에서 선택해서 하면 된다.

### 방법1 - `git ls-remote` 사용

에러 메시지에 있는 것처럼 `git ls-remote -h git@github.com:sample/sample.git HEAD`로 연동할 깃허브 리포지토리의 경로를 입력해주면 된다.

```bash
# 도커의 젠킨스 홈디렉토리의 .ssh 폴더 경로로 이동
cd /var/jenkins_home/.ssh

# 깃허브 원격 저장소의 참조(refs) 조회
git ls-remote -h git@github.com:sample/sample.git HEAD
```

<br/>

위 명령어는 원격 저장소의 참조 목록을 조회하는 해당 url의 HEAD 브랜치(보통 main이나 master)에 대한 참조만 출력한다.  

명령어를 입력하면 SSH를 통해 서버에 처음 접속할 때의 보안 경고 메시지가 출력되는데 SSH 클라이언트가 깃허브 서버의 호스트 키를 알지 못하므로, 이 서버에 연결할 것인지를 묻는 지문이 나타난다.  
yes를 입력해서 서버의 호스트 키가 로컬 컴퓨터에 저장되어 이후 접속할 때 신뢰할 수 있는 서버로 간주할 수 있게 해주면 된다.

<br/>
<br/>

### 방법2 - `ssh-keyscan` 사용

`ssh-keyscan` 명령어를 사용해서 깃허브 서버의 ED25519 호스트 키를 저장시켜주면 된다. 이 명령어는 SSH 서버의 공개 키를 known_hosts 파일에 추가하는 데 사용된다. 

```bash
# ssh-keyscan으로 수집된 깃허브의 공개 키를 known_hosts 파일에 저장
# -t 옵션은 수집할 키의 유형을 지정한다.
ssh-keyscan -t ed25519 github.com >> /var/jenkins_home/.ssh/known_hosts
```
<br/>

명령어를 실행했을 떄 나타나는 문구는 방법1과 같고 yes를 입력하면 known_hosts 파일에 깃허브의 공개 키를 저장한다.

<br/>

이제 젠킨스에서 다시 깃허브의 SSH 경로를 입력하면 에러가 사라진 것을 확인할 수 있디. jenkins서버에 생성한 비밀 키를 Credentials에 등록 후 사용하면 된다.  

![error-clear](/Jenkins/images/No_ED25519_host_key_clear.png)

<br/>
<br/>

