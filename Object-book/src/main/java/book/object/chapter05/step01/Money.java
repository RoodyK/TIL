package book.object.chapter05.step01;

import java.math.BigDecimal;

public class Money {

    public static final Money ZERO = wons(0);

    private final BigDecimal amount;

    Money(BigDecimal amount) {
        this.amount = amount;
    }

    public static Money wons(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    };

    public Money times(int audienceCount) {
        return null;
    }

    public Money minus(Object calculateDiscountAmount) {
        return null;
    }

    public Money times(double discountPercent) {
        return null;
    }
}
