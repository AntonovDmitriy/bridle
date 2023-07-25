package routetest.schedulerkafka;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static com.bridle.configuration.routes.SchedulerKafkaConfiguration.GATEWAY_TYPE_SCHEDULER_KAFKA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static utils.KafkaContainerUtils.countMessages;
import static utils.KafkaContainerUtils.createKafkaContainer;
import static utils.KafkaContainerUtils.createTopic;
import static utils.KafkaContainerUtils.readMessage;
import static utils.KafkaContainerUtils.setupKafka;
import static utils.MetricsTestUtils.verifyMetrics;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/load-freemarker-kafka/application.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
@AutoConfigureMetrics
public class SchedulerKafkaRouteTest {

    public static final String MESSAGE_BODY = """
            {
              "field1": "234",
              "field2": "test",
              "field3": "2022-01-24 12:35",
              "field4": "something"
            }""";

    private static final String TOPIC_NAME = "routetest";

    @Container
    private static final KafkaContainer kafka = createKafkaContainer();

    @Autowired
    private CamelContext context;

    @BeforeAll
    public static void setUp() throws Exception {
        setupKafka(kafka, KAFKA_PORT);
        System.setProperty("kafka-out.brokers",
                           "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
        System.setProperty("kafka-in.brokers",
                           "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
        createTopic(kafka, TOPIC_NAME);
    }

    @Test
    void verifySuccessLoadFreemarkerKafkaScenario() throws Exception {
        int messageCount = 3;
        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyCompleted(messageCount).create();

        boolean done = notify.matches(10, TimeUnit.SECONDS);

        Assertions.assertTrue(done);
        verifyMetrics(GATEWAY_TYPE_SCHEDULER_KAFKA, messageCount, 0, 0);
        assertEquals(MESSAGE_BODY, readMessage(kafka, TOPIC_NAME).stdOut().strip());
        assertEquals(messageCount, countMessages(kafka, TOPIC_NAME));
    }
}

