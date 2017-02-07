package com.stc04.reporters;

public class StdOutReporter implements DataProcessReporter {
    @Override
    public void reportState(long l, boolean done) {
        System.out.println((done ? "Result: " : "Current state: ") + l);
    }
}
