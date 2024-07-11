package book.object.chapter02.step02.discount;

import book.object.chapter02.step02.Screening;

public interface DiscountCondition {

    boolean isSatisfiedBy(Screening screening);
}
