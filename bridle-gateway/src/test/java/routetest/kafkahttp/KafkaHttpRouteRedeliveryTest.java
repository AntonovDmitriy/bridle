package routetest.kafkahttp;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;

@SpringBootTest(classes = {App.class})
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/kafka-http/application.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
public class KafkaHttpRouteRedeliveryTest {

    private static final String TOPIC_NAME = "routetest";

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext context;

    @Container
    private static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @Container
    public static MockServerContainer mockServer = new MockServerContainer(DockerImageName
            .parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion()));

    @BeforeAll
    public static void setUp() throws Exception {
        kafka.start();
        System.setProperty("kafka-in.brokers", "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
        kafka.execInContainer("/bin/bash", "-c",
                String.format("kafka-topics --create --bootstrap-server localhost:9092 " +
                        "--topic %s --partitions 1 --replication-factor 1", TOPIC_NAME));

        mockServer.start();
        System.setProperty("rest-call.port", mockServer.getServerPort().toString());

        var mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
        mockServerClient
                .when(request().withMethod("POST").withPath("/person"))
                .respond(response().withStatusCode(500));

    }

    @AfterAll
    public static void afterAll() throws Exception {
        mockServer.stop();
    }

    @Test
    void verifyThatRouteReadMessagesFromTopicAndInvokeHttpEndpoint() throws Exception {

        NotifyBuilder notify = new NotifyBuilder(context).whenFailed(1).create();

        String message = "Test message";
        producerTemplate.sendBody("kafka-in:" + TOPIC_NAME, message);

        boolean done = notify.matches(10, TimeUnit.SECONDS);
        Assertions.assertTrue(done);
    }
}

