package book.object.chapter02.step02;

import book.object.chapter02.step02.discount.AmountDiscountPolicy;
import book.object.chapter02.step02.discount.NoneDiscountPolicy;
import book.object.chapter02.step02.discount.PercentDiscountPolicy;
import book.object.chapter02.step02.discount.PeriodCondition;
import book.object.chapter02.step02.discount.SequenceCondition;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        Movie avatar = new Movie(
                "아바타",
                Duration.ofMinutes(120),
                Money.wons(10000),
                new AmountDiscountPolicy(Money.wons(800))
        );
        avatar.changeDiscountPolicy(new PercentDiscountPolicy(
                0.1,
                new PeriodCondition(DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(16, 59))));

        Movie titanic = new Movie(
                "타이타닉",
                Duration.ofMinutes(180),
                Money.wons(11000),
                new PercentDiscountPolicy(0.1,
                        new PeriodCondition(DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(16, 59)),
                        new SequenceCondition(2),
                        new PeriodCondition(DayOfWeek.THURSDAY, LocalTime.of(10, 0), LocalTime.of(13, 59))
                )
        );

        Movie starWars = new Movie(
                "스타워즈",
                Duration.ofMinutes(210),
                Money.wons(10000),
                new NoneDiscountPolicy()
        );
    }
}
