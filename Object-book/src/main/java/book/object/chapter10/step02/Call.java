package book.object.chapter10.step02;

import java.time.Duration;
import java.time.LocalDateTime;

public class Call {
    private LocalDateTime from; // 통화 시작 시간
    private LocalDateTime to; // 통화 종료 시간

    public Call(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    public Duration getDuration() {
        return Duration.between(from, to);
    }

    public LocalDateTime getFrom() {
        return from;
    }
}
