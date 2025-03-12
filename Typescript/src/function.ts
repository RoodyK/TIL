/**
 * 함수 타입
 * 타입스크립트의 함수도 자바스크립트와 마찬가지로 익명함수와 기명함수로 작성이 간으하다.
 * 함수의 매개변수가 어떤 타입인지, 어떤 타입의 값을 반환하는지 정의한다.
 */
function printMessage(name: string, age: number): void {
  console.log(`my name is ${name}, and ${age} years`);
}

// 반환 타입은 자동으로 추론되기 때문에 생략 가능하다.
function sum(a: number, b: number) {
  return a + b;
}

// 화살표 함수
const arrowSum = (a: number, b: number) => a + b;


// 매개변수 기본값 설정
function defaultParameterFunc(name: string = "lee", age: number = 22) {
  console.log(`my name is ${name}, and ${age} years`);
}


// 선택적 매개변수 설정
// 선택적 매개변수 타입은 자동으로 undefined와 유니온 된 타입으로 추론된다.
function chooseParam(name: string = "lee", age?: number) {
  console.log(`name : ${name}`);
  console.log(`age : ${age}`);
}

chooseParam("kang", 22);
chooseParam("kang");

// 선택적 매개변수는 반드시 필수 매개변수 뒤에 와야 한다.
// function chooseParam2(age?: number, name: string) {}


// 나머지 매개변수 설정
function remainParam(...args: Array<number>) {}


/**
 * 함수 타입 표현식
 * 함수 타입을 타입 별칭과 함께 별도로 정의할 수 있다.
 */
type Calculate = (a: number, b: number) => number;

const calcSum: Calculate = (a, b) => a + b;
const calcMul: Calculate = (a, b) => a * b;

/**
 * 호출 시그니처
 * 호출 시그니처(Call Signature)는 함수 타입 표현식과 동일하게 함수의 타입을 별도로 정의하는 방식이다.
 */
type Calculate2 = {
  (a: number, b: number): number;
}

const calcSum2: Calculate2 = (a, b) => a + b;
const calcMul2: Calculate2 = (a, b) => a * b;


/**
 * 함수 오버로딩
 * 타입스크립트에서 함수 오버로딩을 하려면 구현부 없이 선언부만 만들어둔 오버로드 시그니처 함수를 만들고 다음으로 구현부를 만들어줘야 한다.
 * 함수 오버로딩 시그니처는 여러 개 작성할 수 있어도, 구현부는 하나만 작성한다.
 * 구현부는 모든 오버로딩 시그니처에서 정의된 매개변수 타입과 일치해야 한다. 
 */
// 오버로딩 시그니처
function overloadFunc(a: number): void;
function overloadFunc(a: number, b: number): void;
function overloadFunc(a: string, b: number): void;

// 구현부 시그니처
function overloadFunc(a: number | string, b?: number) {
  // 구현부 작성
}