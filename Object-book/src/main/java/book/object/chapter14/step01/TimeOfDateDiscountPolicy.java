package book.object.chapter14.step01;

import book.object.chapter14.DateTimeInterval;
import book.object.chapter14.Money;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

// 시간대별 분할 작업의 정보 전문가(일자별로 통화 기간을 분리)
public class TimeOfDateDiscountPolicy extends BasicRatePolicy {
    private List<LocalTime> starts = new ArrayList<>(); // 시작 시간
    private List<LocalTime> ends = new ArrayList<>(); // 종료 시간
    private List<Duration> durations = new ArrayList<>(); // 단위 시간
    private List<Money> amounts = new ArrayList<>(); // 단위 요금


    @Override
    protected Money calculateCallFee(Call call) {
        Money result = Money.ZERO;
        for (DateTimeInterval interval : call.splitByDay()) {
            for (int loop = 0; loop < starts.size(); loop++) {
                result.plus(amounts.get(loop).times(
                        Duration.between(
                                from(interval, starts.get(loop)),
                                to(interval, ends.get(loop))
                        ).getSeconds() / durations.get(loop).getSeconds()
                ));
            }
        }

        return result;
    }

    private LocalTime from(DateTimeInterval interval, LocalTime from) {
        return interval.getFrom().toLocalTime().isBefore(from)
                ? from
                : interval.getFrom().toLocalTime();
    }

    private LocalTime to(DateTimeInterval interval, LocalTime to) {
        return interval.getFrom().toLocalTime().isAfter(to)
                ? to
                : interval.getFrom().toLocalTime();
    }
}
