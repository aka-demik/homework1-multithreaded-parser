package com.stc04.parser;

import com.stc04.processors.DataProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Парсер текста в формате, описанном в задании 1 вариант 3.
 *
 * <p> Формат входных данных.
 * <p> Любая последоватеьность пробельных символов трактуется как разделитель значений.
 * Значение может начинаться со знака минус, дефис, длинное, среднее или цифровое тире.
 * При наличии любого из этих символов, значение трактуется как отрицательное.
 * Далее должна следовать последовательность цифр.
 */
public class Parser {

    /**
     * Создаёт экземпляр парсера.
     *
     * @param reader источник строк для парсера.
     * @param dataProcessor потребитель данных парсинга. Ему будет передан каждый найденый элемент
     *                      путём вызова {@link DataProcessor#consume}.
     * @throws IllegalArgumentException если reader или dataProcessor равны null.
     */
    public Parser(final Reader reader, DataProcessor dataProcessor) throws IllegalArgumentException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }
        if (dataProcessor == null) {
            throw new IllegalArgumentException("Data consumer must not be null");
        }
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }
        this.dataProcessor = dataProcessor;
    }

    /**
     * Выполняет обработку входных данных.
     *
     * @throws IOException
     * @throws NumberFormatException
     */
    public void run() throws IOException, NumberFormatException {
        String nextLine;
        while ((nextLine = bufferedReader.readLine()) != null){
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
                        dataProcessor.consume(l);
                    }
                }
            }
        }
    }

    private final BufferedReader bufferedReader;
    private final DataProcessor dataProcessor;
}
