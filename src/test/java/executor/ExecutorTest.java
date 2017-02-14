package executor;

import org.junit.jupiter.api.Test;
import processors.ThreadSafeSum;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecutorTest {
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