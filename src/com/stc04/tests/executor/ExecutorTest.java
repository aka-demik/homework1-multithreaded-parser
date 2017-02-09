package com.stc04.tests.executor;

import com.stc04.executor.Executor;
import com.stc04.processors.ThreadSafeSum;
import org.junit.jupiter.api.Test;

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