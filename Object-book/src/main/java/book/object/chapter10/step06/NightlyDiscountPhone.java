package book.object.chapter10.step06;

import book.object.chapter10.Money;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class NightlyDiscountPhone extends Phone {
    private static final int LATE_NIGHT_HOUR = 22; // 기준 시간

    private Money nightlyAmount; // 밤 10시 이후 부과될 금액
    private Money regularAmount; // 밤 10시 이전 부과될 금액
    private Duration seconds; // 통화 시간
    private List<book.object.chapter10.step02.Call> calls = new ArrayList<>(); // 통화 목록

    public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds) {
        this.nightlyAmount = nightlyAmount;
        this.regularAmount = regularAmount;
        this.seconds = seconds;
    }

    @Override
    protected Money calculateCallFee(Call call) {
        if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
            return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        } else {
            return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }
}
