package com.stc04.processors;

import com.stc04.reporters.DataProcessReporter;
import com.sun.istack.internal.Nullable;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Потокобезопасная реализация суммирования для {@link DataProcessor}.
 */
public class ThreadSafeSum implements DataProcessor {
    /**
     * Создаёт экземпляр <code>ThreadSafeSum</code> с отключенным отображением процесса обработки.
     */
    public ThreadSafeSum() {
        this.reporter = null;
    }

    /**
     * Создаёт экземпляр <code>ThreadSafeSum</code> с возможностью отображения процесса обработки.
     * @param reporter содержит реализацию вывода отчёта или null, если отчет не нужен.
     */
    public ThreadSafeSum(final @Nullable DataProcessReporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void consume(long newValue) {
        long val = value.addAndGet(newValue);
        if (reporter != null)
            reporter.reportState(val, false);
    }

    /**
     * @return Возвращает текущее накопленное значение суммы.
     */
    public long getValue() {
        return value.get();
    }

    private final AtomicLong value = new AtomicLong();
    private final @Nullable DataProcessReporter reporter;
}
