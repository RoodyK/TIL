package book.object.chapter02.step02.discount;

import book.object.chapter02.step02.Money;
import book.object.chapter02.step02.Screening;

public class NoneDiscountPolicy implements DiscountPolicy {

    @Override
    public Money calculateDiscountAmount(Screening screening) {
        return Money.ZERO;
    }
}
