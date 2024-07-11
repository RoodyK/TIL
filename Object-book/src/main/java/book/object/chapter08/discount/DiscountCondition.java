package book.object.chapter08.discount;

import book.object.chapter08.Screening;

public interface DiscountCondition {
    boolean isSatisfiedBy(Screening screening);
}
