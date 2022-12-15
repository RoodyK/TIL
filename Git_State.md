# 깃(Git)이란?

깃이란 VCS(Version Control System)으로 버전을 관리해주는 시스템으로,
버전 관리는 이전에 작업했던 특정 버전을 불러올 수 있도록 시간 경과에 따른 파일 또는 파일 세트의 변경 사항을 기록하는 시스템이다.   
<br/>
<br/>

## 깃의 영역

깃은 세가지 영역을 가지고 있다.  

![git_state](https://user-images.githubusercontent.com/95058915/207821167-a54e7102-e991-4bb9-9dd2-b9006984c830.png)  

**Working Directory : Git으로 관리하는 파일들이 위치하는 영역**  
git init 명령어를 사용하거나 remote repository에서 clone 해왔을 때 지정한 디렉토리에서 .git디렉토리를 제외한 모든 파일들을 말한다.


**Staging Area : Working Directory에서 commit 하고자하는 파일을 저장하는 영역**  
git에서는 기술용어로써 Index라고도 하며 스테이징 영역도 같은 문구라 보면 되고 깃으로 관리하는 파일중 작업하거나 생성한 파일 및 수정한 파일들 중 버전을 관리하고자 하는 파일을 저장한다.


**Git Directory : Commit되어 버전을 관리되는 파일이 위치하는 영역**  
깃 디렉토리는 버전을 관리하는 파일들이 저장되는 위치이며, 다른 컴퓨터에서 저장소를 복제할 때 복사되는 부분이다.  

<br/>
<br/>

## 깃의 상태 

**Modified** : 수정한 파일을 Local Repository에 커밋하지 않은 것을 말하며, 워킹 디렉토리역억에 있는 파일들 중 수정한 파일들의 상태이다.  


**Staged** : 수정한 파일 중 commit 하기전에 Stating Area영역이 표시된 상태의 파일들 의미한다.  


**Commited** : Staged 상태의 파일들이 Local Repository에 저장되었음을 의미하며 commit된 파일들은 워킹 디렉토리 영역으로 돌아가게된다.
Commited 상태의 파일을 수정하면 Modified 상태가 된다.  
