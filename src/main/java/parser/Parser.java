package parser;

import org.apache.log4j.Logger;
import processors.DataProcessor;
import processors.StateProcessor;

import java.io.*;
import java.net.URL;

import static java.lang.String.format;

/**
 * Парсер текста в формате, описанном в задании 1 вариант 3.
 * <p>
 * <p> Формат входных данных.
 * <p> Любая последоватеьность пробельных символов трактуется как разделитель значений.
 * Значение может начинаться со знака минус, дефис, длинное, среднее или цифровое тире.
 * При наличии любого из этих символов, значение трактуется как отрицательное.
 * Далее должна следовать последовательность цифр.
 */
public class Parser implements Runnable {

    private static Logger logger = Logger.getLogger(Parser.class);
    private final String resource;
    private final DataProcessor dataProcessor;
    private final StateProcessor stateProcessor;
    private BufferedReader bufferedReader;

    /**
     * Создаёт экземпляр парсера.
     *
     * @param reader         источник строк для парсера.
     * @param dataProcessor  потребитель данных парсинга. Ему будет передан каждый найденый элемент
     *                       путём вызова {@link DataProcessor#consumeValue}.
     * @param stateProcessor контролёр процесса обработки.
     * @throws IllegalArgumentException если reader или dataProcessor равны null.
     */
    public Parser(final Reader reader, DataProcessor dataProcessor, StateProcessor stateProcessor)
            throws IllegalArgumentException {
        logger.trace(format("create for reader %s, %s, %s", reader, dataProcessor, stateProcessor));

        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }

        if (dataProcessor == null) {
            throw new IllegalArgumentException("Data processor must not be null");
        }

        if (stateProcessor == null) {
            throw new IllegalArgumentException("State processor must not be null");
        }

        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }

        this.resource = null;
        this.dataProcessor = dataProcessor;
        this.stateProcessor = stateProcessor;
    }

    public Parser(String resource, DataProcessor dataProcessor, StateProcessor stateProcessor)
            throws IllegalArgumentException {
        logger.trace(format("create for resource '%s', %s, %s", resource, dataProcessor, stateProcessor));

        if (resource == null || resource.isEmpty()) {
            throw new IllegalArgumentException("Resource must not be null");
        }

        if (dataProcessor == null) {
            throw new IllegalArgumentException("Data processor must not be null");
        }

        if (stateProcessor == null) {
            throw new IllegalArgumentException("State processor must not be null");
        }

        this.resource = resource;
        this.bufferedReader = null;
        this.dataProcessor = dataProcessor;
        this.stateProcessor = stateProcessor;
    }

    /**
     * Выполняет обработку входных данных.
     */
    public void run() {
        logger.debug(format("data parsing started for '%s'",
                resource != null ? resource : bufferedReader));

        String nextLine;
        try (BufferedReader localReader = getReader()) {
            while ((nextLine = localReader.readLine()) != null) {
                if (!stateProcessor.getActive())
                    break;

                for (String item : nextLine.split("\\s")) {
                    if (item.length() > 0) {
                        switch (item.charAt(0)) {
                            case '\u2012':
                            case '\u2013':
                            case '\u2014':
                            case '\u2212':
                                item = "-" + item.substring(1);
                                break;
                        }

                        long l = Long.parseLong(item);
                        if (l > 0 && (l & 1) == 0) {
                            dataProcessor.consumeValue(l);
                        }
                    }
                }
            }

            logger.debug(format("data parsing %s for '%s'",
                    stateProcessor.getActive() ? "done" : "stoped",
                    resource != null ? resource : bufferedReader));

        } catch (Exception ex) {
            logger.error(format("data parsing error in '%s'",
                    resource != null ? resource : bufferedReader), ex);
            stateProcessor.consumeException(ex);
        }
    }

    private BufferedReader getReader() throws IOException {
        if (bufferedReader != null) {
            return bufferedReader;
        }

        if (resource == null) {
            throw new IllegalStateException("Resource must not be null");
        }

        FileReader fr = null;
        InputStream is = null;
        try {
            if (resource.startsWith("http://") || resource.startsWith("https://")) {
                is = new URL(resource).openStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
            } else {
                fr = new FileReader(resource);
                bufferedReader = new BufferedReader(fr);
            }
            return bufferedReader;
        } catch (Exception ex) {
            try (InputStream tmp = is) {
                if (fr != null)
                    fr.close();
            }
            throw ex;
        }
    }
}
