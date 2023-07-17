package routetest.httpkafka;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static com.bridle.configuration.routes.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static routetest.httpkafka.KafkaContainerUtils.readMessage;
import static routetest.httpkafka.MetricsTestUtils.verifyMetrics;
import static routetest.httpkafka.TestUtils.getStringResources;

@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/http-kafka/application-with-header-collector.yml"})
@CamelSpringBootTest
@Testcontainers
@DirtiesContext
@AutoConfigureMetrics
public class HttpKafkaRouteSuccessScenarioWithHeaderCollectorTest {

    private static final String TOPIC_NAME = "routetest";
    public static final String HTTP_SERVER_URL = "http://localhost:8080/camel/myapi";
    public static final String REQUEST_BODY = "Request Body";

    public static final String EXPECTED_TRANSFORMED_MESSAGE = """
            {
              "system": {
                "name": "cool customer",
                "stream": "new system stream"
              },
              "companyDetails": {
                "name": "XYZ Corp",
                "founded": 1995,
                "locationCount": 2,
                "executiveCount": 2,
                "products": [
                  {
                    "name": "Product A",
                    "category": "Software",
                    "price": 199.99,
                    "versionCount": 2
                  },
                  {
                    "name": "Product B",
                    "category": "Hardware",
                    "price": 99.99,
                    "versionCount": 2
                  }
                ]
              }
            }
            """;
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
        String textMessage = getStringResources("routetest/http-kafka/test.json");
        ResponseEntity<String> httpResponseEntity = sendValidHttpRequest(textMessage);

        assertEquals(200, httpResponseEntity.getStatusCode().value());
        assertEquals("Success!", httpResponseEntity.getBody());
        assertEquals(EXPECTED_TRANSFORMED_MESSAGE, readMessage(kafka, TOPIC_NAME).stdOut());
        verifyMetrics(GATEWAY_TYPE_HTTP_KAFKA, 1, 0, 0);
        KafkaContainerUtils.deleteTopic(kafka, TOPIC_NAME);
    }

    @NotNull
    private static ResponseEntity<String> sendValidHttpRequest(String textMessage) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(textMessage, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                HTTP_SERVER_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
    }
}

