package com.stc04.reporters;

/**
 * Интерфейс передачи данных подсистеме взаимодействия с пользователем.
 */
public interface DataProcessReporter {

    /**
     * Метод, вызываемый для оповещения UI о новом значении.
     *
     * @param newValue новое значение.
     * @param done     флаг окончания обработки.
     */
    void reportState(long newValue, boolean done);
}
