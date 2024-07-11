package book.object.chapter05.step01.discount;

import book.object.chapter05.step01.Money;
import book.object.chapter05.step01.Movie;

import java.time.Duration;

// 금액 할인 정책
public class AmountDiscountMovie extends Movie {

    private Money discountAmount;

    public AmountDiscountMovie(
            String title, Duration runningTime, Money fee, Money discountAmount, DiscountCondition... discountConditions) {
        super(title, runningTime, fee, discountConditions);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money calculateDiscountAmount() {
        return discountAmount;
    }
}
