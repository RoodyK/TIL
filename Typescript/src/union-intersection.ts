/**
 * union type
 * any 타입을 사용하면 어떤 타입도 받을 수 있지만, number나 string만 받고자할 때 any는 그 외의 타입도 받게된다.
 * 이 때 union 타입으로 교차타입을 구성할 수 있다.
 */
let unionValue: number | string;
unionValue = 22;
unionValue = "hello";

function exFunc(value: string, result: string | number) {
  console.log(`value = ${value}, result type = ${typeof result}`);
}

// 에러 발생 Argument of type 'boolean' is not assignable to parameter of type 'string | number'.
// exFunc("", true);

exFunc("hello", 22);


// 유니온 타입으로 배열 정의
let unionArr: Array<string | number | boolean> = ["union", 22, false];
let unionArr2: (string | number | boolean)[] = ["union", 22, true];


// 공통 필드를 갖는 유니온 
// 유니온에 있는 모든 타입에 공통인 멤버에만 접근할 수 있다.
interface Bird {
  fly(): void;
  layEggs(): void;
}

interface Fish {
  swim(): void;
  layEggs(): void;
}

declare function getSmallPet(): Fish | Bird;

let pet = getSmallPet();
pet.layEggs();

// 접근 불가. swin()은 공통이 아니다.
// pet.swim();


/**
 * 교차 타입
 * 교차 타입은 여러 타입을 하나로 결합하고, 기존 타입을 합쳐 필요한 기능을 모두 가지는 단일 타입을 얻을 수 있다.
 */
interface ViewResolver {
  success: boolean;
  error?: { message: string };
}

interface ViewData {
  data: {
    code: number
    result: any
  }
}

type ViewResponse = ViewResolver & ViewData;

const handleViewResponse = (response: ViewResponse) => {
  if (response.error) {
    console.error(response.error.message);
  }

  console.log(response.data);
}