package routetest.kafkasqlkafka;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
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
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static com.bridle.configuration.routes.KafkaSqlKafkaDynamicConfiguration.GATEWAY_TYPE_KAFKA_SQL_KAFKA_DYNAMIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static utils.KafkaContainerUtils.*;
import static utils.MetricsTestUtils.verifyMetrics;
import static utils.OracleContainerUtils.createOracleContainer;
import static utils.TestUtils.getStringResources;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "spring.config.location=classpath:routetest/kafka-sql-kafka/sql/connection-error/application.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
@AutoConfigureMetrics
public class KafkaSqlKafkaRouteOracleConnectionErrorTest {

    private static final String TOPIC_NAME_REQUST = "routetest_request";

    private static final String TOPIC_NAME_RESPONSE = "routetest_response";

    @Container
    private static final KafkaContainer kafka = createKafkaContainer();

    private final static String MESSAGE_IN_KAFKA =
            getStringResources("routetest/kafka-sql-kafka/sql/connection-error/test-message.json");

    @Container
    public static OracleContainer oracle = createOracleContainer();

    @Autowired
    private CamelContext context;

    @Autowired
    private ProducerTemplate producerTemplate;

    @BeforeAll
    public static void setUp() throws Exception {
        setupKafka(kafka, KAFKA_PORT);
        System.setProperty("components.kafka.kafka-out.brokers",
                "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
        System.setProperty("components.kafka.kafka-in.brokers",
                "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
        createTopic(kafka, TOPIC_NAME_REQUST);
        createTopic(kafka, TOPIC_NAME_RESPONSE);

        System.setProperty("datasources.hikari.main-datasource.jdbc-url", oracle.getJdbcUrl());
    }

    @Test
    void verifyKafkaSqlKafkaOracleConnectionError() throws Exception {
        oracle.stop();

        int messageCount = 1;
        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyFailed(messageCount).create();

        producerTemplate.sendBody("kafka-in:" + TOPIC_NAME_REQUST, MESSAGE_IN_KAFKA);

        boolean done = notify.matches(10, TimeUnit.SECONDS);
        Assertions.assertTrue(done);
        assertEquals(0, countMessages(kafka, TOPIC_NAME_RESPONSE));
        verifyMetrics(GATEWAY_TYPE_KAFKA_SQL_KAFKA_DYNAMIC, 0, messageCount, 0);
    }
}

