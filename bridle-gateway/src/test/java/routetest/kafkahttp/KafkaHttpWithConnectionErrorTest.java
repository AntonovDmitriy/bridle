package routetest.kafkahttp;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static com.bridle.configuration.routes.KafkaHttpConfiguration.GATEWAY_TYPE_KAFKA_HTTP;
import static org.mockserver.model.HttpRequest.request;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static utils.KafkaContainerUtils.createKafkaContainer;
import static utils.KafkaContainerUtils.createTopic;
import static utils.KafkaContainerUtils.setupKafka;
import static utils.KafkaContainerUtils.writeMessageToTopic;
import static utils.MetricsTestUtils.verifyMetrics;


@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/kafka-http/application.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
@AutoConfigureMetrics
public class KafkaHttpWithConnectionErrorTest {

    public static final String MESSAGE_BODY = "52.255";

    public static final HttpRequest CALL_SERVER_REQUEST =
            request().withMethod("POST").withPath("/person").withBody(MESSAGE_BODY);

    private static final String TOPIC_NAME = "routetest";

    @Container
    private static final KafkaContainer kafka = createKafkaContainer();

    @Autowired
    private CamelContext context;

    @Autowired
    private ProducerTemplate producerTemplate;

    @BeforeAll
    public static void setUp() throws Exception {
        setupKafka(kafka, KAFKA_PORT);
        System.setProperty("kafka-out.brokers", "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
        System.setProperty("kafka-in.brokers", "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
    }

    @Test
    void verifyThatRouteReadMessagesFromTopicAndInvokeHttpEndpoint() throws Exception {
        int messageCount = 1;
        createTopic(kafka, TOPIC_NAME);
        NotifyBuilder notify = new NotifyBuilder(context).whenFailed(messageCount).create();

        writeMessageToTopic(kafka, TOPIC_NAME, MESSAGE_BODY);

        boolean done = notify.matches(10, TimeUnit.SECONDS);
        Assertions.assertTrue(done);
        verifyMetrics(GATEWAY_TYPE_KAFKA_HTTP, 0, messageCount, 0);
    }
}

