package book.object.chapter02.step02.discount;

import book.object.chapter02.step02.Money;
import book.object.chapter02.step02.Screening;

public interface DiscountPolicy {

    Money calculateDiscountAmount(Screening screening);
}
