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
<br/>
<br/>
<br/>

# git 명령어 정리

git을 사용할 때 주로 사용되는 명령어를 정리한다. 설명에서 나오는 git의 HEAD는 현재 작업 중인 브랜치를 가리키는 포인터다.    
<br/>

## git 설정

`git config` 명령어는 Git의 설정을 관리하는데 사용된다. 사용자 정보, 편집기, 기본 브랜치 등을 설정할 수 있다.

`--global`은 전역 설정, `--local`은 해당 저장소에만 적용되는 설정, `--system`은 시스템 전체에 적용되는데 주로 global 설정을 사용하고 가끔 local 설정을 사용한다.  

사용자명, 이메일은 깃허브에 가입한 자신의 이름과 이메일을 설정하면 되고, 브랜치는 master브랜치가 아닌 main이 기본 브랜치가 되도록 설정할 것이다.  

```bash
# 사용자 이름 설정
# git config --global user.name [이름]
# git config --global user.email [이메일]
git config --global user.name "RoodyK"
git config --global user.email "pps8853@gmail.com"

# 기본 브랜치명을 main으로 설정
# git config --global init.defaultBranch [브랜치명]
git config --global init.defaultBranch main
```
<br/>

설정한 내용을 제거하려면 `--unset` 옵션을 사용하면 된다.
```bash
# git config --global --unset [설정명]
git config --global --unset user.name
git config --global --unset user.email
```
<br/>
<br/>

## git 초기화 및 원격 저장소 연결

### git init

`git init` 명령어는 Git 저장소를 초기화(생성)할 때 사용된다. git을 통해 버전 관리를 하기 위해서는 init 명령어로 초기화를 해야 한다.  
`git init` 명령어를 사용하면 현재 디렉토리나 지정한 디렉토리에 .git 디렉토리가 생성된다.  

```bash
# 깃 초기화
# 디렉토리명을 지정할 수 있고 지정하지 않으면 현재 디렉토리가 생성된다.
git init 
git init my-repo
```
<br/>

### git remote

git 을 사용하면 보통 github을 원격 저장소로 사용할 텐데 `git remote` 명령어를 사용해서 원격 저장소의 주소를 설정해서, 파일을 원격 저장소로 push할 수 있다.    
원격 저장소의 별명(이름)은 보통 origin을 사용한다. url에는 깃허브 리포지토리의 https나 ssh url을 입력하면 된다.

```bash
# 연결된 원격 저장소 확인
git remote -v

# 원격 저장소 추가
git remote add [이름] [url]
git remote add origin "https://github.com/RoodyK/reply-board.git"
```
<br/>
<br/>

## git 상태 관리

깃의 영역에는 Working Directory(작업 디렉토리), Staging Area(스테이징 영역), Git Directory가 있는데 각각의 상태를 관리하는 방법을 확인한다.  
<br/>

### git status
`git status` 명령어를 통해서 현재 작업 디렉토리와 스테이징 영역에 있는 파일들의 상태를 확인할 수 있다.  
수정, 추가, 삭제된 파일을 확인할 수 있고, 작업 디렉토리나 스테이징 영역에 있는 파일을 구분해서 확인할 수 있다.  

```bash
git status
```
<br/>

명령어를 입력한 결과는 다음과 같다.
```bash
On branch main
Your branch is up to date with 'origin/main'.

Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
        modified:   README.md

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
        modified:   Git/Git.md

Untracked files:
  (use "git add <file>..." to include in what will be committed)
        .gitignore

```
<br/>

- `Changes to be committed`: 스테이징 영역에 올라간 파일들로, 커밋될 파일을 말한다.
- `Changes not staged for commit`: 커밋된 파일 중 변경 사항이 발생한 파일들을 말한다.
- `Untracked files`: 추적되지 않은 파일로, 한번도 커밋된적 없는 새로 생성된 파일을 말한다.  
<br/>

### git add
`git add` 명령어는 워킹 디렉토리에 있는 파일을 staging영역에 올리기 위해서 사용된다. 이 명령어를 통해 커밋되기 전에 변경한 파일을 스테이징한다.    

```bash
# 특정 파일을 스테이징한다.
git add [파일명]

# 예: README.md 파일을 스테이징한다.
git add README.md 

# 현재 디렉토리를 포함한 하위의 워킹 디렉토리에 있는 파일을 모두 스테이징한다.
git .  
```
<br/>

스테이징 영역에 올라간 파일은 `git restore --staged` 명령어로 다시 워킹 디렉토리로 옮길 수 있다.  
```bash
# 워킹 디렉토리로 다시 옮기기
git restore --staged [파일명]
git restore --staged README.md 
```
<br/>

### git commit
`git commit` 명령어는 스테이징 영역에 있는 변경된 파일들을 로컬 저장소에 저장하는데 사용된다. `git add` 명령어로 스테이징된 파일들만 적용된다.  

```bash
# 기본적인 커밋 명령어. 기본 텍스트 편집기가 열리고, 커밋 메시지를 입력할 수 있다.
git commit

# -m 옵션을 사용하여 커밋 메시지를 인라인으로 작성할 수 있다.
git commit -m "커밋 메시지"

# --amend 옵션을 사용하여 마지막 커밋 메시지를 수정할 수 있다. (변경사항도 추가할 수 있다)
git commit --amend -m "수정된 커밋 메시지"

# -a 옵션을 사용하여 현재 트래킹되고 있는 모든 파일을 자동으로 스테이지하고 커밋할 수 있다.
git commit -a -m "커밋 메시지"

# -v 옵션을 사용하여 커밋 시 diff 정보를 볼 수 있다.
git commit -v -m "diff 정보 포함 커밋 메시지"
```
<br/>

### git push
`git push` 명령어는 로컬 저장소에 커밋된 파일들을 원격 저장소로 전송하는데 사용된다. 협업 시 커밋된 파일을 푸시해서 변경사항을 원격 저장소에 저장해서 공유할 수 있다.   
```bash
# 기본적인 git push 사용법. 현재 브랜치의 변경 사항을 원격 저장소에 푸시한다.
git push

# 특정 브랜치를 원격 저장소에 푸시한다.
git push origin [브랜치이름]
git push origin main

# 로컬 브랜치와 원격 브랜치 이름이 다를 경우 사용한다.
git push origin [로컬브랜치]:[원격브랜치]

# -f, --force: 로컬 저장소의 파일을 강제로 푸시한다.
# 이 옵션은 원격 저장소의 히스토리를 덮어쓰므로 데이터 손실이 발생할 수 있다.
git push --force
git push -f

# -u, --set-upstream: 현재 브랜치를 원격 저장소의 특정 브랜치와 연결하고 기본 푸시 설정을 한다.
git push --set-upstream origin 브랜치이름
git push -u origin 브랜치이름

# --delete: 원격 저장소에서 브랜치를 삭제한다.
git push origin --delete 브랜치이름

# --all: 로컬 저장소의 모든 브랜치를 원격 저장소에 푸시한다.
git push --all
```
<br/>

### git branch
`git branch` 명령어는 깃에서 브랜치를 관리하는데 사용된다. 브랜치를 생성, 삭제, 목록 조회 등을 할 수 있다.  

```bash
# 현재 존재하는 브랜치 목록을 표시한다.
git branch

# 새로운 브랜치를 생성한다.
git branch [브랜치명]
git branch new-branch

# 브랜치를 삭제한다. '-d' 옵션은 안전하게 삭제하며, 병합된 브랜치만 삭제 가능하다.
git branch -d [브랜치명]

# '-D' 옵션은 강제로 브랜치를 삭제한다. 병합되지 않은 브랜치도 삭제할 수 있다.
git branch -D [브랜치명]

# 브랜치 이름을 변경한다.
git branch -m [현재 브랜치명] [변경할 브랜치명]

# 현재 브랜치와 관련된 원격 브랜치를 확인한다.
git branch -r

# 브랜치의 상세 정보를 확인한다.
git branch -v

# 삭제된 브랜치를 포함한 모든 브랜치를 보여준다.
git branch -a
```
<br/>

### git checkout
`git checkout` 명령어는 깃에서 브랜치를 전환하거나 특정 커밋을 체크아웃할 때 사용된다.

```bash
# 특정 브랜치로 전환한다.
git checkout [브랜치명]
git checkout new-branch

# 새로운 브랜치를 만들고 그 브랜치로 전환한다.
git checkout -b new-branch

# 특정 커밋을 체크아웃한다. 이 경우 작업 디렉토리는 "detached HEAD" 상태가 된다.
git checkout commit_hash
# 예: 특정 커밋 해시 "a1b2c3d4"를 체크아웃
git checkout a1b2c3d4
```
<br/>

### git merge
`git merge` 명령어는 두 개의 브랜치를 병합하는데 사용한다. feature 브랜치에서 개발한 것을 메인 브랜치에 통합할 때 사용할 수 있다.  
병합은 기본적으로 fast-forward 방식으로 동작한다.

```bash
# 현재 브랜치에서 'feature-branch'를 병합한다.
git merge [브랜치명]
git merge feature-branch

# 병합 시, non-fast-forward 옵션으로 병합한다.
git merge --no-ff feature-branch

# 병합 중 충돌 발생 시, 'abort' 옵션으로 병합을 중단하고 이전 상태로 되돌린다.
git merge --abort

# 특정 커밋을 기준으로 병합할 때는 커밋 해시를 지정한다.
git merge [commit-hash]
```
<br/>

깃에서 merge(병합)을 사용하면 3가지 병합 방식 fast-forward, non-fast-forward, 3-way-merge 을 알아야 한다.  
각각의 방식을 간단하게만 정리하며 자세한 부분은 [깃 공식문서](https://git-scm.com/book/en/v2/Git-Branching-Basic-Branching-and-Merging)를 참조하면 된다.    
<br/>

#### Fast-Forward
Fast-Forward 방식은 브랜치를 병합할 때 가장 기본 형태로, 현재 브랜치가 병합하는 브랜치의 조상인 경우 발생한다.  
병합할 브랜치에서 커밋이 추가된 상태에서 현재 브랜치가 병합 브랜치 생성 후 아무런 변경이 없는 상태일 때 사용된다.  
깃 커밋 로그(히스토리)가 선형으로 유지된다.  
<br/>

#### Non-Fast-Forward
Non-Fast-Forward 방식은 현재 브랜치가 병합하는 브랜치의 조상이 아닌 경우 발생한다. 두 브랜치의 HEAD가 서로 다른 경우 새로운 커밋을 생성해서 병합한다.  
Non-fast-forward는 새로운 커밋을 생성하지만 Fast-Forward가 가능한 경우 HEAD를 단순하게 이동시킬 수 있다.
<br/>

#### 3-Way-Merge
3-Way-Merge는 Non-Fast-Forward와 비슷하지만 서로 다른 브랜치에서 작업이 이루어졌을 때 발생하며, 두 개의 브랜치와 그들의 공통 조상을 기준으로 병합을 수행한다.  
3-way merge는 항상 새로운 커밋을 생성한다.
<br/>
<br/>

### git fetch
`git fetch` 명령어는 원격 저장소의 변경 사항을 로컬 저장소로 가져오는 작업을 수행한다. 가져온 사항은 자동 병합되지 않으며, 로컬 브랜치에서 수동으로 병합하거나 비교할 수 있다.  
보통은 fetch와 merge가 합쳐진 pull을 사용하고, fetch 명령어는 원격 저장소의 최신 상태를 확인할 때 사용된다.    

```bash
# 원격 저장소의 모든 브랜치의 변경 사항을 가져온다.
git fetch

# 특정 원격 저장소의 변경 사항만 가져온다.
git fetch [원격저장소 명]
git fetch origin

# 특정 브랜치의 변경 사항만 가져온다.
git fetch [원격저장소명] [브랜치명]
git fetch origin main

# 가져온 데이터의 정보만 출력한다. 실제로 데이터를 가져오지 않고 어떤 변경 사항이 있을지 미리 보여준다.
git fetch --dry-run
```
<br/>

### git pull
`git pull` 명령어는 원격 저장소에서 변경된 내용을 가져와 로컬 브랜치와 자동으로 병합하는 작업을 수행한다.  
`git fetch`와 `git merge`를 합친 명령어로, 원격 저장소의 최신 상태를 쉽게 로컬에 반영할 수 있다.  

```bash
# 원격 저장소의 변경 사항을 가져와서 현재 브랜치에 병합한다.
git pull

# 특정 원격 저장소와 브랜치에서 변경 사항을 가져와서 병합한다.
git pull [원격저장소명] [브랜치명]
git pull origin main

# 자동 병합 대신, rebase를 사용하여 변경 사항을 가져온다.
git pull --rebase
```
<br/>

### git rebase
`git rebase` 명령어는 브랜치를 재구성하는 방법중 하나로, 특정 브랜치의 변경사항을 다른 브랜체의 베이스를 기준으로 이동시켜 커밋 로그를 깔끔하게 해준다.  
`git merge`는 두 브랜치의 변경사항을 병합해서 새로운 커밋을 생성하지만, `git rebase`는 커밋을 재배치해서 기존의 커밋을 덮어쓰는 방식으로 동작한다.  

```bash
# 기본 사용법
# [upstream]은 rebase할 기준이 되는 브랜치나 커밋을 의미한다.
git rebase [upstream]

# -i, --interactive 옵션은 밋을 수정, 삭제, 합치는 등의 작업을 할 수 있다.
git rebase -i [upstream]

# -p, --preserve-merges 옵션은 병합 커밋을 유지하면서 rebase를 수행한다. 병합 기록을 보존하고 싶을 때 사용한다.
git rebase -p [upstream]

# --continue 옵션은 충돌 해결 후 rebase를 계속 진행할 때 사용한다.
git rebase --continue

# --abort 옵션은 rebase 도중 문제가 발생했을 때, 원래 상태로 되돌리기 위해 사용한다.
git rebase --abort

# --skip 옵션은 현재 커밋을 건너뛰고 rebase를 계속할 때 사용한다.
git rebase --skip
```
<br/>

리베이스에 대한 개념은 [공식문서](https://git-scm.com/book/en/v2/Git-Branching-Rebasing)를 확인하면 보다 깊게 알 수 있다. 다음 rebase 진행 예제를 통해서 간단하게 설명한다.
```bash
# 현재 브랜치를 확인한다.
# * feature
#   main
git branch

# 'main' 브랜치로 전환한다.
git checkout main

# 원격 저장소에서 최신 커밋을 가져와 'main' 브랜치를 업데이트한다.
git pull origin main

# 다시 'feature' 브랜치로 전환한다.
git checkout feature

# 'main' 브랜치의 최신 커밋으로 'feature' 브랜치를 재정렬한다.
git rebase main

# 결과 그래프
# rebase 이전
A---B---C (main)
    \
     D---E---F (feature)

# rebase 적용
A---B---C---D'---E'---F' (main)
```

<br/>

### git stash
`git stash` 명령어는 현재 작업 중인 변경사항들을 임시로 저장한다. Untracked files 파일들을 제외한 모든 파일을 임시 저장소에 저장한다.  

```bash
# 현재 작업 디렉토리의 변경 사항을 임시로 저장한다.
git stash

# 특정 메시지를 붙여서 저장할 수 있다.
git stash save "작업 중인 변경사항 저장"

# 명령어를 축약하여 사용 가능하다.
git stash push -m "작업 중인 변경사항 저장"

# 스테이지된 변경 사항만 임시로 저장한다.
git stash push --staged

# 특정 파일만 임시로 저장할 수 있다.
git stash push -- path/to/file

# 저장된 스태시 목록을 확인할 수 있다.
git stash list

# 가장 최근에 저장된 스태시를 적용한다.
git stash apply

# 특정 스태시를 적용할 수 있다 (stash@{n} 형식).
git stash apply stash@{2}

# 가장 최근에 저장된 스태시를 적용하고, 스태시 목록에서 제거한다.
git stash pop

# 특정 스태시를 적용하고 제거한다.
git stash pop stash@{2}

# 스태시 목록에서 특정 스태시를 제거한다.
git stash drop stash@{2}

# 스태시 목록을 모두 비운다.
git stash clear

# 스태시된 변경 사항을 새로운 브랜치로 체크아웃한다.
git stash branch new-branch

# 특정 스태시를 새로운 브랜치로 체크아웃한다.
git stash branch new-branch stash@{2}
```
<br/>
<br/>

## git 상태 되돌리기

깃에서 상태를 되돌리는 명령어는 reset과 revert가 있다.  
<br/>

### git reset
`git reset` 명령어는 특정 커밋으로 되돌리거나 변경사항을 취소할 때 사용한다. 사용되는 주요 옵션은 soft, mixed(기본), hard가 있다.  
reset에서 자주 사용되는 HEAD~N에서 N은 현재 커밋으로부터 몇 번째 이전 커밋을 참조할지를 나타내는 숫자이다.
```bash
# HEAD는 현재 체크아웃된 커밋을 의미한다.
# HEAD~1은 현재 커밋의 바로 이전 커밋을 의미한다.
# HEAD~2는 현재 커밋의 두 번째 이전 커밋을 의미한다.
git resest --[soft, mixed, hard] [커밋 해쉬]
git resest --[soft, mixed, hard] e1ejie2
git resest --[soft, mixed, hard] HEAD
git resest --[soft, mixed, hard] HEAD~1
```
<br/>

`--soft` 옵션은 지정한 커밋으로 HEAD를 이동시키지만, 스테이징 영역과 작업 디렉토리는 변경하지 않는다. 
```bash
# 커밋만 되돌리고 변경 사항은 그대로 유지
git reset --soft [커밋 해쉬]
```
<br/>

`--mixed` 옵션은 지정한 커밋으로 HEAD를 이동시키고, 스테이징 영역은 되돌리지만 작업 디렉토리는 변경하지 않는다.
```bash
# HEAD를 한 커밋 이전으로 이동, 변경 사항은 워킹 디렉토리에서 유지, 스테이징 영역은 초기화
git reset --mixed [커밋 해쉬]
```
<br/>

`--hard` 옵션은 지정한 커밋으로 HEAD를 이동시키고, 스페이징 영역과 작업 디렉토리도 해당 커밋의 상태로 되돌린다. 즉, 모든 변경사항이 삭제된다.  
```bash
# HEAD를 한 커밋 이전으로 이동, 충돌이 발생한 경우 충돌을 해결하지 않음
# 커밋 이후의 모든 변경 사항이 삭제되며, 복구할 수 없다.
git reset --hard [커밋 해쉬]
```
<br/>

### git revert
`git revert` 명령어는 커밋된 변경 사항을 되돌리기 위해 사용된다. 기존의 커밋을 지우는 것이 아닌, 그 커밋에서 발생한 변경 사항을 반영하는 새로운 커밋을 추가한다.  

```bash
# 특정 커밋을 되돌린다.
git revert [커밋 해쉬]
git revert e1ewfw2

# -e, --edit 옵션은 되돌리기 커밋 메시지를 수정할 수 있게 한다.
git revert -e [커밋 해쉬]

# -n, --no-commit: 되돌리기 커밋을 바로 커밋하지 않고, 작업 트리에 변경 사항만 적용한다.
git revert -n [커밋 해쉬]
```
<br/>

### git reset과 git revert의 차이
- `git reset` 명령어는 지정한 커밋 이후의 기록이 삭제된다., 삭제된 커밋은 로컬 저장소에서만 적용되므로, 원격 저장소에 이미 푸시된 경우에 주의가 필요하다.
- `git revert` 명령어는 기존 커밋의 기록은 보존되며, 새로운 커밋이 추가된다. 협업 상황에서도 안전하게 사용할 수 있지만 커밋 로그가 어느정도 지저분해질 수 있다.
<br/>
<br/>
- 
## 깃 로그 및 차이 비교

### git log
`git log` 명령어는 git 저장소의 커밋 로그를 조회하는 데 사용된다. 커밋된 히스토리를 여러 옵션을 통해서 다양한 형식으로 출력할 수 있다.  
 
```bash
# 기본적인 git log 명령어, 커밋 히스토리를 기본 형식으로 출력한다.
git log

# --branches 옵션을 사용하여 특정 브랜치의 커밋 이력을 조회한다.
git log --branches=<브랜치명>

# --oneline 옵션은 각 커밋을 한 줄로 출력한다.
git log --oneline

# --graph 옵션은 커밋 히스토리를 그래프로 출력한다.
git log --graph

# --decorate 옵션은 각 커밋에 대한 브랜치, 태그 등의 정보를 함께 출력한다.
git log --decorate

# --stat 옵션은 각 커밋에 포함된 파일 변경 사항을 요약하여 출력한다.
git log --stat

# --p, -patch 옵션은 각 커밋에 포함된 실제 변경 내용을 출력한다.
git log --patch
git log -p

# --author 옵션은 특정 작성자의 커밋만 필터링하여 출력한다.
git log --author="작성자 이름"

# 조합해서 사용하기
git log --oneline --branches --decorate
```
<br/>

### git diff
`git diff` 명령어는 깃에서 파일의 변경사항을 비교할 때 사용된다. 작업 디렉토리와 스테이징 영역 사이의 차이, 특정 커밋들 사이의 차이 등을 확인할 수 있다.

```bash
# 작업 디렉터리와 스테이징 영역 사이의 차이를 보여준다.
git diff

# 특정 파일에 대한 변경 사항만 확인한다.
git diff [파일명]
git diff README.md

# 스테이징된 변경 사항을 확인한다. (즉, 'git add'를 통해 추가된 변경 사항)
git diff --cached

# 특정 커밋과 현재 작업 디렉터리의 차이를 비교
git diff [커밋 해시]
git diff 1a2b3c4d

# 두 커밋 간의 차이를 비교한다.
git diff [커밋 해시1] [커밋 해시2]
git diff 1a2b3c4d 5e6f7a8b

# 특정 브랜치와의 차이를 비교한다.
git diff [브랜치명]
git diff main

# 특정 디렉터리 내의 파일들에 대한 변경 사항을 확인한다.
git diff [디렉터리명]
git diff src/

# 두 특정 브랜치 간의 차이를 비교한다.
git diff [브랜치1] [브랜치2]
git diff main feature-branch

# 파일 이름이 변경된 경우 그 차이를 무시한다.
git diff --no-renames

# 파일 이름이 변경된 경우 그 차이를 포함한다.
git diff --find-renames

# 모든 공백 변경 사항을 무시한다.
git diff --ignore-all-space

# 줄 끝 공백 변경 사항을 무시한다.
git diff --ignore-space-at-eol
```
<br/>
<br/>

## 원격 저장소 내용 복제

### git clone
`git clone` 명령어는 원격 저장소의 내용을 로컬로 복제하는데 사용된다. 원격 저장소의 모든 파일 및 이력을 가져올 수 있다. 주로 프로젝트를 시작할 때나 이어서 진행할 때 사용한다.  

```bash
# 원격 저장소의 URL을 지정하여 클론한다. 기본 브랜치(main 또는 master)가 클론된다.
git clone [원격 저장소 URL]

# -b, --branch 옵션은 특정 브랜치를 클론한다.
git clone -b [브랜치명] [원격 저장소 URL]

# --depth 옵션은 지정한 깊이만큼의 커밋 기록만 클론한다. 이 옵션은 최신 커밋 몇 개만 가져와서 클론할 수 있다.
git clone --depth [깊이] [원격 저장소 URL]

# 기본 브랜치만 클론하고 다른 브랜치는 클론하지 않는다. 
# -b 옵션과 함께 사용하면 특정 브랜치만 가져온다.
git clone --single-branch -b [브랜치명] [원격 저장소 URL]

# 서브모듈도 함께 클론한다. 서브모듈이 있는 경우 이 옵션을 사용해야 한다.
git clone --recurse-submodules [원격 저장소 URL]

# 원격 저장소의 이름을 지정한다. 기본값은 'origin'이다.
git clone -o [이름] [원격 저장소 URL]
```
<br/>
<br/>

지금까지 주로 사용되는 명령어를 정리했다. 추가적인 명령어는 공식문서를 참조해서 핋요한 명령어를 찾아서 사용하면 될 것 같다.
<br/>