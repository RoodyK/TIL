# 깃의 명령어 정리

```
# git 초기 설정(전역으로 사용되는 사용자명 이메일 주소설정) 
$ git config —global user.name “username”
$ git config —global user.email user@user.com
# 설정된 정보 확인
$ git config --list

# 깃 초기화 (해당 디렉토리를 버전관리)
$ git init
$ git init 생성할폴더명

# 현재 깃의 상태 확인
$ git status
# No commits yet 커밋한 적이 없음
# Untracked files: 깃이 관리하지않는 파일

# 파일 Staging Area(저장 대기 상태[커밋할 파일의 정보를 저장])에 올리기 
$ git add 파일명 # 파일 일부
$ git add . # 파일 전체

# 파일 깃 디렉토리에 버전 관리하기
$ git commit –m ‘메시지’
$ git commit –m ‘타이틀’-m '설명'
$ git commit -am '메시지' # 한번이라도 커밋한 파일이어야 가능
# 커밋내용 변경
$ git commit -—amend

# 커밋한 파일들의 기록 확인
$ git log
$ git log --help # 다양한 로그 방법을 찾아서 사용할 수 있다.

# 파일을 수정했을 때 수정 내용을 비교하는 명령어
$ git diff 
git diff 이후코드..이전코드 # working directory와 index(staging area)의 내용을 비교하는 명령

# Staging Area에 올라간 파일을 다시 Working Directory에 이동시키기 
$ git restore —staged 파일명

# reset : 시간을 과거로 돌림 (이후 행적이 history에서 지워짐), 기본은 mixed이다
# reset 의 종류
# soft
# - 최근 커밋을 하기 전 상태로 작업트리를 되돌린다.
# - repository의 history만 삭제가 되고 나머지는 건들지 않는다.
# - staging area 상태로 돌아감
$ git reset —soft 커밋해쉬

# mixed
# - 최근 커밋과 스테이징을 하기 전 상태로 작업 트리를 되돌린다.
# - repository와 index(staging area)는 취소하지만 working directory는 남아있다
# - working directory 작업하던 상태로 돌아감
$ git reset —mixed 커밋해쉬

# hard
# - 최근 커밋과 스테이징, 파일 수정을 하기 전 상태로 작업트리를 되돌린다.
# - 이 옵션으로 되돌린 작업은 복구할 수 없다.
# - repository / index(staging area) / working directory의 내용을 다 초기화
$ git reset —hard 커밋해쉬

# 수정된 상태에서 원래의 기존 커밋으로 돌아가기
# HEAD 란 마지막 커밋의 위치를 의미하고 ORIGIN/HEAD 는 원격저장소의 마지막 커밋을 의미한다
$ git reset —hard 
$ git reset ORIG_HEAD

# HEAD에서 하나 이전의 커밋으로 리셋
$ git reset HEAD~1

# 리셋 되돌리기(지금까지 참조한 기록을 보여줌)
$ git reflog

# revert : 변경할 곳의 이후의 내용은 유지하되 지정된 커밋 해쉬만 변화만을 제거해야할 때 사용
$ git revert 커밋해쉬




# git branch

# 브랜치 확인
$ git branch  

#  브랜치 생성
$ git branch 브랜치명

# 지정된 브랜치로 변경
$ git checkout 브랜치명

# 브랜치 제거
$ git branch -d 브랜치명
# 지워질 브랜치에 다른 브랜치에는 적용되지 않은 내용이 있으면 강제 삭제하는 용도
$ git branch -D 브랜치명

# 브랜치 생성과 동시에 체크아웃 진행
$ git checkout -b 브랜치명

# 커밋해쉬가 브랜치가 됨
$ git checkout 커밋해쉬

# 최근 사용 브랜치 이동 방법
$ git switch 브랜치명
# 브랜치 생성과 동시에 변경
$ git switch –c 브랜치명

# 브랜치 병합
$ git merge 브랜치명

# 자신이 main 브랜치일 때 branch/#1을 병합
$ git merge branch/#1 

# 병합 시 충돌 해결
# main 브랜치와 else 브랜치 만든후 같은 곳 저장 후 commit
# main브랜치로 이동 후 merge else 후 conflict 해결
# git status로 충돌난 파일을 확인 후 충돌부분 후 수정 
$ git add .
$ git commit or git commit –m ‘message’

# 충돌 이전 merge 하기전으로 돌아가기
$ git merge —abort



# git stash 작업하고 있던 파일을 임시 저장한다.(안전한 곳에 넣어둔다)
$ git stash
$ git stash save [설명]

# 가장 최근에 stash했던 파일이 modified 상태로 돌아온다
$ git stash apply

# 이름에 해당하는 stash를 가져온다.
$ git stash apply stash명

# stash의 Staged상태의 파일까지 꺼내오기
$ git stash apply --index

# stash 했던 리스트를 확인한다.
$ git stash list

# 가장 최신의 stash를 제거한다.
$ git stash drop

# 이름에 해당하는 stash 제거
$ git stash drop stash명

# 가장 최근에 stash한 내용을 불러옴과 동시에 stash 리스트에서 제거한다. apply + pop
$ git stash pop

# 원하는 이름의 stash한 내용을 불러옴과 동시에 stash 리스트에서 제거
$ git stash pop 스태시명

# stash했던 전체 목록을 제거한다.
$ git stash clear




# 원격저장소 remote repository 사용 명령어
# 원격저장소 경로는 github create remote repository 후의 url을 말한다.
# origin 원격저장소 나의 로컬저장소와 연결되어있는 주로 동기화를 하는 기본적인 원격저장소(메인)

# 경로를 원격저장소로 지정 (origin은 별칭)
$ git remote add orgin 경로

# 지정된 경로 확인
$ git remote -v

# 원격저장소 경로 제거
$ git remote remove origin

# 원격저장소로 푸시(보내기) => git push : 원격 저장소에 정보를 업로드하는 것(변경정보나 새로운 정보)
$ git push origin main

# 폴더명에 깃허브경로에 해당하는 폴더들을 클론함
$ git clone 깃헙주소 폴더명

# 현재 디렉토리에 클론 디렉토리를 생성하면서 클론
$ git clone 깃헙주소

# 메인 브랜치 정보를 저장하면서 푸시(디폴트  원격 저장) => 다음 부터는 git push만 사용해도 자동으로 푸시됨
$ git push -u origin main

# 원격저장소로 강제 푸시
$ git push –f origin main

# 원격 저장소의 정보를 가져오면서 자동으로 로컬 브랜치에  병합(merge)
# 클론을 한 뒤에 작업을 하기전에 반드시 git pull 을 확인하고 작업하는 것이 좋다.
$ git pull origin 원격저장소의브랜치명

# 로컬과 원격의 분기된 히스토리를 병합 : (merge 방식)
$ git pull —no—rebase 

# 마지막 푸시와 원격의 마지막 푸시를 적용하고 그 다음에 현재 작업내용이 붙는 형식
$ git pull —rebase
```