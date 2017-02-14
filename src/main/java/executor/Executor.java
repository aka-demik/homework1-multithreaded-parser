package executor;

import org.apache.log4j.Logger;
import parser.Parser;
import processors.DataProcessor;
import processors.StateProcessor;

import java.util.Arrays;

import static java.lang.String.format;

/**
 * Обработчик группы ресурсов.
 * <p>
 * Порождает потоки для обработки, контролирует выполнение и остановку.
 */
public class Executor implements StateProcessor {

    private static Logger logger = Logger.getLogger(Executor.class);
    private final String[] resources;
    private final DataProcessor dataProcessor;
    private boolean active = true;

    /**
     * Создаёт обработчик группы ресурсов.
     *
     * @param resources     список ресурсов для обработки.
     * @param dataProcessor процессор для распарсенных данных.
     */
    public Executor(String resources[], DataProcessor dataProcessor) {
        logger.trace(format("create with %s, %s", dataProcessor, Arrays.toString(resources)));

        if (resources == null || resources.length == 0) {
            throw new IllegalArgumentException("Resources is empty");
        }

        if (dataProcessor == null) {
            throw new IllegalArgumentException("Data processor must not be null");
        }

        this.resources = resources;
        this.dataProcessor = dataProcessor;
    }

    /**
     * Запускает потоки обработки ресурсов.
     *
     * @return true если обработка успешно завершена. Иначе false.
     */
    public boolean run() throws InterruptedException {
        logger.trace("creating parse threads");

        Thread[] threads = new Thread[resources.length];

        try {
            for (int i = 0; i < resources.length; i++) {
                threads[i] = new Thread(new Parser(resources[i], dataProcessor, this));
                threads[i].start();
            }
        } catch (Exception ex) {
            logger.error("thread creation error", ex);
            consumeException(ex);
            return false;
        } finally {
            logger.trace("waiting for parse threads");
            for (Thread thread : threads) {
                if (thread != null) {
                    thread.join();
                }
            }
            logger.trace("all parse threads finished");
        }

        return active;
    }

    @Override
    public void consumeException(Exception ex) {
        active = false;
    }

    @Override
    public boolean getActive() {
        return active;
    }
}
