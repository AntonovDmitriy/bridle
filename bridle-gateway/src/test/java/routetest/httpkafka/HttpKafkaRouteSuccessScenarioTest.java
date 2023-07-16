package routetest.httpkafka;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.bridle.configuration.routes.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static routetest.httpkafka.TestUtils.PROMETHEUS_URI;

@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/http-kafka/application.yml"})
@CamelSpringBootTest
@Testcontainers
@DirtiesContext
@AutoConfigureMetrics
public class HttpKafkaRouteSuccessScenarioTest {

    private static final String TOPIC_NAME = "routetest";
    public static final String HTTP_SERVER_URL = "http://localhost:8080/camel/myapi";
    public static final String REQUEST_BODY = "Request Body";

    @Autowired
    private CamelContext context;

    @Container
    private static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
            .withEnv("KAFKA_DELETE_TOPIC_ENABLE", "true")
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false");

    @BeforeAll
    public static void setUp() throws Exception {
        KafkaContainerUtils.setupKafka(kafka, KAFKA_PORT);
    }

    @Test
    void verifySuccessHttpKafkaScenario() throws Exception {
        KafkaContainerUtils.createTopic(kafka, TOPIC_NAME);

        ResponseEntity<String> httpResponseEntity = sendValidHttpRequest();

        assertEquals(200, httpResponseEntity.getStatusCode().value());
        assertEquals("Success!", httpResponseEntity.getBody());
        verifyMetrics(GATEWAY_TYPE_HTTP_KAFKA, 1, 0, 0);
        KafkaContainerUtils.deleteTopic(kafka, TOPIC_NAME);
    }

    private void verifyMetrics(String routeName, int successCount, int failedCount, int handledErrors) {
        ResponseEntity<String> metricsResponse =
                TestUtils.sendHttpRequest(PROMETHEUS_URI, String.class, HttpMethod.GET, null);
        int receivedFailedMessageCount =
                MetricsTestUtils.parseFailedMessagesAmount(context, metricsResponse.getBody(), routeName);
        assertEquals(failedCount, receivedFailedMessageCount);
        int handledErrorsCount =
                MetricsTestUtils.parseMessagesWithHandledErrorAmount(context, metricsResponse.getBody(), routeName);
        assertEquals(handledErrors, handledErrorsCount);
        int receivedSuccessMessageCount =
                MetricsTestUtils.parseSuccessMessagesAmount(context, metricsResponse.getBody(), routeName);
        assertEquals(successCount, receivedSuccessMessageCount - handledErrorsCount);

    }

    @NotNull
    private static ResponseEntity<String> sendValidHttpRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(REQUEST_BODY, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                HTTP_SERVER_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
    }
}

