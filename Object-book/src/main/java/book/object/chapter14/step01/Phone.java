package book.object.chapter14.step01;

import book.object.chapter14.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Phone {

    private RatePolicy ratePolicy;
    private List<Call> calls = new ArrayList<>(); // 전체 통화 목록

    public Phone(RatePolicy ratePolicy) {
        this.ratePolicy = ratePolicy;
    }

    public void call(Call call) {
        calls.add(call);
    }

    public List<Call> getCalls() {
        return Collections.unmodifiableList(calls);
    }

    public Money calculateFee() {
        return ratePolicy.calculateFee(this);
    }

    // protected abstract Money calculateCallFee(Call call);

}
