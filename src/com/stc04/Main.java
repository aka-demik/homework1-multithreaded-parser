package com.stc04;

import com.stc04.parser.Parser;
import com.stc04.processors.ThreadSafeSum;
import com.stc04.reporters.StdOutReporter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: lab1var3 resource [, resource...]");
            return;
        }

        StdOutReporter stdOutReporter = new StdOutReporter();
        ThreadSafeSum threadSafeSum = new ThreadSafeSum(stdOutReporter);

        for (String resource: args) {
            try (FileReader fr = new FileReader(resource)) {
                Parser p = new Parser(fr, threadSafeSum);
                p.run();
            }
            catch (NumberFormatException ex) {
                System.err.println(ex.getMessage() + ": invalid input data.");
                return;
            }
            catch (FileNotFoundException e) {
                System.err.println("Resource not found: '" + resource + "'. " + e.getMessage());
                return;
            }
            catch (IOException e) {
                System.err.println("Resource read error: '" + resource + "'. " + e.getMessage());
                return;
            }
        }
        stdOutReporter.reportState(threadSafeSum.getValue(), true);
    }
}
