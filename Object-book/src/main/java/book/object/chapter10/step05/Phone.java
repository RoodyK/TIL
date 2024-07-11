package book.object.chapter10.step05;

import book.object.chapter10.Money;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

// 강하게 결합된 Phone과 NightlyDiscountPhone
public class Phone {
    private Money amount; // 단위 요금
    private Duration seconds; // 단위 시간
    private List<Call> calls = new ArrayList<>(); // 전체 통화 목록
    private double taxRate; // 세율

    public Phone(Money amount, Duration seconds, double taxRate) {
        this.amount = amount;
        this.seconds = seconds;
        this.taxRate = taxRate;
    }

    public void call(Call call) {
        calls.add(call);
    }

    public List<Call> getCalls() {
        return calls;
    }

    public Money getAmount() {
        return amount;
    }

    public Duration getSeconds() {
        return seconds;
    }

    public Money calculateFee() {
        Money result = Money.ZERO;

        for(Call call : calls) {
            result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
        }

        return result.plus(result.times(taxRate));
    }

    public double getTaxRate() {
        return taxRate;
    }
}
