package book.object.chapter11.step03;

import book.object.chapter11.Money;

// 기본 정책, 부가 정책을 포괄
public interface RatePolicy {

    Money calculateFee(Phone phone);
}
