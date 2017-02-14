package processors;

/**
 * Интерфейс <code>DataProcessor</code> должен быть реализован любым объектом, который будет
 * использоваться для обработки поступающих чисел.
 */
public interface DataProcessor {

    /**
     * Метод используется поставщиком данных, для передачи очередного значения.
     *
     * @param newValue очередное значение для обработки.
     */
    void consumeValue(long newValue);

}
