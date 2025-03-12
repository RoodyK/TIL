// 자료형
let num: number = 10;
let str: string = "hello typescript";
let bool: boolean = true;
let nullValue: null = null;
let undefindValue: undefined = undefined;


// 배열
let numArr: Array<number> = [1,2,3];
let strArr: string[] = ["1", "2", "3"];


// 튜플 타입을 사용하면 요소의 타입과 개수가 고정된 배열을 표현 가능. 단 요소의 타입이 모두 같을 필요는 없다.
let tuple: [string, number] = ["typescript", 11];


// enum
// enum은 값의 집합에 더 나은 이름을 붙여줄 수 있다.
// enum은 기본적으로 0부터 시작해서 멤버들의 번호를 매긴다.
enum Color {Red = "Red", Green = "Green", Blue = "Blue"};
let colorValue: Color = Color.Blue;
enum Fruit {Banana, Apple, Cherry};
let fruitValue: string = Fruit[1];


// any
// any는 특정 값으로 인하여 타입 검사 오류가 발생하는 것을 원하지 않을 때 사용할 수 있다. 하지만 타입 안정성은 떨어진다.
// 사용자로부터 받은 데이터나 서드 파티 라이브러리같은 동적인 컨텐츠에서 타입을 검사하지 않고 통과하고자 할 때 사용할 수 있다.
let appValue: any = "what?";
appValue = 11; // 문제 없음
appValue = true;    // 문제 없음
appValue = {};      // 문제 없음


// unknown
// unknown 타입은 any 타입과 비슷하게 어떤 값이든 저장할 수 있지만 unknown은 안전한 타입 검사를 제공한다.
// unknown은 "어떤 타입인지는 모르지만, 그 값을 사용하기 전에 타입 검사를 해야 한다"는 제약을 둔다.
let unknownValue: unknown = "what?";
unknownValue = 11; // 문제 없음

// 오류 발생: unknown 타입을 바로 사용하려고 하면 타입 검사 필요
//let refUnknownValue: string = unknownValue; // 오류: Type 'unknown' is not assignable to type 'string'.


// void
// void는 어떤 타입도 존재할 수 없음을 나타낸다. any의 반대 타입
function sayHello(): void {
  console.log("Hello Typescript!");
}


// never
// never는 절대 불가능한 타입을 나타낸다.
function error(message: string): never {
  throw new Error(message);
}
// 반환 타입이 never로 추론된다. function fail():never
function fail() {
  return error("Something failed");
}


// object
// 객체 user.name 와 같은 표기법으로 객체의 특정 프로퍼티에 접근하려고하면 에러 발생
// 타입스크립트의 object 타입은 단순 값이 객체임을 표현하는 것 외에는 아무런 정보도 제공하지 않는 타입이다.
let user: object = {
  name: "kang",
  age: 22
}
// 에러 발생 Property 'name' does not exist on type 'object'
// user.name

// 객체 리터럴 타입
let user2: {
  name: string;
  age: number;
} = {
  name: "kang",
  age: 22
}
user2.name


// type alias
// 타입 별칭
type User = {
  id: number;
  name: string,
  age: number
}

let member: User = {
  id: 1,
  name: "kang",
  age: 22
}


// 타입 단언은 컴파일러에게 타입이 뭔지 알고있다고 알려주는 방법이다.
let someValue: any = "this is a string";
let strLength: number = (<string>someValue).length;


/**
 * 타입 단언 => 값 as 타입
 * 타입을 단언할 때 값이 타입의 슈퍼타입이거나 서브타입이어야 한다.
 * as는 타입을 강제로 변환하는 문법이다. 
 * as는 타입 안전성을 위협할 수 있다. 잘못된 타입으로 단언할 경우 런타임 에러가 발생할 수 있기 때문에, as를 사용할 때는 확실한 이유가 있어야 한다.
 */
type Person = {
  name: string;
  age: number;
}

// 빈 객체를 할당하면 에러 발생
// let person: Person = {}

// 빈 객체가 Person 타입이라고 타입 단언
let person2: Person = {} as Person

// 초과 프로퍼티가 존재하지만 Person 타입을 단언해서 초과 프로퍼티 검사를 피함
let lee: Person = {
  name: "kang",
  age: 22,
  phone: "010-1111-1111"
} as Person

enum custom {A, B, C};

type userA = {
  val: custom,
  name: "kang"
}

function ex(result: userA): void {
  if (result.val == custom.A){
    console.log("A");
    return;
  }

  if (result.val == custom.B){
    console.log("A");
    return;
  }
}

let kang: userA = {
  val: custom.A,
  name: "kang"
}

ex(kang);


/**
 * 사용자 정의 타입가드
 * 사용자 정의 타입가드란 true 또는 false를 반환하는 함수를 이용해 우리의 생각대로 타입 가드를 만들 수 있도록 도와주는 타입스크립트 문법이다.
 */
type Dog = {
  name: string;
  isBark: boolean;
};

type Cat = {
  name: string;
  isScratch: boolean;
};

type Animal = Dog | Cat;

// is 문법은 타입스크립트에서 타입 좁히기를 위해 사용된다. 
// isDog 함수의 반환 타입은 animal is Dog인데, 이것은 animal이 Dog 타입일 때만 참이 된다는 것을 의미한다.
function isDog(animal: Animal): animal is Dog {
  return (animal as Dog).isBark !== undefined;
}

// Cat 타입인지 확인하는 타입가드
function isCat(animal: Animal): animal is Cat {
  return (animal as Cat).isScratch !== undefined;
}

function warning(animal: Animal) {
  if (isDog(animal)) {
    console.log(animal.isBark ? "짖습니다" : "안짖어요");
  } else {
    console.log(animal.isScratch ? "할큅니다" : "안할퀴어요");
  }
}