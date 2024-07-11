package book.object.chapter11.step02;

import book.object.chapter11.Money;

import java.time.Duration;

// 일반 요금제와 기본 요금 할인 정책을 조합
public class RateDiscountableRegularPhone extends RegularPhone {

    private Money discountAmount;

    public RateDiscountableRegularPhone(Money amount, Duration seconds, Money discountAmount) {
        super(amount, seconds);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee.minus(discountAmount);
    }
}
