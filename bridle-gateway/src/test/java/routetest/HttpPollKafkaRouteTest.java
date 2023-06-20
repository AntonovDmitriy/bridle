package routetest;

import com.bridle.App;
import org.apache.camel.CamelContext;
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
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/http-poll-kafka/application.yml"})
@CamelSpringBootTest
@Testcontainers
@DirtiesContext
public class HttpPollKafkaRouteTest {

    private static final String TOPIC_NAME = "routetest";

    @Autowired
    private CamelContext context;

    @Container
    public static MockServerContainer mockPollServer = new MockServerContainer(DockerImageName
            .parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion()));

    @Container
    private static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @BeforeAll
    public static void setUp() throws Exception {
        mockPollServer.start();
        System.setProperty("rest-poll.port", mockPollServer.getServerPort().toString());
        var mockPollerverClient = new MockServerClient(mockPollServer.getHost(), mockPollServer.getServerPort());
        mockPollerverClient
                .when(request().withMethod("GET").withPath("/salary"))
                .respond(response().withBody("52.255").withStatusCode(200));

        kafka.start();
        System.setProperty("kafka-out.brokers", "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
        kafka.execInContainer("/bin/bash", "-c",
                String.format("kafka-topics --create --bootstrap-server localhost:9092 " +
                        "--topic %s --partitions 1 --replication-factor 1", TOPIC_NAME));
    }

    @AfterAll
    public static void afterAll() throws Exception{
        mockPollServer.stop();
    }

    @Test
    public void verifySuccessHttpPollKafkaScenario() throws Exception {

        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyCompleted(3).create();
        boolean done = notify.matches(10, TimeUnit.SECONDS);
        Assertions.assertTrue(done);
    }
}

