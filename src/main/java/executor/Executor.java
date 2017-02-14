package executor;

import parser.Parser;
import processors.DataProcessor;
import processors.StateProcessor;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Обработчик группы ресурсов.
 * <p>
 * Порождает потоки для обработки, контролирует выполнение и остановку.
 */
public class Executor implements StateProcessor {

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
        Thread[] threads = new Thread[resources.length];

        try {
            for (int i = 0; i < resources.length; i++) {
                threads[i] = new Thread(new Parser(resources[i], dataProcessor, this));
                threads[i].start();
            }
        } catch (Exception ex) {
            consumeException(ex);
            return false;
        } finally {
            for (Thread thread : threads) {
                if (thread != null) {
                    thread.join();
                }
            }
        }

        return active;
    }

    @Override
    public void consumeException(Exception ex) {
        active = false;
        if (ex instanceof NumberFormatException) {
            System.err.println(ex.getMessage() + ": invalid input data.");
        } else if (ex instanceof FileNotFoundException) {
            System.err.println("Resource not found: " + ex.getMessage());
        } else if (ex instanceof IOException) {
            System.err.println("Resource read error: " + ex.getMessage());
        } else {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean getActive() {
        return active;
    }
}
