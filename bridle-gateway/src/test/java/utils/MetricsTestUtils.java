package utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.TestUtils.PROMETHEUS_URI;

public class MetricsTestUtils {

    public static final String START_TAG_BODY_SYMBOL = "{";

    public static final String MAIN_ROUTE_NAME = "mainRoute";

    public static final String METRIC_NAME_FOR_PERCENTILES = "CamelRoutePolicy_seconds";

    public static final String TAG_PERCENTILE = "quantile";

    public static final int PROCESS_EXCHANGE_TIMEOUT = 10;

    public static void verifyMetrics(String routeName, int successCount, int failedCount, int handledErrors) {
        verifyMetrics(routeName, successCount, failedCount, handledErrors, PROMETHEUS_URI);
    }

    public static void verifyMetrics(String routeName,
            int successCount,
            int failedCount,
            int handledErrors,
            String prometheusUri) {
        verifyMetrics(routeName,
                      result -> Objects.equals(result, successCount),
                      result -> Objects.equals(result, failedCount),
                      result -> Objects.equals(result, handledErrors),
                      prometheusUri);
    }

    public static void verifyMetrics(String routeName,
            Predicate<Integer> successCount,
            Predicate<Integer> failedCount,
            Predicate<Integer> handledErrors,
            String prometheusUri) {
        ResponseEntity<String> metricsResponse =
                TestUtils.sendHttpRequest(prometheusUri, String.class, HttpMethod.GET, null);
        parseMessagesAmount(metricsResponse.getBody(), routeName);
        if (failedCount != null) {
            int receivedFailedMessageCount =
                    MetricsTestUtils.parseFailedMessagesAmount(metricsResponse.getBody(), routeName);
            assertTrue(failedCount.test(receivedFailedMessageCount));
        }
        int handledErrorsCount =
                MetricsTestUtils.parseMessagesWithHandledErrorAmount(metricsResponse.getBody(), routeName);
        if (handledErrors != null) {
            assertTrue(handledErrors.test(handledErrorsCount));
        }
        int receivedSuccessMessageCount =
                MetricsTestUtils.parseSuccessMessagesAmount(metricsResponse.getBody(), routeName);
        if (successCount != null) {
            assertTrue(successCount.test(receivedSuccessMessageCount - handledErrorsCount));
        }
    }

    public static int parseSuccessMessagesAmount(String metricsInfo, String routeName) {
        return extractDecimalMetric(metricsInfo, routeName, "camel_exchanges_succeeded_total").getValue().intValue();
    }

    public static int parseMessagesAmount(String metricsInfo, String routeName) {
        return extractDecimalMetric(metricsInfo, routeName, "camel_exchanges_total").getValue().intValue();
    }

    public static int parseFailedMessagesAmount(String metricsInfo, String routeName) {
        return extractDecimalMetric(metricsInfo, routeName, "camel_exchanges_failed_total").getValue().intValue();
    }

    public static int parseMessagesWithHandledErrorAmount(String metricsInfo, String routeName) {
        return extractDecimalMetric(metricsInfo, routeName, "camel_exchanges_failures_handled_total")
                .getValue()
                .intValue();
    }

    public static List<MetricsHolder<Double>> extractDecimalMetrics(String content, String routeId, String metricName) {
        List<String> metricRows = extractMetricRowsFromStringContent(content, routeId, metricName);
        List<MetricsHolder<Double>> metrics = extractDecimalMetricsFromMetricRows(metricRows);
        if (metrics.size() == 0) {
            throw new IllegalStateException(String.format("MetricsForTest is not found. MetricsForTest: %s RouteId: %s",
                                                          metricName,
                                                          routeId));
        }
        return metrics;
    }

    public static MetricsHolder<Double> extractDecimalMetric(String content, String routeId, String metricName) {
        List<MetricsHolder<Double>> metrics;
        if (content.lines().count() == 1) {
            metrics = parseSingleRowMetrics(content, routeId, metricName);
        } else {
            metrics = extractDecimalMetrics(content, routeId, metricName);
        }
        if (metrics.size() != 1) {
            throw new RuntimeException(String.format(
                    "MetricsForTest has found more than once. MetricsForTest: %s RouteId: %s",
                    metricName,
                    routeId));
        }
        MetricsHolder<Double> result = metrics.get(0);
        System.out.printf("metricName: %s: %s%n", metricName, result.getValue());
        return result;
    }

    private static List<MetricsHolder<Double>> parseSingleRowMetrics(String content,
            String routeId,
            String metricName) {
        Pattern pattern =
                Pattern.compile("(%s\\{.+?routeId=\"%s\".+?\\}\\s)(\\d+\\.\\d+)".formatted(metricName, routeId));
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return List.of(new MetricsHolder<>(matcher.group(1), Double.parseDouble(matcher.group(2))));
        }
        return Collections.emptyList();
    }

    public static List<MetricsHolder<Double>> extractDecimalMetricsFromMetricRows(List<String> metricRows) {
        return metricRows.stream().map(metricRow -> {
            Double value = Double.parseDouble(StringUtils.substringAfterLast(metricRow, "}").trim());
            return new MetricsHolder<>(metricRow, value);
        }).collect(Collectors.toList());
    }


    public static Double[] extractQuantilesTagValuesFromMetricApiResponse(String content) {
        List<String> metricRows =
                extractMetricRowsFromStringContent(content, MAIN_ROUTE_NAME, METRIC_NAME_FOR_PERCENTILES);
        return extractTagValuesFromMetricRows(metricRows, TAG_PERCENTILE)
                .stream()
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

    public static List<String> extractMetricRowsFromStringContent(String content, String routeId, String metricName) {
        String preparedMetricName =
                metricName.endsWith(START_TAG_BODY_SYMBOL) ? metricName : metricName + START_TAG_BODY_SYMBOL;
        String[] splitContent = content.split("\n");
        return Arrays
                .stream(splitContent)
                .filter(entry -> routeId == null || entry.contains(routeId))
                .filter(entry -> entry.contains(preparedMetricName))
                .collect(Collectors.toList());

    }
}
