# Spring IoC 와 DI

## IoC(Inversion of Control) 제어의 역전

\- 프로그램의 제어권을 프로그래머가 아닌 Framework가 가져가는 것.  
\- 개발자가 모든 제어의 중심을 갖지만, 소스 코드에 전체에 대한 제어는 프레임워크가 하며, 개발자가 설정(어노테이션 사용)만 해주면 Spring Container가 알아서 처리해준다.  
  
<br/>
<br/>

## **스프링 컨테이너**

스프링에서는 빈의 생성과 관계설정 같은 제어를 담당하는 IoC컨테이너인 빈 팩토리가 존재하는데, 그 외에도 빈의 생성과 관계설정 외에 추가적인 기능이 필요한데는 ApplicationContext를 주로 사용하며, ApplicationContext 를 스프링 컨테이너라 한다.  
  

ApplicationContext는 별도의 설정 정보를 참고하고 IoC를 적용하여 빈의 생성, 관계설정 등의 제어 작업을 총괄한다. (객체의 생성 정보와 연관관계정보에 대한 설정을 읽어서 처리.)

ApplicationContext는 BeanFactory를 상속받아서 만든 인터페이스이며 BeanFactory는 스프링 컨테이너의 최상위 인터페이스이다.

ApplicationContext는 별도의 정보를 참고해서 빈의 생성, 관계설정 등의 제어를 총괄하며 많은 부가기능을 제공한다.  
\- 메시지 소스를 활용한 국제화 기능(한국에서 들어오면 한국어로, 영어권에서 들어오면 영어로 출력)  
\- 환경변수(로컬, 개발, 운영등을 구분해서 처리)  
\- 애플리케이션 이벤트(이벤트를 발행하고 구독하는 모델을 편리하게 지원  
\- 편리한 리소스 조회(파일 클래스패스, 외부 등에서 리소스를 편리하게 조회)  
<br/>

ApplicationContext 인터페이스를 구현한 클래스들  
\- ClassPathXmlApplicationContext : ClassPath에 위치한 xml 파일을 읽어 설정 정보를 로딩, root로부터 경로를 지정함  
\- FileSystemXmlApplicationContext : 파일 경로로 지정된 곳의 xml을 읽어 설정 정보를 로딩  
\- XmlWebApplicationContext : 웹 어플리케이션에 위치한 곳에서 xml파일을 읽어 설정 정보를 로딩  
\- AnnotationConfigApplicationContext : @Configuration 어노테이션이 붙은 클래스를 이용하여 설정 정보로 로딩  
<br/>
<br/>

### Bean이란

**자바에서의 javaBean**  
\- 데이터를 저장하기 위한 구조체로 자바 빈 규악이라는 것을 따르는 구조체.  
\- 캡슐화를 통해서 pirvate 프로퍼티와 getter/setter로만 데이터를 접근.  
<br/>

**Spring에서의 Bean**  
\- 스프링 IoC 컨테이너에 의해 생성되고 관리되는 객체.  
\- Java에서처럼 new Object();로 생성하지 않는다  
\- 각각의 Bean들 끼리는 서로를 의존(사용)할 수 있다.  
\- Bean으로 설정되면 싱글톤형식으로 인스턴스화되서 사용된다.  
\- @Bean으로 주입받을 수 있는 것은 객체의 변수, 생성자, set메소드(클래스는 @Component 사용)  
<br/>

Bean의 등록은 과거에는 xml파일로 설정을 따로 관리해서 등록 했지만, 현재는 Annotation을 기반으로 Bean으로 편리하게 등록이 가능하다.  
\- ex) @Bean, @Controller, @Service @Configuration  
<br/>

**Bean의 요청 시 처리과정**  
1.  ApplicationContext는 @Configuration이 붙은 클래스들을 설정 정보로 등록해두고, @Bean이 붙은 메소드의 이름으로 빈 목록을 생성.  
2.  클라이언트가 해당 빈을 요청.  
3.  ApplicationContext는 자신의 빈 목록에서 요청한 이름이 있는지 찾는다.  
4.  ApplicationContext는 설정 클래스로부터 빈 생성을 요청하고, 생성된 빈을 돌려준다.  
<br/>
<br/>

## DI(Dependency Injection) 의존성 주입

의존이란 변경에 의해 영향을 받는 관계를 의미한다.  
객체 자체가 아니라 Framework에 의해 객체의 의존성이 주입되는 설계 패턴이다.  
빈 컨테이너로 객체가 실행되기 전에 그 객체가 필요로 하는 의존 객체를 주입해 주는 역할을 수행한다.  
<br/>  

**DI의 장점**  
\- 의존성으로부터 격리시켜 코드 테스트에 용이하다.  
\- DI를 통해서 불가능한 상황을 Mock와 같은 기술을 통해 안정적으로 테스트가 가능하다.  
\- 코드를 확장하거나 변경할 때 영향을 최소화한다.(추상화)  
\- 순환참조를 막을 수 있다.  
<br/>

**스프링 빈 설정**  

```java
@Configuration // 스프링 설정 클래스로 등록
public class AppContext {
  @Bean // 스프링 빈으로 등록 
  public MemberDao memberDao() {
    return new MemberDao(); 
  } 
}
```
<br/>
<br/>
<br/>
  

@Configuration으로 선언한 스프링 설정을 담는 클래스에서 등록한 Bean을 사용하려면 설정 클래스를 이용해서 스프링 컨테이너인 ApplicationContext객체를 선언해서 사용해야 한다.  
<br/>

```java
public class Main {
    public static void main(String[] args) { 
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppContext.class); 
        MemberDao memberDao = ctx.getBean(memberDao.class); // new 생성자가 아닌 스프링 컨테이너에서 주입받음 
    } 
}
```
<br/>


@Autowired : 의존에 필요한 Bean을 찾아서 의존성을 자동 주입한다.(동일한 빈이 두 개 이상이면 예외 발생)  
@Qualifier : 동일한 빈이 존재할 때 의존 객체를 선택할 수 있다.(자동 주입할 Bean을 지정)  
@Autowired(required = false) : 매칭되는 빈이 없어도 예외를 발생하지 않고 자동 주입을 수행하지 않는다.(자동 주입이 되는 대상이 되는 필드나 메서드에 null을 전달하지 않는다.)  
  

```java
@Configuration
public class AppContext {

    @Bean
    @Qualifier("printer")
    public MemberPrinter memberPrinter() {
      return new MemberPrinter();
    }

    @Bean  
    @Qualifier("summaryPrinter")  
       public MemberPrinter memberPrinter2() {
        return new MemberSummaryPrinter();
    }  
}
```
<br/>

```java
public class MemberListPrinter {

    @Autowired // 의존관계 자동주입  
    public void setMemberDao(MemberDao memberDao) {  
        this.memberDao = memberDao;  
    }

    @Autowired
    @Qualifier("summaryPrinter") // 빈이 여러개일 경우 예외가 발생하기 때문에 주입될 빈 지정
    public void setPrinter(MemberPrinter memberPrinter) {
        this.printer = memberPrinter;
    }
}
```
<br/>
<br/>
<br/>

**컴포넌트 스캔을 사용한 의존관계 주입**

```java
@Configuration // 설정 클래스 지정
@ComponentScan(basePackages = {com.sample.spring}) // 등록된 컴포넌트(빈)를 읽어들임
public class AppConfig {

}

@Repository
public class MySqlMemberRepository implements MemberRepository {

    Map<Integer, Member> store = new HashMap<>();

    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findByName(Integer id) {
        return store.get(id);
    }
}


@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void join(Member member) {

        if (member == null) {
            throw new IllegalArgumentException();
        }

        memberRepository.save(member);
    }

    public Member findMember(Integer id) {
        return memberRepository.findByName(id);
    }
}
```

\- @Component : class를 spring bean으로 등록할 때 사용한다. 어노테이션에 값을 따로 지정하지않으면 클래스의 첫글자가 소문자로 바뀐 이름을 빈이름으로 사용하고(MemberRepository => memberRepository) 지정한 이름이 있으면 그 이름이 빈으로 등록된다.  
\- @Component를 붙인 클래스를 스캔해서 스프링 빈으로 등록하려면 빈을 설정하는 클래스에 @ComponentScan을 적용해야 한다.  
\- @ComponentScan(basePackages={})로 basePackages를 설정하면 지정한 패키지를 포함한 모든 하위 패키지를 스캔하게 된다.  
\- 컴포넌트 스캔 대상에 @Component를 제외하고도 포함되는 것들이 있는데 MVC와 관련이 있는 @Controller, @Service, @Repository및 @Aspect @Configuration 어노테이션은 @Component를 포함하므로 컴포넌트 스캔 대상에 포함된다.  


<br/>
<br/>
<br/>
