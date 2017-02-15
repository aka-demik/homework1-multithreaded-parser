package executor;

import org.junit.jupiter.api.Test;
import processors.ThreadSafeSum;

import static org.junit.jupiter.api.Assertions.*;

class ExecutorTest {
    @Test
    void constructorNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Executor(null, new ThreadSafeSum(null));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Executor(new String[0], new ThreadSafeSum(null));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Executor(new String[]{"data.txt"}, null);
        });
    }

    @Test
    void run() throws InterruptedException {
        Executor executor = new Executor(
                new String[]{
                        "http://localhost:66535/file.txt",
                        "some:\\//:file-2.txt",
                        "some:\\//:file-3.txt",
                        "some:\\//:file-4.txt",
                        "some:\\//:file-5.txt",
                        "some:\\//:file-6.txt",
                        "some:\\//:file-7.txt",
                },
                new ThreadSafeSum(null));
        executor.run();
        assertFalse(executor.getActive());
    }

    @Test
    void activeByStart() {
        String[] resources = {"C:\1.txt"};
        ThreadSafeSum sum = new ThreadSafeSum(null);
        Executor executor = new Executor(resources, sum);

        assertEquals(true, executor.getActive());
    }

    @Test
    void stopOnError() {
        String[] resources = {"C:\1.txt"};
        ThreadSafeSum sum = new ThreadSafeSum(null);
        Executor executor = new Executor(resources, sum);

        executor.consumeException(new Exception("Test exception"));

        assertEquals(false, executor.getActive());
    }

}