package executor;

import org.apache.log4j.Logger;
import parser.Parser;
import processors.DataProcessor;
import processors.StateProcessor;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (String resource : resources) {
            pool.submit(new Parser(resource, dataProcessor, this));
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
        logger.trace("all parse threads finished");

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
