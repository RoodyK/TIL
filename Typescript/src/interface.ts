/**
 * 인터페이스
 * 인터페이스는 객체의 구조를 정의할 때 주로 사용한다. 클래스에서 사용할 구조를 정의할 수 있다.
 * 인터페이스는 상속(또는 확장)을 통해 다른 인터페이스나 타입을 확장할 수 있다.
 * 인터페이스는 다중 상속(확장)이 가능하다.
 * 
 * interface와 type의 차이점
 * 인터페이스는 union이나 intersection 타입을 정의할 수 없다.
 */
interface Beverage {
  name: string;
  price: number;
}

const water: Beverage = {
  name: "물",
  price: 200
}


// 선택적 프로퍼티
interface Beverage2 {
  name: string;
  price?: number;
}

const water2: Beverage2 = {
  name: "물",
  // price: 200
}


// 읽기전용 프로퍼티
interface Beverage3 {
  readonly name: string;
  price?: number;
}

const water3: Beverage3 = {
  name: "물",
  // price: 200
}

// 에러 발생 Cannot assign to 'name' because it is a read-only property
// water3.name = "커피";


// 메서드 타입 정의 및 오버로딩
interface Beverage4 {
  readonly name: string;
  price?: number;
  getInfo(): void;
  getInfo(a: string): void;
  getInfo(a: string, b: string): void;
}


// 함수 타입
// 인터페이스는 함수 타입을 설명할 수 있다.
interface SearchFunc {
  (source: string, subString: string): boolean;
}

let mySearch: SearchFunc;
mySearch = function(src: string, sub: string) {
    let result = src.search(sub);
    return result > -1;
}

mySearch = function(src, sub) {
  let result = src.search(sub);
  // 반환 타입이 다르면 에러가 발생한다.
  // return result + ""
  return result > -1;
}


/**
 * 인터페이스는 중복 선언 가능하지만 프로퍼티가 서로 다른 타입일 수 없다.
 */
interface Animal4 {
  name: string;
  leg: number;
}

interface Animal4 {
  // name: number; // 에러 발생 - 타입이 다를 수 없음
  leg: number;
}


/**
 * 클래스 타입
 */
// 인터페이스 구현하기
// 자바와 같이 인터페이스를 사용할 수 있다.
interface MemberService {
  save(id: number): number;
}

class MemberServiceImpl implements MemberService {
  save(id: number) {
    console.log("회원 저장완료");
    return id;
  }
}


/**
 * 인터페이스 확장
 * 하나의 인터페이스를 다른 인터페이스들이 상속받아 중복된 프로퍼티를 정의하지 않도록 도와주는 문법
 */
interface Animal2 {
  name: string;
  leg: number;
}

interface Dog2 extends Animal2 {
  name: "강아지" // 타입 재정의
  move(): void;
}

interface Cat2 extends Animal2 {
  jump(): void;
}

// 부모 타입의 프로퍼티의 타입(ex. name:string)을 다른 타입(name:number)으로 변경하면 부모의 서브 타입이 아니게 되므로 불가능하다.
interface errorDog extends Animal2 {
  // name: number; // 에러 발생
  move(): void;
}


/**
 * 타입 별칭으로 정의된 객체도 확장 가능
 */
type Animal3 = {
  name: string;
  leg: number;
}

interface Dog3 extends Animal3 {
  move(): void;
}
