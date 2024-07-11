package book.object.chapter13.step01;

public class Bird {
    public void fly() {

    }

    // 인자로 전달된 모든 bird가 Penguin의 인스턴스가 아닐 경우에만 fly() 메시지를 전송한다.
    public void flyBird(Bird bird) {
        if (!(bird instanceof Penguin)) {
            bird.fly();
        }
    }
}
