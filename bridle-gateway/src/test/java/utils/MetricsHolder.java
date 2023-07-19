package utils;

public class MetricsHolder<T> {

    private final T value;

    private final String metricRow;

    public MetricsHolder(String metricRow,
            T value) {
        this.value = value;
        this.metricRow = metricRow;
    }

    public T getValue() {
        return value;
    }

    public String getMetricRow() {
        return metricRow;
    }
}
