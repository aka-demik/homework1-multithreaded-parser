import executor.Executor;
import processors.ThreadSafeSum;
import reporters.StdOutReporter;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: lab1var3 resource [, resource...]");
            return;
        }

        StdOutReporter stdOutReporter = new StdOutReporter();
        ThreadSafeSum threadSafeSum = new ThreadSafeSum(stdOutReporter);
        Executor executor = new Executor(args, threadSafeSum);

        try {
            if (executor.run()) {
                stdOutReporter.reportState(threadSafeSum.getValue(), true);
            }
        } catch (InterruptedException e) {
            System.err.println("Working thread interrupted.");
            e.printStackTrace();
        }
    }

}
