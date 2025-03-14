/**
 * 제네릭
 * 제네릭은 타입을 직접 지정하지 않고, 타입을 파라미터로 받아 동적으로 결정지어 사용하는 방법이다.
 * 런타임에 타입을 지정하므로 코드의 유연성이 증가하며, 같은 코드로 타양한 타입에 대해 동작하는 코드를 작성할 수 있다.
 */
function genericFunc<T>(value: T): T {
  console.log(typeof value);
  return value;
}

// 타입이 동적으로 문자열로 추론됨
let genericStr: string = genericFunc("hello");

// 타입을 여러 개 지정하는 것도 가능
function genericFunc2<T, E>(a: T, b: E): void {
  // ... 처리 로직
  console.log(`value a type=${typeof a}, value b type=${typeof b}`);
}

// 배열타입을 매개변수로 갖는 제네릭 함수
function genericFunc3<T>(arr: Array<T>): number {
  return arr.length;
}


// 제네릭 인터페이스
interface GenericInterfaceV1 {
  <T> (arg: T): T;
}


// 제네릭 클래스
class CommonResponse<T> {
  private statusCode: number;
  private data: T;

  constructor(statusCode: number, data: T) {
    this.statusCode = statusCode;
    this.data = data;
  }

  getResponse(): any {
    return {
      code: this.statusCode,
      data: this.data
    };
  }
}