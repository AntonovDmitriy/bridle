package routetest.kafkasql;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterAll;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.bridle.configuration.routes.KafkaSqlConfiguration.GATEWAY_TYPE_KAFKA_SQL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static utils.KafkaContainerUtils.createKafkaContainer;
import static utils.KafkaContainerUtils.createTopic;
import static utils.KafkaContainerUtils.setupKafka;
import static utils.MetricsTestUtils.verifyMetrics;
import static utils.OracleContainerUtils.createOracleContainer;
import static utils.TestUtils.getStringResources;


@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/kafka-sql/update/application.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
@AutoConfigureMetrics
public class KafkaSqlRouteOracleUpdateTest {

    private static final String TOPIC_NAME = "routetest";

    @Container
    private static final KafkaContainer kafka = createKafkaContainer();

    private final static String MESSAGE_IN_KAFKA = getStringResources("routetest/kafka-sql/update/test-message.json");

    @Container
    public static OracleContainer oracle =
            createOracleContainer().withInitScript("routetest/kafka-sql/update/init-oracle.sql");

    @Autowired
    private CamelContext context;

    @Autowired
    private ProducerTemplate producerTemplate;

    @BeforeAll
    public static void setUp() throws Exception {
        setupKafka(kafka, KAFKA_PORT);
        System.setProperty("components.kafka.kafka-in.brokers",
                           "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
        createTopic(kafka, TOPIC_NAME);

        oracle.start();
        System.setProperty("datasources.hikari.main-datasource.jdbc-url", oracle.getJdbcUrl());
    }

    @AfterAll
    public static void afterAll() throws Exception {
        oracle.stop();
    }

    @Test
    void verifyKafkaSqlWithUpdate() throws Exception {
        String query = "SELECT * FROM COMPANY";
        int messageCount = 1;
        String name = "XYZ Corp";
        List<Map<String, Object>> result = selectMessages(query);
        assertEquals(1, result.size());
        Map<String, Object> row = result.get(0);
        assertEquals(3, row.size());
        assertEquals(name, row.get("NAME"));
        assertEquals(new BigDecimal(1996), row.get("FOUNDED"));
        assertEquals(new BigDecimal(1), row.get("ID"));

        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyCompleted(messageCount).create();

        producerTemplate.sendBody("kafka-in:" + TOPIC_NAME, MESSAGE_IN_KAFKA);

        boolean done = notify.matches(10, TimeUnit.SECONDS);
        assertTrue(done);
        result = selectMessages(query);
        assertEquals(1, result.size());
        row = result.get(0);
        assertEquals(3, row.size());
        assertEquals(name, row.get("NAME"));
        assertEquals(new BigDecimal(1995), row.get("FOUNDED"));
        assertEquals(new BigDecimal(1), row.get("ID"));
        verifyMetrics(GATEWAY_TYPE_KAFKA_SQL, messageCount, 0, 0);
    }

    private List<Map<String, Object>> selectMessages(String query) {
        List<Map<String, Object>> result =
                producerTemplate.requestBody("main-jdbc-call:" + query + "?dataSource=#mainDataSource",
                                             null,
                                             List.class);
        return result;
    }
}

