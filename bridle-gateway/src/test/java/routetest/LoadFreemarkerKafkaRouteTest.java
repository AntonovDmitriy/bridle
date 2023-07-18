package routetest;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;

@SpringBootTest(classes = {App.class})
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/load-freemarker-kafka/application.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
public class LoadFreemarkerKafkaRouteTest {

    private static final String TOPIC_NAME = "routetest";
    @Container
    private static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));
    @Autowired
    private ProducerTemplate producerTemplate;
    @Autowired
    private CamelContext context;

    @BeforeAll
    public static void setUp() throws Exception {
        kafka.start();
        System.setProperty("kafka-out.brokers", "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
        kafka.execInContainer("/bin/bash", "-c",
                String.format("kafka-topics --create --bootstrap-server localhost:9092" +
                        " --topic %s --partitions 1 --replication-factor 1", TOPIC_NAME));
    }

    @Test
    void verifySuccessLoadFreemarkerKafkaScenario() throws Exception {

        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyCompleted(5).create();

        boolean done = notify.matches(10, TimeUnit.SECONDS);
        Assertions.assertTrue(done);
    }
}

