package book.object.chapter12.step02;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Lecture lecture = new Lecture(70, "객체지향 프로그래밍", Arrays.asList(81, 95, 75, 60, 50));

        String evaluate = lecture.evaluate();
        System.out.println("evaluate = " + evaluate);

        System.out.println("===================================");

        List<Grade> grades = List.of(
                new Grade("A", 100, 90),
                new Grade("B", 89, 80),
                new Grade("C", 79, 70),
                new Grade("D", 69, 60),
                new Grade("F", 59, 0)
        );

        Lecture gradeLecture = new GradeLecture(
                70,
                "객체지향 프로그래밍",
                Arrays.asList(80, 95, 75, 60, 50, 100),
                grades
        );

        String evaluate1 = gradeLecture.evaluate();
        System.out.println("evaluate1 = " + evaluate1);

        System.out.println("===================================");

        Professor professor = new Professor(
                "다익스트라",
                new Lecture(70, "알고리즘", Arrays.asList(81, 95, 75, 60, 50)));

        String statistics = professor.compileStatistics();
        System.out.println("statistics = " + statistics);

        System.out.println("===================================");

        Professor professor2 = new Professor(
                "다익스트라",
                new GradeLecture(
                        70,
                        "알고리즘",
                        Arrays.asList(80, 95, 75, 60, 50, 100),
                        grades)
        );
        String statistics2 = professor2.compileStatistics();
        System.out.println("statistics2 = " + statistics2);
    }
}
