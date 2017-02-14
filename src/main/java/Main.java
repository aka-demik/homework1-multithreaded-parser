import executor.Executor;
import org.apache.log4j.Logger;
import processors.ThreadSafeSum;
import reporters.StdOutReporter;

public class Main {
    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        logger.trace("application started");

        if (args.length == 0) {
            System.out.println("Usage: lab1var3 resource [, resource...]");
            logger.trace("nothing to do, exit.");
            return;
        }

        logger.trace("initialising execution stack");
        StdOutReporter stdOutReporter = new StdOutReporter();
        ThreadSafeSum threadSafeSum = new ThreadSafeSum(stdOutReporter);
        Executor executor = new Executor(args, threadSafeSum);

        logger.trace("start processing");
        try {
            if (executor.run()) {
                stdOutReporter.reportState(threadSafeSum.getValue(), true);
            }
        } catch (InterruptedException ex) {
            logger.error("working thread interrupted", ex);
        }
    }

}
