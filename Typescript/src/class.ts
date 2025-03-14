/**
 * 클래스
 * 타입스크립트에서는 자바와 같은 언어처럼 접근제어자를 지정해서 사용할 수 있다.
 * 
 * public
 * - public 접근 제어자는 클래스 외부에서 해당 멤버를 자유롭게 접근할 수 있도록 한다. 
 * - 기본적으로 모든 클래스 멤버는 public이다. 즉, 별도로 지정하지 않으면 해당 멤버는 public으로 취급된다.
 * 
 * private
 * - private 접근 제어자는 해당 멤버가 클래스 내부에서만 접근 가능하도록 제한한다. 
 * - 클래스 외부에서는 이 멤버에 접근할 수 없으며, 상속받은 클래스에서도 접근할 수 없다. 
 * - 이를 통해 클래스 외부에서 멤버에 직접 접근할 수 없도록 보호할 수 있다.
 * 
 * protected
 * - protected 접근 제어자는 해당 멤버가 클래스 내부와 해당 클래스를 상속받은 자식 클래스에서만 접근 가능하도록 한다. 
 * - 클래스 외부에서는 접근할 수 없으며, 상속된 클래스에서는 접근할 수 있다.
 */
class ConsoleHandler {
  private use: string

  constructor(use: string) {
    this.use = use;
  }

  description(): void {
    console.log(`이 핸들러의 용도는 ${this.use} 입니다.`);
  }
}

class InputHandler extends ConsoleHandler {
  constructor(use: string) {
    super(use);
  }

  description(): void {
    console.log("입력 핸들러입니다.");
  }
}

class OutputHandler extends ConsoleHandler {
  constructor(use: string) {
    super(use);
  }

  description(): void {
    console.log("출력 핸들러입니다.");
  }
}

const consoleInputHandler = new InputHandler("input handling");
// consoleInputHandler.use // private 필드 접근 불가


// 읽기전용 지정자
// readonly 키워드를 사용하여 프로퍼티를 읽기전용으로 만들 수 있다.
// 읽기전용 프로퍼티들은 선언 또는 생성자에서 초기화해야 한다.
class Lion {
  readonly name: string;
  readonly leg: number = 4;

  constructor(name: string) {
    this.name = name;
  }
}

const lion = new Lion("이빨빠진 사지");
// lion.name = "초원의 사자"; // readonly 이므로 수정할 수 없다.


// 전역 프로퍼티(static)
// 전역 변수는 클래스.변수명 으로 사용가능하며, 인스턴스를 생성하지 않고 사용할 수 있다.
class Mike {
  static leg = 2;
}

Mike.leg;


// 추상 클래스
abstract class Department {

  constructor(public name: string) {
  }

  printName(): void {
      console.log("Department name: " + this.name);
  }

  abstract printMeeting(): void; // 자식 클래스에서 추상 메서드를 구현해야 한다.
}

class AccountingDepartment extends Department {
  constructor() {
      super("Accounting and Auditing"); // 자식 클래스의 생성자는 반드시 super()를 호출해야 한다.
  }

  printMeeting(): void {
      console.log("The Accounting Department meets each Monday at 10am.");
  }
}