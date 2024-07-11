package book.object.chapter14.step02;

import book.object.chapter14.Money;

// 기본 정책, 부가 정책을 포괄
public interface RatePolicy {

    Money calculateFee(Phone phone);
}
