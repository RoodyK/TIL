package book.object.chapter02.step01.discount;

import book.object.chapter02.step01.Screening;

public interface DiscountCondition {

    boolean isSatisfiedBy(Screening screening);
}
