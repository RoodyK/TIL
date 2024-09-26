# 설계 품질과 트레이드 오프

- 협력: 애플리케이션 기능을 구현하기 위해 메시지를 주고받는 객체들 사이의 상호작용
- 책임: 객체가 다른 객체와 협력하기 위해 수행하는 행동
- 역할: 대체 가능한 책임의 집합  
<br/>

객체지향 설계란 올바른 객체에게 올바른 책임을 할당하면서 낮은 결합도와 높은 응집도를 가진 구조를 창조하는 활동이다.  
이 정의에는 객체지향 설계의 핵심이라는 관점과, 책임을 할당하는 작업이 응집도와 결합도 같은 설계 품질과 깊이 연관되어 있다는 관점이 섞여있다.  

설계는 변경을 위해 존재하며 변경에는 무조건 비용이 발생하는데, 좋은 설계는 합리적인 비용 안에서 변경을 수용할 수 있는 구조를 만드는 것이다.  
이러한 설계는 응집도가 높고 서로 느슨하게 결합돼 있는 요소로 구성된다.  
<br/>
<br/>

## 데이터 중심의 영화 예매 시스템
객체지향 설계에서 시스템을 객체로 분할하는 두 가지 방법
- 상태를 분할의 중심축으로 삼는 방법
- 책임을 분할의 중심축으로 삼는 방법  
<br/>

상태는 구현으로 불안정하며 변하기 쉽다. 따라서 시스템 분할은 책임에 초점을 맞춰야 한다.  
책임은 인터페이스에 속하고, 객체는 책임을 드러내는 안정적인 인터페이스 뒤로 책임을 수행하는 데 필요한 상태를 캡슐화함으로써 구현 변경에 대한 파장이 외부로 퍼져나가는 것을 방지한다. (안정적인 설계 가능)  
<br/>

### step01 데이터 중심 설계
객체 내부에 저장되는 데이터를 기반으로 시스템을 분할하는 방법. 객체가 포함해야 하는 데이터에 집중한다.    

금액 할인 정책의 할인 금액(discountAmount)과, 비율 할인 정책의 할인비율을 Movie 안에 직접 정의  

객체지향의 가장 중요한 원칙은 캡슐화이므로 내부 데이터가 외부의 다른 객체들을 오염시키는 것을 막아야 한다.  

이 로직의 데이터 처리는 ReservationAgency 클래스에서 처리하고 있다.  
<br/>
<br/>

## 설계 트레이드 오프

### 캡슐화 
객체는 상태와 행동울 안에 모으는 데 내부 구현을 외부로부터 감추기 위함이다. 여기서 구현은 나중에 변경될 가능성이 높은 것을 말한다.  
객체지향이 변경에 대한 파급효과를 조절할 수 있는 장치를 제공한다. 변경 가능성이 높은 부분은 숨기고 상대적으로 안정적인 부분만 공개할 수 있다.  
변경될 가능성이 높은 부분을 **구현**이라고 부르고 상대적으로 안정적인 부분을 **인터페이스**라고 부른다.

객체지향에서 가장 중요한 원리는 **캡슐화**로, 외부에서 알 필요가 없는 부분을 감춤으로써 대상을 단순화하는 추상화의 한 종류다.  

설계가 필요한 이유는 요구사항이 변동되기 때문이고, 캡슐화가 중요한 이유는 불안정한 부분과 안정적인 부분을 분리해서 변경의 영향을 통제할 수 있기 때문이다. 따라서 변경의 관점에서 설꼐의 품질을 판단하기 위해 캡슐화를 기준으로 삼을 수 있다.  

캡슐화는 변경 가능성이 높은 부분을 객체 내부로 숨기는 추상화 기법이다.
<br/>

### 응집도와 결합도 
**응집도**는 모듈에 포함된 내부 요소들이 연관돼 있는 정도를 나타낸다.  
모듈 내의 요소들이 하나의 목적을 위해 협력한다면 그 모듈은 높은 응집도를 갖고, 서로 다른 목적을 추구한다면 낮응 응집도를 가진다.  

**결합도**는 의존성 정도를 나타내며 다른 모듈에 대해 얼마나 많은 지식을 갖고 있는지를 나타내는 척도다.  
어떤 모듈이 다른 모듈에 대해 자세한 부분까지 알고 있다면 높은 결합도를, 꼭 필요한 지식만 알고 있다면 낮은 결합도를 가진다.  
<br/>
  
일반적으로 좋은 설계란 높은 응집도와 낮은 결합도를 가진 모듈로 구성된 설계를 의미하며, 오늘의 기능을 수행하면서 내일의 변경을 수용할 수 있는 설계를 말한다.  

높은 응집도와 낮은 결합도를 가진 설계를 추구해야 하는 이유는 설계를 변경하기 쉽게 만들기 때문이다.  
변경의 관점에서 응집도란 변경이 발생할 때 모듈 내부에서 발생하는 변경의 정도로 측정할 수 있다.  

응집도가 높은 설계는 요구사항 변경을 위해 하나의 모듈만 수정하면 되지만, 응집도가 낮은 설계는 하나의 원인에 의해 다수의 모듈을 동시에 수정해야 한다.  
응집도가 높을수록 변경의 대상과 범위가 명확해지기 때문에 코드를 변경하기 쉬워진다.  
<br/>

결합도는 한 모듈이 변경되기 위해서 다른 모듈의 변경을 요구하는 정도로 측정할 수 있다. 하나의 모듈을 수정할 때 얼마나 많은 모듈을 수정해야 하는지를 나타낸다.  
내부 구현을 변경했을 때 이것을 다른 모듈에 영향을 미치는 경우에는 두 모듈 사이의 결합도가 높다고 말한다.  
클래스의 구현이 아닌 인터페이스에 의존하도록 코드를 작성해야 낮은 결합도를 얻을 수있다.  
<br/>

캡슐화를 지키면 모듈 안의 응집도는 높아지고 모듈 사이의 결합도는 낮아진다.
<br/>
<br/>

## 데이터 중심의 영화 예매 시스템의 문제점  

데이터 중심의 설계는 캡슐화를 위반하고 객체의 내부 구현을 인터페이스의 일부로 만든다. 
책임 중심의 설계는 객체의 내부 구현을 안정적인 인터페이스 뒤로 캡슐화한다.  

데이터 중심 설계의 문제는 캡술화 위반, 높은 결합도, 낮은 응집도로 요약할 수 있다.  
<br/>

### 캡슐화 위반 
데이터 중심으로 설계한 Movie 클래스에서 setter 메서드를 통해서만 객체 내부의 상태에 접근하므로 캡슐화가 지켜진 것 같지만 어떤 정보도 캡슐화하지 못한다.  
getFee, setFee 메서드는 Movie 내부에 Money 타입의 fee 라는 이름의 인스턴스 변수가 존재한다는 사실을 퍼블릭 인터페이스에 노골적으로 드러낸다.   

Movie가 캡슐화를 어긴 근본적 원인은 객체가 수행할 책임이 아니라 내부에 저장할 데이터에 초점을 맞췄기 때문이다. 객체에게 중요한 것은 책임이다.  
<br/>

### 높은 결합도 
데이터 중심 설계는 객체의 캡슐화를 약화시키기 때문에 클라이언트가 객체의 구현에 강하게 결합된다.  
여러 데이터 객체들을 사용하는 제어 로직이 특정 객체안에 집중되기 때문에 하나의 제어 객체가 다수의 데이터 객체에 강하게 결합된다는 단점도 있다.  
이 결합도로 인해 어떤 데이터 객체를 변경해도 제어 객체를 함께 변경할 수 밖에 없다.
  
예제에서는 ReservationAgency가 모든 데이터 객체에 의존한다.   
<br/>

### 낮은 응집도
서로 다른 이유로 변경되는 코드가 하나의 모듈 안에 공존할 때 모듈의 응집도가 낮다고 말한다.  
각 모듈의 응집도를 살펴보기 위해서는 코드를 수정하는 이유가 무엇인지 살펴봐야 한다. 
<br/>

**낮은 응집도가 설계의 문제를 일으키는 두 가지 측면**
- 변경의 이유가 서로 다른 코드들을 하나의 모듈 안에 뭉쳐놓았기 때문에 변경과 아무 상관이 없는 코드들이 영향을 받게 된다.  
- 하나의 요구사항 변경을 반영하기 위해 동시에 여러 모듈을 수정해야 한다. 응집도가 낮을 경우 다른 모듈에 위치해야 할 챔임의 일부가 엉뚱한 곳에 위치하게 되기 때문이다.  
<br/>

#### 단일 책임 원칙(Single Responsibility Principle, SRP)
하나의 클래스는 단 한가지의 책임(변경 이유)만 가져야 한다는 원칙이다.  
<br/>
<br/>

## 자율적인 객체를 향해
객체는 스스로의 상태를 책임져야 하며 외부에서는 인터페이스에 정의된 메서드를 통해서만 상태에 접근할 수 있어야 한다.  
여기서의 메서드는 단순히 속성 하나의 값을 반환하거나 변경하는 것이 아닌 객체가 챔임져야 하는 무언가를 수행하는 메서드다.  
속성의 가시성을 private으로 설정해도 get, set 메서드를 통해 외부로 제공하면 캡슐화를 위반하는 것이다.  

캡슐화가 제대로 되어있지 않은 객체는 코드 중복, 변경에 취약하다는 치명적인 단점이 있다.  
<br/>

상태와 행동을 개겣라는 하나의 단위로 묶는 이유는 객체 스스로 자신의 상태를 처리할 수 있게 하기 위해서다.  
객체는 단순한 데이터 제공자가 아닌 객체 내부에 저장되는 데이터보다 객체가 협력에 참여하면서 수행할 책임을 정의하는 오퍼레이션이 더 중요하다.  
<br/>

객체 설계시 필요한 질문 
- 이 객체가 어떤 데이터를 포함해야 하는가?
- 이 객체가 데이터에 대해 수행해야 하는 오퍼레이션은 무엇인가?  
<br/>

### 캡슐화의 진정한 의미
캡슐화는 **변경될 수 있는 어떤 것이라도 감추는 것**을 의미한다.  
내부 속성을 외부로부터 감추는 것은 '데이터 캡슐화'라고 불리는 캡슐화의 한 종류일 뿐이다.  

내부 구현의 변경으로 인해 외부의 객체가 영향을 받는다면 캡슐화를 위반한 것이다. 설계에서는 변하는 것이 무엇인지 고려하고 변하는 개념을 캡슐화해야 한다.  
<br/>
<br/>

## 데이터 중심 설계의 문제점

데이터 중심의 설계가 변경에 취약한 이유
- 데이터 중심의 설계는 본질적으로 너무 이른 시기에 데이터에 관해 결정하도록 강요한다.
- 데이터 중심의 설계에서는 협력이라는 문맥을 고려하지 않고 객체를 고립시킨 채 오퍼레이션을 결정한다.  
<br/>

데이터를 먼저 결정하고 데이터를 처리하는 데 필요한 오퍼레이션을 나중에 결정하는 방식은 데이터에 관한 지식이 객체의 인터페이스에 그대로 드러나게 된다.  

데이터 중심의 설계는 너무 이른 시기에 데이터에 대해 고민하기 때문에 캡슐화에 실패하게 된다.  

객체지향 설계는 협력하는 객체들의 공동체를 구축한다는 것을 의미하고, 올바른 객체지향 설계의 무게 중심은 항상 객체 네부가 아니라 외부에 맞춰져 있어야 한다.  
<br/>