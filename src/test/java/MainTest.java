import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void mainTest() {
        Main.main(new String[]{
                "some:\\//:file-1.txt",
                "some:\\//:file-2.txt"
        });

        Main.main(new String[0]);
    }

}