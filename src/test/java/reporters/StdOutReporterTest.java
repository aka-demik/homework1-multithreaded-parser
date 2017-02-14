package reporters;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StdOutReporterTest {
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private static PrintStream sout = System.out;
    private static PrintStream serr = System.err;

    @BeforeAll
    static void setUpAll() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterAll
    static void tearDownAll() {
        System.setOut(sout);
        System.setErr(serr);
    }

    @AfterEach
    void tearDown() {
        outContent.reset();
        errContent.reset();
    }

    @Test
    void reportState() {
        StdOutReporter reporter = new StdOutReporter();

        reporter.reportState(100, false);

        assertEquals("Current state: 100",
                outContent.toString().trim());
        assertEquals(0, errContent.size());
    }

    @Test
    void reportResult() {
        StdOutReporter reporter = new StdOutReporter();

        reporter.reportState(150, true);

        assertEquals("Result: 150",
                outContent.toString().trim());
        assertEquals(0, errContent.size());
    }
}