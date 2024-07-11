package book.object.chapter06;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {
        // 목요알
        Event meeting = new Event(
                "회의",
                LocalDateTime.of(2023, 7, 13, 10, 30),
                Duration.ofMinutes(30)
        );

        // 수요일
        RecurringSchedule schedule = new RecurringSchedule(
                "회의",
                DayOfWeek.WEDNESDAY,
                LocalTime.of(10, 30),
                Duration.ofMinutes(30)
        );


        System.out.println(meeting.isSatisfied(schedule));
        System.out.println(meeting.isSatisfied(schedule));
    }
}
