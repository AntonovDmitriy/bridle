package routetest.httpkafka;

import com.bridle.App;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientResponseException;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import utils.KafkaContainerUtils;

import static com.bridle.configuration.routes.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static utils.MetricsTestUtils.verifyMetrics;
import static utils.TestUtils.getStringResources;
import static utils.TestUtils.sendPostHttpRequest;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "spring.config.location=classpath:routetest/http-kafka/application-with-validation.yml"})
@CamelSpringBootTest
@Testcontainers
@DirtiesContext
@AutoConfigureMetrics
public class HttpKafkaRouteValidationErrorTest {

    public static final String HTTP_SERVER_URL = "http://localhost:8080/camel/myapi";

    private static final String TOPIC_NAME = "routetest";

    @Container
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
            .withEnv("KAFKA_DELETE_TOPIC_ENABLE", "true")
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false");

    @BeforeAll
    public static void setUp() throws Exception {
        KafkaContainerUtils.setupKafka(kafka, KAFKA_PORT);
    }

    @Test
    void verifyErrorHttpKafkaScenarioWhenValidationError() throws Exception {
        KafkaContainerUtils.createTopic(kafka, TOPIC_NAME);
        String textMessage = getStringResources("routetest/http-kafka/test-without-products.json");

        RestClientResponseException exception = Assertions.assertThrows(RestClientResponseException.class,
                                                                        () -> sendPostHttpRequest(HTTP_SERVER_URL,
                                                                                                  textMessage));

        assertTrue(exception.getResponseBodyAsString().contains("JSON validation error with 2 errors"));
        assertEquals(400, exception.getRawStatusCode());
        assertEquals(0, KafkaContainerUtils.countMessages(kafka, TOPIC_NAME));
        verifyMetrics(GATEWAY_TYPE_HTTP_KAFKA, 0, 0, 1);
        KafkaContainerUtils.deleteTopic(kafka, TOPIC_NAME);
    }
}

