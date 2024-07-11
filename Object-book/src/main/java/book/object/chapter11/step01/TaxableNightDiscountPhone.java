package book.object.chapter11.step01;

import book.object.chapter11.Money;

import java.time.Duration;

public class TaxableNightDiscountPhone extends NightlyDiscountPhone {
    private double taxRate;

    public TaxableNightDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds, double taxRate) {
        super(nightlyAmount, regularAmount, seconds);
        this.taxRate = taxRate;
    }

    @Override
    public Money calculateFee() {
        Money fee = super.calculateFee();
        return fee.plus(fee.times(taxRate));
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee.plus(fee.times(taxRate));
    }
}
