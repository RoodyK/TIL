# 하드디스크 관리와 사용자별 공간 할당

## 하드디스크 종류

**IDE(Integrated Drive Electronics)**

- 가장 오래된 규격으로 포트는 40개의 핀으로 구성된 직사각형이다. 데이터를 병렬로 전송한다는 뜻에서 PATA(Parallel Advanced Tachnology Attachment) 인터페이스라고 부르기도 한다. 버전별로 데이터 전송속도가 다르며 최신 규격인 UDMA6 모드에서는 초당 133.3MB의 데이터를 전송한다. 현재는 초기형 IDE보다 성능이 향상된 E-IDE(Enhanced IDE) 규격이지만 일반적으로 IDE라고 부른다.
<br/>

**SATA(Serial Advanced Technology Attachment)**

- 최근에 나온 인터페이스로 하드디스크 드라이브의 속도와 연결 방식 등을 개선하기 위해 개발되었다. SATA 1 규격에서는 초당 150MB, SATA 2는 초당 300MB의 전송 속도를 낸다. 또 SATA 2에서는 USB처럼 허브를 이용해 하나의 포트에 여러 개의 하드디스크를 연결할 수 있고 PC를 끄지 않고 장치를 연결하는 핫플러그 등의 기능이 추가되었다.
<br/>

**SCSI(Small Computer System Interface)**

- 서버나 워크스테이션 등에 쓰이는 고속 인터페이스다. 무엇보다 안정성이 높은 것이 최대의 장점이지만 가격이 매우 비싸다. 이 규격을 쓰려면 별도의 확장 카드를 달아야 한다. 최신 규격인 울트라 320은 최대 320MB/초의 속도를 낸다. 최근 이를 대체하는 SAS 인터페이스가 등장하면서 쓰는 곳이 조금씩 줄고 있다.
<br/>

**SAS(Serial Attached SCSI)**

- SCSI 규격을 한 단계 발전시킨 것으로 이 규격 역시 서버 등의 대형 컴퓨터에 주로 쓰인다. 성능은 울트라 320 SCSI보다 좋다. 커넥터와 선은 SATA와 같은 것을 이용하지만 컨트롤러가 달라 SAS 규격의 하드디스크가 SATA 제품보다 훨씬 비싸다. SATA 규격 하드디스크 드라이브를 SAS 장치에 꽂아 쓸 수는 있지만 반대로 SAS 하드디스크를 일반 SATA 인터페이스에 연결하지는 못한다.
<br/>

**SSD(Solid State Drive. Solid State Disk)**

- 하드 디스크 드라이브(HDD)와 비슷하게 동작하면서도 기계적 장치인 HDD와는 달리 반도체를 이용하여 정보를 저장한다. 임의접근을 하여 탐색시간없이 고속으로 데이터를 입출력할 수 있으면서도 기계적 지연이나 실패율이 현저히 적다. 또 외부의 충격으로 데이터가 손상되지 않으며, 발열·소음 및 전력소모가 적고, 소형화·경량화할 수 있는 장점이 있다.
- 플래쉬 방식의 비휘발성 낸드 플래쉬 메모리 램(RAM) 방식의 휘발성 DRAM을 사용합니다. 플래시 방식은 RAM 방식에 비하면 느리지만 HDD보다는 속도가 빠르며, 비휘발성 메모리를 사용하여 갑자기 정전이 되더라도 데이터가 손상되지 않습니다.
<br/>
<br/>
<br/>

### SATA 장치와 SCSI 장치의 구성

**Server의 하드웨어 구성도**

SATA : 일반 PC 용 디스크 주로 장착

SCSI : 서버용 디스크 주로 장착

슬롯은 SATA 4개, SCSI 4개 존재하며, 케이블은 SATA 30개, SCSI는 15개 존재한다

SCSI 0:0 은 서버를 설치한 디스크가 이미 등록되어있음

![Untitled (3)](https://user-images.githubusercontent.com/95058915/206430971-d03bb2d9-2405-40ba-a0c8-21c3d4b57684.png)


### SATA

물리적으로 SATA0 0:1 을 리눅스 안에서는  /dev/sr0 이라고 부른다.

### SCSI

물리적으로 SCSI0 의 SCSI 0:0을 리눅스 안에서는 /dev/sda 이라고 부른다. 두 번째는 sdb 세 번째는 sdc 가 된다.

한 장치 안에 파티션이 존재하여 디스크를 분할해서 사용할 수 있는데, 파티션으로 나뉘게 되면 /dev/sda1, /dev/sda2 이런 방식으로 이름이 나뉘고 파티션은 총 4개로 분할 가능하다.

파티션 : **디스크 공간의 분할**을 의미하는 것으로 하드 디스크 드라이브의 기억 공간을 별도의 데이터 영역으로 분할하는 것을 말한다


### 하드디스크 추가하기

하드디스크 1개 추가 하드웨어 구성

![Untitled (4)](https://user-images.githubusercontent.com/95058915/206431421-6800a199-7206-493c-988c-f359b6eecb00.png)

SCSI 0~3:0~15로 등록하는 것은 물리적인 장치(기계)만 등록하는 것이고 사용하려면 파티션을 설정해야 한다.

파티션을 그냥 사용할 수 없으며 반드시 특정한 디렉토리를 만들어서 디렉토리와 디스크를 연결해야 하는데 이것을 mount라고 한다.

**실행 흐름도**

![Untitled (5)](https://user-images.githubusercontent.com/95058915/206431431-afd688c3-87c2-4a66-a579-c195024e134c.png)

장착된 디스크의 이름은 /dev/sdb

논리적인 파티션의 이름은 /dev/sdb1

1. 물리적인 하드디스크 장착
fdisk(명령어)를 통해서 물리적인 하드디스크를 분할(파티션을 나눔) 해준다.
2. 원하는 모양으로 파티션을 나눠준다.
mkfs.ext4(명령어)을 사용해 파일 시스템을 생성해준다.
3. 사용할 수 있는 하드디스크가 된다.
하드디스크를 사용하기 위해서 mount 해준다.
4. 파티션한 장치를 데이터와 연결해준다.
5. /etc/fstab에 등록하면 재부팅하더라도 자동으로 mount 된다.

마운트 : 리눅스에서는 하드디스크의 파티션, CD/DVD, USB메모리 등을 사용하려면 특정한 위치에 연결을 해 줘야 한다. 이렇게 물리적인 장치를 특정한 위치(대개는 디렉토리)에 연결시켜 주는 과정을 마운트라고 한다.

```bash
# 디스크 확인
ls -l /dev/sd* 

# fdisk : 물리적인 하드 디스크를 분할(파티션)하는 명령어
# fdisk /dev/sdb => sda ~ 순서대로
fdisk 장치명

# fdisk -l 장치명 : 장치분할로 들어가지 않고 설정 정보만 확인
fidks -l /dev/sdb

# Command m : 명령어 help
m 

# Command n : 새 파티션 생성
n

# partition type : primary , extended는 잘 사용x
p

# partition number : 파티션을 1~4개로 나눔
1

# first sector : 어디서부터 시작할 것인지
2048 # 기본값 2048

# last sector : 어디까지 사용할 것인지 +300K, M, G, T 단위 사용 가능
enter # 기본값 사용

# Command 명령어 사용
# p : print
p
# w : 적용
w 

# 파일 시스템 포맷(생성) make file system
# -t 옵션은 파일시스템의 type를 지정
# 파일 시스템 타입
# swap: 메모리 공간이 부족할 때 프로그램이 실행 가능하도록 예비 공간의 역할을 수행
# ext4: Linux에서 널리 사용되는 저널링(백업 및 복구 능력) 파일 시스템

mkfs -t [파일시스템타입] 파티션명
mkfs.[파일시스템타입] 파티션명
mkfs.ext4 /dev/sdb1 # ext4 : 기본으로 제공하는 파일 시스템

# 디렉토리 생성 후 디렉토리와 mount 해줘야 함

# 디렉토리 생성
mkdir /mydata

# mount : 물리적인 장치를 디렉토리와 연결
mount 파티션명 디렉토리명
mount /dev/sdb1 /mydata

# 디렉토리와 파티션 연결 완료
# df : 확인하기
df 

# 파일 카피 후 다시 확인
cp /boot/vmlinuz /mydata/testFile
ls -l /mydata
df # 용량이 증가

# umount : 연결 끊기
umount /dev/sdb1
ls /mydata

# mount 유지를 위한 등록
gedit /etc/fstab
# 파일 하단에 한줄 추가
# [파일시스템 장치] [마운트포인트] [파일시스템종류] [옵션] [덤프] [파일체크 옵션]
/dev/sdb1  /mydata  ext4  defaults  0  0

# Command 명령어 종류
# n : 새 파티션 생성
n
# p : print
p
# w : 적용
w 
# t : 타입 변경
t # l을 누르면 타입 확인 가능 16진수로 구성
```

**/etc/fstab 파일**

리눅스가 부팅될 때마다 자동으로 읽는 중요한 파일이다.

이 파일에는 마운트 정보가 수록되어 있으며 글자가 틀릴 경우 아예 부팅되지 않을 수도 있으므로 수정시 주의가 필요하다.

**/etc/fstab 파일 설정**

[파일 시스템 장치 이름]  [마운트 포인트]  [파일시스템 종류]  [마운트 옵션]  [덤프]  [파일 체크 옵션]

1. 파일시스템 장치 이름(
⇒ 파일 시스템 장치 이름은 파티션 이름을 의마한다.
2. 마운트 포인트
⇒ 생성한 파티션을 어느 디렉토리에 연결할지 지정한다.
3. 파일시스템 종류
⇒ 파티션 생성 시 지정한 파일시스템의 종류를 지정한다.
- ext, ext2, ext3, ext4, iso9660, nfs, swap, ufs, vfat, msdos, hpfs, ntfs 등
4. 마운트 옵션
⇒ 파일 시스템 옵션을 지정한다.
- default : rw, nouser, auto, exec, suid속성을 모두 설정
- auto : 부팅시 자동으로 마운트 된다.
- exec : 실행파일이 실행되는 것을 허용하는 파일 시스템이다.
- suid : SetUID와 SetGID의 사용을 허용하는 파일 시스템이다.
- ro : 읽기 전용 파일시스템이다.(Read Only)
- rw : 읽고 쓰기(Read Write) 파일시스템으로 사용된다.
- user : 일반 계정 사용자들도 마운트를 할 수 있는 파일 시스템이다.
- nouser : root만 마운트할 수 있는 파일시스템이다.
- noauto : 부팅시 자동으로 마운트 되지 않게하는 파일 시스템이다.
- noexec : 실행파일을 실행되지 못하게 하는 파일시스템이다.
- nosuid : SetUID와 SetGID의 사용을 허용하지 않은 파일 시스템이다.
- usrquota : 개별 계정사용자의 Quota설정이 가능한 파일시스템이다.(쿼터:사용자 별로 디스크 할당을 조정한(제한))
- grp : 그룹 별 Quota설정이 가능한 파일 시스템이다.
5. 덤프 설정
⇒ 덤프 여부를 설정한다.
- 0 : 백업을 하지 않는다.
- 1 : 백업 가능한 파일 시스템이다.
6. 파일 체크 옵션
⇒ 루트 파일 시스템을 점검할때 사용하고 , 0, 1, 2 로 설정한다.
- 부팅시에 파일시스템을 점검하지 않는다.
- 루트 파일 시스템으로 부팅시 파일 시스템을 점검한다.
- 루트 파일 시스템 이외의 파일시스템으로서 부팅시 파일 시스템을 점검한다.

### RAID 정의 및 개념

RAID(Redundant Array of Inexpensive Disks) 는 여러 개의 디스크를 하나의 디스크처럼 사용하는 것으로, 비용 절감, 신뢰성 향상, 성능 향상의 효과를 낸다.

**하드웨어 RAID**  

하드웨어 제조업체에서 여러 개의 하드 디스크를 가지고 장비를 만들어서 그 자체를 공급한다.  

좀 더 안정적이지만, 상당한 고가이다.  

**소프트웨어 RAID**

고가의 하드웨어 RAID의 대안이다.  

운영체제에서 지원하는 방식으로, 저렴한 비용으로 좀 더 안전한 데이터의 저장이 가능하다.

### 각 RAID 방식의 비교

![Untitled (6)](https://user-images.githubusercontent.com/95058915/206432231-fadd0cc9-b863-4c7c-9f9a-71c0b932b436.png)


**볼륨** : 파일시스템으로 포맷된 디스크상의 저장영역이다. 기본 디스크의 저장영역은 파티션이기 때문에, 하나의 파티션이 하나의 볼륨이지만, 파티션보다 더 유동적이고 논리적인 개념이다.

 **패리티** : 정수의 홀수 또는 짝수 특성, 오류 후 데이터를 재구축하는데 사용되는 계산된 값

### Linear RAID

최소 2개의 하드디스크가 필요하다.

2개 이상의 하드디스크를 1개의 볼륨으로 사용한다.

앞 디스크부터 차례대로 저장하고 100%의 공간 효율성을 가진다.(= 비용 저렴)

### RAID 0(**Stripping)**

최소 2개의 하드디스크가 필요하다.

모든 디스크에 동시에 저장되며 100%의 공간 효율성을 가진다.(= 비용 저렴)

동시에 저장되어 A B C D 가 저장될 때 A C / B D 이런 식으로 저장되지만 한쪽만 고장나면 양쪽을 다 사용 할 수 없으므로 신뢰성이 떨어진다.

빠른성능을 요구하되 혹시 전부 잃어벼러도 큰 문제가 되지 않는 자료가 적당하다.

### RAID 1(Mirroring)

미러링이라 부르며 데이터 저장의 두 배의 용량이 필요하고 결함이 허용된다.(= 신뢰성 높음)

동시에 저장되며 양쪽 모두 같이 저장됨 A B C D 란 파일이 양 쪽 같이 저장된다. 

두 배의 저장공간 = 비용이 두 배 = 공간효율 나쁨

저장속도(성능)는 변함이 없으며 중요한 데이터를 저장하기에 적절하다.

### RAID 5

RAID1의 데이터의 안정성 + RAID0의 공간 효율성 ⇒ 어느정도의 결함을 허용하면서 저장 공간의 효율성도 좋다.

최소한 3개의 하드디스크가 필요하다.

오류가 발생할 때는 패리티(Parity)를 이용해서 데이터를 복구한다.

디스크 개수 -1의 저장 공간을 사용한다.

디스크 2개가 고장 나면 복구를 못한다.

### RAID 6

RAID5 방식이 개선된 것으로 공간 효율은 RAID5보다 약간 떨어지지만, 2개의 디스크가 동시에 고장이 나도 데이터에는 이상이 없도록 하는 방식이다.

RAID6의 경우에는 최소 4개의 디스크가 필요하다.

공간 효율은 RAID5보다 약간 떨어지지만 데이터에 대한 신뢰도는 좀 더 높아지는 효과를 갖는다.

RAID6은 패리티를 2개 생성해야 하므로 내부적인 쓰기 알고리즘이 복잡해져서 성능(속도)은 RAID5에 비해 약간 떨어진다.

### RAID 구축

**구성도**

![Untitled (7)](https://user-images.githubusercontent.com/95058915/206432295-dca4e7ad-5412-4341-bf0b-e3d9c4e781ed.png)


**작업 과정**

![Untitled (8)](https://user-images.githubusercontent.com/95058915/206432304-6ef29f57-f965-4e77-a3a6-c596d06d2756.png)


RAID는 관례적으로 md라는 이름이 붙는다.  /dev/md
 md1, md2  뒤의 번호는 중복되면 안된다

```bash
# 파티션 상태 확인
fdisk -l /dev/sdb

# RAID를 생성하기 위한 패키지 설치
apt -y install mdadm

# mdadm 패키지 사용
mdadm

# fdisk : 물리적인 하드 디스크를 분할(파티션 생성)하는 명령어
# fdisk /dev/sdb => sda ~ 순서대로
fdisk 장치명
fdisk /dev/sdb
# 생성 시 t를 사용해서 타입 raid auto로 지정
# Command 명령어
n # 새로운 파티션 생성
t # 타입 변경
fd # raid auto 타입
w # 쓰기(저장)

# linear  RAID 생성
mdadm --create RAID이름 --level=RAID형태 --raid-divices=장치개수 장치명...
# --level=linear, --level=raid0, --level=raid1, --level=raid5, --level=raid6
mdadm --create /dev/md9 --level=linear --raid-devices=2 /dev/sdb1 /dev/sdc1
# Continue Creating Array? => y입력

# RAID 확인
mdadm --detail --scan

# 파일 시스템 포멧(생성) make file system
mkfs.ext4 /dev/md9

# 디렉토리 생성 후 마운트
mkdir /raidLinear # 디렉토리명은 자신이 원하는 이름으로 지정
mount /dev/md9 /raidLinear

# 확인
df

# 장치 상세한 상황 확인
mdadm --detail 장치명
mdadm --detail /dev/md9

# 영구 적용 설정
gedit /etc/fstab

# 맨 아래줄 삽입
/dev/md9 /raidLinear ext4 defaults 0 0

# 위의 과정으로 모두 적용 후 아래 진행

# /etc/fstab 설정 시 추가작업 => VMware와 우분투 충돌로 인한 작업
mdadm --detail --scan 후 복사
ARRAY /dev/md9 metadata=1.2 name=server:9 UUID=74d723c2:eea61e2d:bb419fcc:081be448
ARRAY /dev/md0 metadata=1.2 name=server:0 UUID=914ca4c7:0c634ede:dc079060:444e5d09
ARRAY /dev/md1 metadata=1.2 name=server:1 UUID=f9a4ad55:27581f00:fa427411:a0d92417
ARRAY /dev/md5 metadata=1.2 name=server:5 UUID=b56dfa9c:d8552686:07930928:0dab99a8

# mdadn 기타 설정 파일
gedit /etc/mdadm/mdadm.conf

# name=server:9 만 제거후 맨 아랫줄 삽입
ARRAY /dev/md9 metadata=1.2 UUID=74d723c2:eea61e2d:bb419fcc:081be448
ARRAY /dev/md0 metadata=1.2 UUID=914ca4c7:0c634ede:dc079060:444e5d09
ARRAY /dev/md1 metadata=1.2 UUID=f9a4ad55:27581f00:fa427411:a0d92417
ARRAY /dev/md5 metadata=1.2 UUID=b56dfa9c:d8552686:07930928:0dab99a8

# 업데이트 시스템 적용 => 한 번만 하면 됨
update-initramfs -u

# 리부트
reboot

# 결과 확인
df
ls -l /dev/md*
```

### RAID 문제 발생 테스트

```bash
# 디스크 제거 시 INACTIVE상태가 됨
# INACTIVE 상태인 RAID 다시 ACTIVE 상태로 변환
mdadm --run 장치명
mdadm --run /dev/md1

# mount
mount /dev/md1 /raid1

# 확인
df
mdadm --detail 장치명

# 손상있는 부분 설정 주석처리
gedit /etc/fstab

# 디스크 제거로 인한 RAID의 고장은 다시 디스크를 빈 부분에 추가를 해줘야 함
# Linear, RAID0은 디스크 제거로 문제 발생 시 이전 데이터는 사용 불가

# 디스크를 다시 추가 후 fdisk로 파티션을 설정을 똑같이 해야함

# 파티션 상태 확인
fdisk -l /dev/sdb

# fdisk : 물리적인 하드 디스크를 분할(파티션)하는 명령어
# fdisk /dev/sdb => sda ~ 순서대로
fdisk 장치명

# 이전 사용 RAID stop 후 다시 생성
mdadm --stop /dev/md9

# 손실된 RAID 재 생성

# linear  RAID 생성
mdadm --create RAID이름 --level=RAID형태 --raid-divices=장치개수 장치명...
# --level=linear, --level=raid0, --level=raid1, --level=raid5, --level=raid6
mdadm --create /dev/md9 --level=linear --raid-devices=2 /dev/sdb1 /dev/sdc1
# Continue Creating Array? => y입력

# RAID 1과 RAID 5는 하나의 디스크 결함으로는 데이터는 남아있으므로
# 추가만 해주면 된다.
mdadm /dev/md1 --add /dev/sdg1

# 확인
df

# 장치 상세한 상황 확인
mdadm --detail 장치명
mdadm --detail /dev/md9

# 재설정
gedit /etc/fstab

# 장치 다시확인 후 설정
mdadm --detail --scan

# 변경된 사항이 존재 시 재설정 아니면 그대로 사용
# name=server:5 부분은 제거할 것
gedit /etc/mdadm/mdadm.conf
ARRAY /dev/md5 metadata=1.2 name=server:5 UUID=b56dfa9c:d8552686:07930928:0dab99a8
ARRAY /dev/md1 metadata=1.2 name=server:1 UUID=f9a4ad55:27581f00:fa427411:a0d92417
ARRAY /dev/md9 metadata=1.2 name=server:9 UUID=1fd04e94:5658f664:147ca408:b7fbdb45
ARRAY /dev/md0 metadata=1.2 name=server:0 UUID=af5fcc14:0a78ddc6:b654686e:c89bf535

# 다시시작
reboot

# 리부트 후 변경사항 확인
df

# md 적용이 안되있을 시 
mdadm --detail --scan
ARRAY /dev/md/server:0 metadata=1.2 name=server:0 UUID=af5fcc14:0a78ddc6:b654686e:c89bf535
ARRAY /dev/md5 metadata=1.2 name=server:5 UUID=b56dfa9c:d8552686:07930928:0dab99a8
ARRAY /dev/md1 metadata=1.2 name=server:1 UUID=f9a4ad55:27581f00:fa427411:a0d92417
ARRAY /dev/md/server:9 metadata=1.2 name=server:9 UUID=1fd04e94:5658f664:147ca408:b7fbdb45

# /dev/md가 이전 이름과 바뀌어 있을 수 있음 => /dev/md/server:0
# 바뀐이름으로 /ets/fstab을 재설정 해줘야 함
gedit /etc/fstab

# 리부트
reboot

# 다시 디스크 확인
df
```

**디스크가 정상일 상태일 때 백업을 해두는 것이 안전하다.**

### LVM(Logical Volume Manage)

리눅스의 저장 공간을 효율적이고 유연하게 관리하기 위한 커널의 한 부분이다.

여러 개의 하드디스크를 합쳐서 한 개의 파일 시스템으로 사용하는 것으로 필요에 따라서 다시 나눌 수 있다.

예) 2TB 용량의 하드디스크 2개를 합친 후에 다시 1TB와 3TB로 나누어 사용할 수 있다.

- 용어
- PhysicalVolume(물리 볼륨) : /dev/sda1, /dev/sdb1 등의 파티션
- Volume Griuo(볼륨 그룹) : 물리 볼륨을 합쳐서 1개의 물리 그룹으로 만드는 것
- Logical Volume(논리 볼륨) : 볼륨 그룹을 1개 이상으로 나눠서 논리 그룹으로 나눈 것

![Untitled (9)](https://user-images.githubusercontent.com/95058915/206432347-314c701e-ff6f-42f6-a19e-41abe1ca17f6.png)


### LVM 구현

![Untitled (10)](https://user-images.githubusercontent.com/95058915/206432353-aefe493e-628c-479c-90ae-d85a061116b3.png)


```bash
ls -l /dev/sd*

# LVM를 생성하기 위한 패키지 설치
apt -y install lvm2

# fdisk : 물리적인 하드 디스크를 분할(파티션 생성)하는 명령어
# fdisk /dev/sdb => sda ~ 순서대로
fdisk 장치명
fdisk /dev/sdb
# 생성 시 t를 사용해서 타입 LVM로 지정
# Command 명령어
n # 새로운 파티션 생성
t # 타입 변경
8e # LVM 타입
w # 쓰기(저장)

# 물리 볼륨으로 만들어줌
pvcreate 파티션
pvcreate /dev/sdb1

# 볼륨 그룹으로 만들어줌
vgcreate 볼륨그룹명 파티션...
vgcreate myVG /dev/sdb1 /dev/sdc1

# 현재 볼륨 그룹 확인
vgdisplay

# 논리 그룹으로 쪼개기
lvcreate --size 지정할크기 --name 논리그룹명 볼륨그룹명
lvcreate --size 1G --name myLG1 myVG
# 나머지 용량 사용하기
lvcreate --extents 100%FREE --name myLG3 myVG

# 확인
ls -l /dev/myVG

# 파일 시스템 생성
mkfs.ext4 /dev/myVG/myLG1

# 디렉토리 생성
mkdir /lvm1 /lvm2 /lvm3
# 마운트(물리적인 장치를 디렉토리에 연결)
mount /dev/myVG/myLG1 /lvm1

# 영구적용 설정
gedit /etc/fstab

# 맨 아랫줄 삽입
/dev/myVG/myLG1 /lvm1 ext4 defaults 0 0
```

### 사용자별 공간 할당 - 쿼터

파일 시스템마다 사용자나 그룹이 생성할 수 있는 파일의 용량 및 개수를 제한하는 것

파일 시스템을 “/” 로 지정하는 것보다는 별도의 파일 시스템을 지정해서 해당 부분을 쓰도록 하는 것이 좋다.

“/” 파일 시스템을 많은 사용자가 동시에 사용하게 되면 우분투 서버를 운영하기 위해서 디스크를 읽고 쓰는 작업과 일반 사용자가 디스크를 읽고 쓰는 작업이 동시에 발생하므로 전반적인 시스템 성능이 저하된다.

**쿼터 진행 순서**

![Untitled (11)](https://user-images.githubusercontent.com/95058915/206432364-88090b56-2258-47e1-925c-050f8e61b282.png)


```bash
# 파티션 생성
fdisk /dev/sdb

# 생성
n
# 쓰기(저장)
w

# 파일 시스템 생성
mkfs.ext4 /dev/sdb1

# 디렉토리 생성
mkdir /userHome

# 마운트 (물리적인 장치를 디렉토리에 연결)
mount /dev/sdb1 /userHome/

# /etc/fstab 영구 적용
gedit /etc/fstab

# 맨 아래에 삽입
/dev/sdb1 /userHome ext4 defaults 0 0

## 테스트 사용자 등록
adduser --home /userHome/kang kang
adduser --home /userHome/john john

# 디스크를 쿼터용으로 제한
gedit /etc/fstab

# 내용 변경
/dev/sdb1 /userHome ext4 defaults,usrjquota=aquota.user,jqfmt=vfsv0 0 0

# 재부팅  (둘 중 하나 사용)
reboot
mount --options remount /userHome # 재부팅하는 효과를 줌

# mount 확인
mount

# 쿼터 패키지 설치
apt -y install quota

# 쿼터 off
quotaoff -avug

# 쿼터 체크
quotacheck -augmn

rm -f aquota.*
# 다시 쿼터 체크
quotacheck -augmn

# 사용자와 그룹에 대한 쿼터 설정 파일 생성
touch aquota.user aquota.group

# 접근권한 변경 - 소유자만 읽고 쓰기 가능
chmod 600 aquota.*
# 다시 쿼터 체크
quotacheck -augmn

# 쿼터 켜기
quotaon -avug

# 사용자에게 공간 제한
edquota -u kang
# Filesystem : 쿼터를 할당하는 파일 시스템(파티션)
# blocks : 현재 사용중인 용량(KB)
# soft : 사용할 수 있는 최대 용량, 지정한 용량이 hard에 지정한 값보다 
# 작게 설정되어 있다면 hard 용량에 도달하기 전까지 soft용량을 초과해도 
# 지정한 유예기간동안 초과 가능하다.
# 유예기간 내에 soft 설정 값 만큼 용량이 정리되지 않는다면 
# 그 이상 파일 생성의 작업을 할 수 없다.
# hard : 사용할 수 있는 최대 용량, 초과 사용 불가
# inodes : 파일의 개수
# soft, hard : inode 값으로 파일이나 폴더를 생성할 수 있는 개수. 위와 동일

# 설정 soft 30메가, hard 40메가
Filesystem                   blocks       soft       hard     inodes     soft     hard
/dev/sdb1                        16       30720      40960         4        0        0
# 저장
[Ctrl] + O
[Ctrl] + x # 종료

# 사용
su - kang

whoami
pwd

# 파일복사 30메가까지 사용
cp /boot/vmlinuz.* test1
cp /boot/vmlinuz.* test2

# 두개를 더 복사하면 초과 메시지 발생
cp /boot/vmlinuz.* test3
cp /boot/vmlinuz.* test4

# quota 확인
quota

# 사용자 변경
exit

# 전체 사용자의 쿼터 확인
repquota /userHome

# 쿼터 설정이 같을 때 복사 하는법
edquota -p kang john
```
