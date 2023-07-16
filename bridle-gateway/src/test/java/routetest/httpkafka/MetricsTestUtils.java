package routetest.httpkafka;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.commons.lang3.StringUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricsTestUtils {

    public static final String START_TAG_BODY_SYMBOL = "{";
    public static final String MAIN_ROUTE_NAME = "mainRoute";
    public static final String METRIC_NAME_FOR_PERCENTILES = "CamelRoutePolicy_seconds";
    public static final String TAG_PERCENTILE = "quantile";
    public static final int PROCESS_EXCHANGE_TIMEOUT = 10;

    public static int parseSuccessMessagesAmount(CamelContext context, String metricsInfo, String routeName){
        List<MetricsHolder<Double>> results = extractDecimalMetrics(context,
                metricsInfo, routeName,  "CamelExchangesSucceeded_total");
        return results.get(0).getValue().intValue();
    }

    public static int parseFailedMessagesAmount(CamelContext context, String metricsInfo, String routeName){
        List<MetricsHolder<Double>> results = extractDecimalMetrics(context,
                metricsInfo, routeName,  "CamelExchangesFailed_total");
        return results.get(0).getValue().intValue();
    }

    public static int parseMessagesWithHandledErrorAmount(CamelContext context, String metricsInfo, String routeName){
        List<MetricsHolder<Double>> results = extractDecimalMetrics(context,
                metricsInfo, routeName,  "CamelExchangesFailuresHandled_total");
        return results.get(0).getValue().intValue();
    }

    public static void verifyThatAllMessagesIsFinished(NotifyBuilder notifier) {
        assertTrue(notifier.matches(PROCESS_EXCHANGE_TIMEOUT, TimeUnit.SECONDS));
    }

    public static void verifyThatAllMetricsMatchPredicate(List<MetricsHolder<Double>> metrics,
                                                      Predicate<? super MetricsHolder<Double>> predicate) {
        assertTrue(metrics.stream().allMatch(predicate));
    }

    public static List<MetricsHolder<Double>> extractDecimalMetrics(CamelContext context,
                                                                String content,
                                                                String routeId,
                                                                String metricName) {
        List<String> metricRows = extractMetricRowsFromStringContent(content, routeId, metricName);
        List<MetricsHolder<Double>> metrics = extractDecimalMetricsFromMetricRows(context, metricRows);
        if (metrics.size() == 0) {
            throw new RuntimeException(String.format("MetricsForTest is not found. MetricsForTest: %s RouteId: %s",
                    metricName, routeId));
        }
        return metrics;
    }

    public static MetricsHolder<Double> extractDecimalMetric(CamelContext context,
                                                         String content,
                                                         String routeId,
                                                         String metricName) {
        List<MetricsHolder<Double>> metrics = extractDecimalMetrics(context, content, routeId, metricName);
        if (metrics.size() != 1) {
            throw new RuntimeException(String.format("MetricsForTest has found more than once. MetricsForTest: %s RouteId: %s",
                    metricName, routeId));
        }
        return metrics.get(0);
    }

    public static List<MetricsHolder<Double>> extractDecimalMetricsFromMetricRows(CamelContext context, List<String> metricRows) {
        return metricRows.stream()
                .map(metricRow -> {
                    Double value = context.getTypeConverter().convertTo(BigDecimal.class,
                            StringUtils.substringAfterLast(metricRow, "}").trim()).doubleValue();
                    return new MetricsHolder<>(metricRow, value);
                })
                .collect(Collectors.toList());
    }


    public static Double[] extractQuantilesTagValuesFromMetricApiResponse(String content) {
        List<String> metricRows = extractMetricRowsFromStringContent(content,
                MAIN_ROUTE_NAME,  METRIC_NAME_FOR_PERCENTILES);
        return extractTagValuesFromMetricRows(metricRows, TAG_PERCENTILE).stream()
                .map(Double::parseDouble)
                .toList()
                .toArray(new Double[]{});
    }

    public static List<String> extractTagValuesFromMetricRows(List<String> metricRows, String tag) {
        List<String> tagValues = new ArrayList<>();
        for (String metricRow : metricRows) {
            tagValues.add(StringUtils.substringBetween(metricRow, tag + "=\"", "\""));
        }
        return tagValues;
    }

    public static List<String> extractMetricRowsFromStringContent(String content,
                                                              String routeId,
                                                              String metricName) {
        String preparedMetricName = metricName.endsWith(START_TAG_BODY_SYMBOL) ?
                metricName : metricName + START_TAG_BODY_SYMBOL;
        String[] splitContent = content.split("\n");
        return Arrays.stream(splitContent)
                .filter(entry -> routeId == null || entry.contains(routeId))
                .filter(entry -> entry.contains(preparedMetricName))
                .collect(Collectors.toList());

    }
}
