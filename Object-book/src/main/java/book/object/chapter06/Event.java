package book.object.chapter06;

import java.time.Duration;
import java.time.LocalDateTime;

// 이벤트 : 특정 일자에 실제로 발생하는 사건
public class Event {
    private String subject;
    private LocalDateTime from;
    private Duration duration;

    public Event(String subject, LocalDateTime from, Duration duration) {
        this.subject = subject;
        this.from = from;
        this.duration = duration;
    }

    // 현재 이벤트가 반복 일정 조건을 만족하는지 검사
    // 명령과 쿼리가 하나의 로직에 존재
//    public boolean isSatisfied(RecurringSchedule schedule) {
//        if (from.getDayOfWeek() != schedule.getDayOfWeek() ||
//                !from.toLocalTime().equals(schedule.getFrom()) ||
//                !duration.equals(schedule.getDuration())) {
//            reschedule(schedule); // 명령 - 데이터 변경이 일어남
//            return false; // 조건 판단 true, false - 쿼리
//        }
//
//        return true; // 조건 판단 true, false - 쿼리
//    }

//    private void reschedule(RecurringSchedule schedule) {
//        from = LocalDateTime.of(from.toLocalDate().plusDays(dayDistance(schedule)), schedule.getFrom());
//        duration = schedule.getDuration();
//    }

     // 명령 - 쿼리 분리
    // 명령
    public boolean isSatisfied(RecurringSchedule schedule) {
        if (from.getDayOfWeek() != schedule.getDayOfWeek() ||
                !from.toLocalTime().equals(schedule.getFrom()) ||
                !duration.equals(schedule.getDuration())) {
            return false;
        }

        return true;
    }

    // 쿼리 private -> public 으로 가시성 변경
    public void reschedule(RecurringSchedule schedule) {
        from = LocalDateTime.of(from.toLocalDate().plusDays(dayDistance(schedule)), schedule.getFrom());
        duration = schedule.getDuration();
    }

    private long dayDistance(RecurringSchedule schedule) {
        return schedule.getDayOfWeek().getValue() - from.getDayOfWeek().getValue();
    }
}
