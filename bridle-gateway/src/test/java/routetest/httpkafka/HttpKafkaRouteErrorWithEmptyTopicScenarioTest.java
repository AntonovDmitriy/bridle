package routetest.httpkafka;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientResponseException;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import utils.KafkaContainerUtils;

import static com.bridle.configuration.routes.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static utils.MetricsTestUtils.verifyMetrics;
import static utils.TestUtils.sendPostHttpRequest;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/http-kafka/application.yml"})
@CamelSpringBootTest
@Testcontainers
@DirtiesContext
@AutoConfigureMetrics
public class HttpKafkaRouteErrorWithEmptyTopicScenarioTest {

    public static final String HTTP_SERVER_URL = "http://localhost:8080/camel/myapi";

    public static final String REQUEST_BODY = "Request Body";

    @Container
    private static final KafkaContainer kafka = KafkaContainerUtils.createKafkaContainer();

    @Autowired
    private CamelContext context;

    @BeforeAll
    public static void setUp() throws Exception {
        KafkaContainerUtils.setupKafka(kafka, KAFKA_PORT);
    }

    @Test
    void verifyErrorHttpKafkaScenarioWhenTopicDoesNotExist() throws Exception {
        RestClientResponseException exception = Assertions.assertThrows(RestClientResponseException.class,
                                                                        () -> sendPostHttpRequest(HTTP_SERVER_URL,
                                                                                                  REQUEST_BODY));
        assertEquals(501, exception.getRawStatusCode());
        assertEquals("Internal server Error", exception.getResponseBodyAsString());
        verifyMetrics(GATEWAY_TYPE_HTTP_KAFKA, 0, 0, 1);
    }
}
