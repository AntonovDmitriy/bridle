package routetest.kafkasqlkafka;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterAll;
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
import org.testcontainers.utility.MountableFile;

import java.util.concurrent.TimeUnit;

import static com.bridle.configuration.routes.KafkaSqlKafkaConfiguration.GATEWAY_TYPE_KAFKA_SQL_KAFKA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static utils.KafkaContainerUtils.countMessages;
import static utils.KafkaContainerUtils.createKafkaContainer;
import static utils.KafkaContainerUtils.createTopic;
import static utils.KafkaContainerUtils.readMessage;
import static utils.KafkaContainerUtils.setupKafka;
import static utils.MetricsTestUtils.verifyMetrics;
import static utils.OracleContainerUtils.createOracleContainer;
import static utils.TestUtils.getStringResources;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "spring.config.location=classpath:routetest/kafka-sql-kafka/procedure/clob-in-params/application.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
@AutoConfigureMetrics
public class KafkaSqlKafkaRouteOracleProcedureWithClobInParamsTest {

    private static final String TOPIC_NAME_REQUST = "routetest_request";

    private static final String TOPIC_NAME_RESPONSE = "routetest_response";

    @Container
    private static final KafkaContainer kafka = createKafkaContainer();

    private final static String MESSAGE_IN_KAFKA =
            getStringResources("routetest/kafka-sql-kafka/procedure/clob-in" + "-params/test-message.json");

    private final static String EXPECTED_TRANSFORMED_MESSAGE_AFTER_PRODUCER = """
            {
              "correlationId": "abc123",
              "status": "OK"
            }""";

    @Container
    public static OracleContainer oracle =
            createOracleContainer().withCopyFileToContainer(MountableFile.forClasspathResource(
                                                                    "routetest/kafka-sql-kafka/procedure/clob-in-params/init-oracle"),
                                                            "/container-entrypoint-startdb" + ".d/");

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

        oracle.start();
        System.setProperty("datasources.hikari.main-datasource.jdbc-url", oracle.getJdbcUrl());
    }

    @AfterAll
    public static void afterAll() throws Exception {
        oracle.stop();
    }

    @Test
    void verifyKafkaSqlKafkaOracleProcedureWithClobInParams() throws Exception {
        int messageCount = 1;
        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyCompleted(messageCount).create();

        producerTemplate.sendBody("kafka-in:" + TOPIC_NAME_REQUST, MESSAGE_IN_KAFKA);

        boolean done = notify.matches(10, TimeUnit.SECONDS);
        Assertions.assertTrue(done);
//        assertEquals(MESSAGE_IN_KAFKA,
//                     readMessage(kafka, TOPIC_NAME_RESPONSE).stdOut().strip());
        assertEquals(messageCount, countMessages(kafka, TOPIC_NAME_RESPONSE));
        verifyMetrics(GATEWAY_TYPE_KAFKA_SQL_KAFKA, messageCount, 0, 0);
    }
}

