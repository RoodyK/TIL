package book.object.chapter02.step01.discount;

import book.object.chapter02.step01.Money;
import book.object.chapter02.step01.Screening;

public class NoneDiscountPolicy extends DiscountPolicy {

    @Override
    protected Money getDiscountAmount(Screening screening) {
        return Money.ZERO;
    }
}
