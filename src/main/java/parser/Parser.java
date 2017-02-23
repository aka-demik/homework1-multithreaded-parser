package parser;

import org.apache.log4j.Logger;
import processors.DataProcessor;
import processors.StateProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

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
    private Stream<String> stream;

    /**
     * Создаёт экземпляр парсера.
     *
     * @param stream         источник строк для парсера.
     * @param dataProcessor  потребитель данных парсинга. Ему будет передан каждый найденый элемент
     *                       путём вызова {@link DataProcessor#consumeValue}.
     * @param stateProcessor контролёр процесса обработки.
     * @throws IllegalArgumentException если reader или dataProcessor равны null.
     */
    public Parser(final Stream<String> stream, DataProcessor dataProcessor, StateProcessor stateProcessor)
            throws IllegalArgumentException {
        logger.trace(format("create for reader %s, %s, %s", stream, dataProcessor, stateProcessor));

        if (stream == null) {
            throw new IllegalArgumentException("stream must not be null");
        }

        if (dataProcessor == null) {
            throw new IllegalArgumentException("Data processor must not be null");
        }

        if (stateProcessor == null) {
            throw new IllegalArgumentException("State processor must not be null");
        }

        this.stream = stream;
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
        this.stream = null;
        this.dataProcessor = dataProcessor;
        this.stateProcessor = stateProcessor;
    }

    /**
     * Выполняет обработку входных данных.
     */
    public void run() {
        logger.debug(format("data parsing started for '%s'",
                resource != null ? resource : stream));

        String nextLine;
        try {
            stream = getReader();
            stream.parallel().forEach(this::parseLine);

            logger.debug(format("data parsing %s for '%s'",
                    stateProcessor.getActive() ? "done" : "stoped",
                    resource != null ? resource : stream));

        } catch (Exception ex) {
            logger.error(format("data parsing error in '%s'",
                    resource != null ? resource : stream), ex);
            stateProcessor.consumeException(ex);
        }
    }

    private void parseLine(String nextLine) {
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

    private Stream<String> getReader() throws IOException {
        if (stream != null) {
            return stream;
        }

        if (resource == null) {
            throw new IllegalStateException("Resource must not be null");
        }

        if (resource.startsWith("http://") || resource.startsWith("https://")) {
            stream = new BufferedReader(new InputStreamReader(new URL(resource).openStream())).lines();
        } else {
            stream = Files.lines(Paths.get(resource));
        }
        return stream;
    }
}
