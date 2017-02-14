package reporters;

/**
 * Реализация интерфейса пользователя, использующая {@link System#out} для отображения данных.
 */
public class StdOutReporter implements DataProcessReporter {

    @Override
    public void reportState(long newValue, boolean done) {
        System.out.println((done ? "Result: " : "Current state: ") + newValue);
    }
}
