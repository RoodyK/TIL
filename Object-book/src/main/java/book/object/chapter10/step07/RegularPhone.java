package book.object.chapter10.step07;

import book.object.chapter10.Money;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

// Phone 다시 살펴보기
public class RegularPhone extends Phone {
    private Money amount; // 단위 요금
    private Duration seconds; // 단위 시간
    private List<Call> calls = new ArrayList<>(); // 전체 통화 목록

    public RegularPhone(Money amount, Duration seconds, double taxRate) {
        super(taxRate);
        this.amount = amount;
        this.seconds = seconds;
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

    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }
}
