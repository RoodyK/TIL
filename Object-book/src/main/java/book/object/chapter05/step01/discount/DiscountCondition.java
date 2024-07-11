package book.object.chapter05.step01.discount;

import book.object.chapter05.step01.Screening;

public interface DiscountCondition {
    boolean isSatisfiedBy(Screening screening);
}
