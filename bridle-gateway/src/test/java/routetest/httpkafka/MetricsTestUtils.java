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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static routetest.httpkafka.TestUtils.PROMETHEUS_URI;

public class MetricsTestUtils {

    public static final String START_TAG_BODY_SYMBOL = "{";
    public static final String MAIN_ROUTE_NAME = "mainRoute";
    public static final String METRIC_NAME_FOR_PERCENTILES = "CamelRoutePolicy_seconds";
    public static final String TAG_PERCENTILE = "quantile";
    public static final int PROCESS_EXCHANGE_TIMEOUT = 10;

    public static void verifyMetrics(String routeName, int successCount, int failedCount, int handledErrors) {
        ResponseEntity<String> metricsResponse =
                TestUtils.sendHttpRequest(PROMETHEUS_URI, String.class, HttpMethod.GET, null);
        int receivedFailedMessageCount =
                MetricsTestUtils.parseFailedMessagesAmount(metricsResponse.getBody(), routeName);
        assertEquals(failedCount, receivedFailedMessageCount);
        int handledErrorsCount =
                MetricsTestUtils.parseMessagesWithHandledErrorAmount(metricsResponse.getBody(), routeName);
        assertEquals(handledErrors, handledErrorsCount);
        int receivedSuccessMessageCount =
                MetricsTestUtils.parseSuccessMessagesAmount(metricsResponse.getBody(), routeName);
        assertEquals(successCount, receivedSuccessMessageCount - handledErrorsCount);
    }

    public static int parseSuccessMessagesAmount(String metricsInfo, String routeName) {
        return extractDecimalMetric(metricsInfo, routeName, "CamelExchangesSucceeded_total")
                .getValue().intValue();
    }

    public static int parseFailedMessagesAmount(String metricsInfo, String routeName) {
        return extractDecimalMetric(metricsInfo, routeName, "CamelExchangesFailed_total")
                .getValue().intValue();
    }

    public static int parseMessagesWithHandledErrorAmount( String metricsInfo, String routeName) {
        return extractDecimalMetric(metricsInfo, routeName, "CamelExchangesFailuresHandled_total")
                .getValue().intValue();
    }

    public static List<MetricsHolder<Double>> extractDecimalMetrics(String content,
                                                                    String routeId,
                                                                    String metricName) {
        List<String> metricRows = extractMetricRowsFromStringContent(content, routeId, metricName);
        List<MetricsHolder<Double>> metrics = extractDecimalMetricsFromMetricRows(metricRows);
        if (metrics.size() == 0) {
            throw new RuntimeException(String.format("MetricsForTest is not found. MetricsForTest: %s RouteId: %s",
                    metricName, routeId));
        }
        return metrics;
    }

    public static MetricsHolder<Double> extractDecimalMetric(String content,
                                                             String routeId,
                                                             String metricName) {
        List<MetricsHolder<Double>> metrics = extractDecimalMetrics(content, routeId, metricName);
        if (metrics.size() != 1) {
            throw new RuntimeException(String.format("MetricsForTest has found more than once. MetricsForTest: %s RouteId: %s",
                    metricName, routeId));
        }
        return metrics.get(0);
    }

    public static List<MetricsHolder<Double>> extractDecimalMetricsFromMetricRows(List<String> metricRows) {
        return metricRows.stream()
                .map(metricRow -> {
                    Double value = Double.parseDouble(
                            StringUtils.substringAfterLast(metricRow, "}").trim());
                    return new MetricsHolder<>(metricRow, value);
                })
                .collect(Collectors.toList());
    }


    public static Double[] extractQuantilesTagValuesFromMetricApiResponse(String content) {
        List<String> metricRows = extractMetricRowsFromStringContent(content,
                MAIN_ROUTE_NAME, METRIC_NAME_FOR_PERCENTILES);
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
