package book.object.chapter12.step01;

public class Grade {
    private String name;
    private int upper;
    private int lower;

    public Grade(String name, int upper, int lower) {
        this.name = name;
        this.upper = upper;
        this.lower = lower;
    }

    public String getName() {
        return name;
    }

    public boolean isName(String name) {
        return this.name.equals(name);
    }

    // 수강생의 성적이 등급에 포함되는가
    public boolean include(int score) {
        return score >= lower && score <= upper;
    }
}
