package book.object.chapter10.step01;

import book.object.chapter10.Money;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        Phone phone = new Phone(Money.wons(5L), Duration.ofSeconds(10));
        phone.call(new Call(
                LocalDateTime.of(2023, 1, 1, 12, 10, 0),
                LocalDateTime.of(2023, 1, 1, 12, 11, 0)
        ));
        phone.call(new Call(
                LocalDateTime.of(2023, 1, 2, 12, 10, 0),
                LocalDateTime.of(2023, 1, 2, 12, 11, 0)
        ));

        Money money = phone.calculateFee();// Money.wons(60)

        System.out.println(money);
    }
}
