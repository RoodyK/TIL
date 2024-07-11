package book.object.chapter08.discount;

import book.object.chapter08.Money;
import book.object.chapter08.Screening;

public class NoneDiscountPolicy extends DiscountPolicy {
    @Override
    protected Money getDiscountAmount(Screening screening) {
        return Money.ZERO;
    }
}
