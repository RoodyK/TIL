package book.object.chapter11.step02;

import book.object.chapter11.Money;

import java.time.Duration;

// 표준 요금제에 기본 요금 할인 정책을 먼저 적용한 후 세금을 나중에 부과하고 싶다면
public class RateDiscountableAndTaxableRegularPhone extends RateDiscountableRegularPhone {
    private double taxRate;

    public RateDiscountableAndTaxableRegularPhone(Money amount, Duration seconds, Money discountAmount, double taxRate) {
        super(amount, seconds, discountAmount);
        this.taxRate = taxRate;
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return super.afterCalculated(fee).plus(fee.times(taxRate));
    }
}
