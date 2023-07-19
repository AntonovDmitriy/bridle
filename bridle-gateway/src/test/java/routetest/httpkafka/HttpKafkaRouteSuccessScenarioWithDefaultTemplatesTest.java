package routetest.httpkafka;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import utils.KafkaContainerUtils;

import static com.bridle.configuration.routes.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static utils.KafkaContainerUtils.readMessage;
import static utils.MetricsTestUtils.verifyMetrics;
import static utils.TestUtils.getStringResources;
import static utils.TestUtils.sendPostHttpRequest;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "spring.config.location=classpath:routetest/http-kafka/application-with-default-templates.yml"})
@CamelSpringBootTest @Testcontainers @DirtiesContext @AutoConfigureMetrics
public class HttpKafkaRouteSuccessScenarioWithDefaultTemplatesTest {

    public static final String HTTP_SERVER_URL = "http://localhost:8080/camel/myapi";

    private static final String TOPIC_NAME = "routetest";

    @Container private static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1")).withEnv("KAFKA_DELETE_TOPIC_ENABLE",
                                                                                             "true")
                    .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false");

    @Autowired private CamelContext context;

    @BeforeAll
    public static void setUp() throws Exception {
        KafkaContainerUtils.setupKafka(kafka, KAFKA_PORT);
    }

    @Test
    void verifySuccessHttpKafkaScenarioWithDefaultResponseTemplate() throws Exception {
        KafkaContainerUtils.createTopic(kafka, TOPIC_NAME);

        String textMessage = getStringResources("routetest/http-kafka/test.json");
        ResponseEntity<String> httpResponseEntity = sendPostHttpRequest(HTTP_SERVER_URL, textMessage);

        assertEquals(200,
                     httpResponseEntity.getStatusCode()
                             .value());
        assertEquals("Success!", httpResponseEntity.getBody());
        assertEquals(textMessage,
                     readMessage(kafka, TOPIC_NAME).stdOut()
                             .strip());
        verifyMetrics(GATEWAY_TYPE_HTTP_KAFKA, 1, 0, 0);
        KafkaContainerUtils.deleteTopic(kafka, TOPIC_NAME);
    }
}
