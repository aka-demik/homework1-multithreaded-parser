package reporters;

import org.apache.log4j.Logger;

/**
 * Реализация интерфейса пользователя, использующая {@link System#out} для отображения данных.
 */
public class StdOutReporter implements DataProcessReporter {
    private static Logger logger = Logger.getLogger(StdOutReporter.class);

    @Override
    public void reportState(long newValue, boolean done) {
        logger.info("Current state: " + newValue);
        if (done)
            System.out.println("Result: " + newValue);
    }
}
