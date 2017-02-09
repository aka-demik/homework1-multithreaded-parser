package com.stc04.parser;

import com.stc04.processors.DataProcessor;
import com.stc04.processors.StateProcessor;

import java.io.*;
import java.net.URL;

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
        if (resource == null) {
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
        } catch (Exception ex) {
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
            if (fr != null) {
                fr.close();
            }

            if (is != null) {
                is.close();
            }
            throw ex;
        }
    }
}
