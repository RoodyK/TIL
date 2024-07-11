package book.object.chapter14.step01;

import book.object.chapter14.DateTimeInterval;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


// 통화
// 통화 기간에 대한 정보 전문가(기간을 일자 단위로 나누는 작업)
public class Call {
    private DateTimeInterval interval;

    public Call(LocalDateTime from, LocalDateTime to) {
        this.interval = DateTimeInterval.of(from, to);
    }

    public Duration getDuration() {
        return interval.duration();
    }

    public LocalDateTime getFrom() {
        return interval.getFrom();
    }

    public LocalDateTime getTo() {
        return interval.getTo();
    }

    public DateTimeInterval getInterval() {
        return interval;
    }

    public List<DateTimeInterval> splitByDay() {
        return interval.splitByDay();
    }
}
