package parser;

import processors.StateProcessor;
import processors.ThreadSafeSum;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {
    private static ThreadSafeSum getSum(String testData) throws Exception {
        ThreadSafeSum safeSum = new ThreadSafeSum();
        StateProcessorMock stateProcessor = new StateProcessorMock();
        Reader reader = new StringReader(testData);
        Parser parser = new Parser(reader, safeSum, stateProcessor);
        parser.run();
        stateProcessor.check();
        return safeSum;
    }

    private static StateProcessorMock getState(String testData) {
        ThreadSafeSum safeSum = new ThreadSafeSum();
        StateProcessorMock stateProcessor = new StateProcessorMock();
        Reader reader = new StringReader(testData);
        Parser parser = new Parser(reader, safeSum, stateProcessor);

        parser.run();
        return stateProcessor;
    }

    @Test
    void simple() throws Exception {
        final String testData = "2 4";
        assertEquals(6, getSum(testData).getValue());
    }

    @Test
    void empty() throws Exception {
        final String testData = "";
        assertEquals(0, getSum(testData).getValue());
    }

    @Test
    void whiteSpaceOnly() throws Exception {
        final String testData = "\r\n \r \n \t \t\t \r\r\n\r";
        assertEquals(0, getSum(testData).getValue());
    }

    @Test
    void multiline() throws Exception {
        final String testData = "2 \r\n 2 \r 2 \n 2 \n\r 2 \t2\t2\n2\r2";
        assertEquals(18, getSum(testData).getValue());
    }

    @Test
    void unicodeMinus() throws Exception {
        final String testData = "1 123 -123  —2 –2 ‒2 −2 8 124 -124";
        assertEquals(132, getSum(testData).getValue());
    }

    @Test
    void malformedWithText() throws Exception {
        final String testData = "2 qwe";
        assertEquals(NumberFormatException.class,
                getState(testData).getConsumedException().getClass());
    }

    @Test
    void malformedWithMinus() throws Exception {
        final String testData = "77\r\n2- 3";
        assertEquals(NumberFormatException.class,
                getState(testData).getConsumedException().getClass());
    }

    @Test
    void malformedDangMinus() throws Exception {
        final String testData = "2\r\n2 - 3";
        assertEquals(NumberFormatException.class,
                getState(testData).getConsumedException().getClass());
    }

    @Test
    void malformedFloat1() throws Exception {
        final String testData = "45.5";
        assertEquals(NumberFormatException.class,
                getState(testData).getConsumedException().getClass());
    }

    @Test
    void malformedFloat2() throws Exception {
        final String testData = "3,14";
        assertEquals(NumberFormatException.class,
                getState(testData).getConsumedException().getClass());
    }
}

class StateProcessorMock implements StateProcessor {
    private boolean active = true;
    private Exception consumedException = null;

    @Override
    public void consumeException(Exception ex) {
        consumedException = ex;
        active = false;
    }

    Exception getConsumedException() {
        return consumedException;
    }

    @Override
    public boolean getActive() {
        return active;
    }

    void check() throws Exception {
        if (consumedException != null)
            throw consumedException;
    }
}