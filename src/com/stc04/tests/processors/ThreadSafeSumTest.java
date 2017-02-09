package com.stc04.tests.processors;

import com.stc04.processors.ThreadSafeSum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ThreadSafeSumTest {
    @Test
    void defaultValue() {
        ThreadSafeSum threadSafeSum = new ThreadSafeSum(null);

        long expected = 0;
        long actual = threadSafeSum.getValue();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void consumeValue() {
        ThreadSafeSum threadSafeSum = new ThreadSafeSum(null);

        threadSafeSum.consumeValue(50);
        threadSafeSum.consumeValue(100);
        threadSafeSum.consumeValue(-50);

        long expected = 100;
        long actual = threadSafeSum.getValue();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void consumeValueMultiThread() throws InterruptedException {
        ThreadSafeSum threadSafeSum = new ThreadSafeSum(null);
        Thread[] threads = new Thread[100];
        Runnable test = new Runnable() {
            @Override
            public void run() {
                threadSafeSum.consumeValue(10);
            }
        };

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(test);
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        long expected = 1000;
        long actual = threadSafeSum.getValue();

        Assertions.assertEquals(expected, actual);
    }

}