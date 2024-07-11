package book.object.chapter11.step02;

import book.object.chapter11.Money;

import java.time.Duration;

// Phone 다시 살펴보기
public class RegularPhone extends Phone {
    private Money amount; // 단위 요금
    private Duration seconds; // 단위 시간

    public RegularPhone(Money amount, Duration seconds) {
        this.amount = amount;
        this.seconds = seconds;
    }

    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }

//    @Override
//    protected Money afterCalculated(Money fee) {
//        return fee;
//    }
}
