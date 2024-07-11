package book.object.chapter11.step03;

import book.object.chapter11.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Phone {

    private RatePolicy ratePolity;
    private List<Call> calls = new ArrayList<>(); // 전체 통화 목록

    public Phone(RatePolicy ratePolity) {
        this.ratePolity = ratePolity;
    }

    public Money calculateFee() {
        return ratePolity.calculateFee(this);
    }

    public void call(Call call) {
        calls.add(call);
    }

    protected Money afterCalculated(Money fee) {
        return fee;
    }

    protected abstract Money calculateCallFee(Call call);

    public List<Call> getCalls() {
        return Collections.unmodifiableList(calls);
    }
}
