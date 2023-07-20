package routetest.httppollkafka;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static com.bridle.configuration.routes.HttpPollKafkaConfiguration.GATEWAY_TYPE_HTTP_POLL_KAFKA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static utils.KafkaContainerUtils.countMessages;
import static utils.KafkaContainerUtils.createKafkaContainer;
import static utils.KafkaContainerUtils.createTopic;
import static utils.KafkaContainerUtils.readMessage;
import static utils.KafkaContainerUtils.setupKafka;
import static utils.MetricsTestUtils.verifyMetrics;
import static utils.MockServerContainerUtils.createMockServerClient;
import static utils.MockServerContainerUtils.createMockServerContainer;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/http-poll-kafka/application.yml"})
@CamelSpringBootTest
@Testcontainers
@DirtiesContext
@AutoConfigureMetrics
public class HttpPollKafkaRouteTest {

    public static final String POLL_SERVER_RESPONSE = "52.255";

    private static final String TOPIC_NAME = "routetest";

    @Container
    private static final KafkaContainer kafka = createKafkaContainer();

    private static final HttpRequest POLL_SERVER_REQUEST = request().withMethod("GET").withPath("/salary");

    @Container
    public static MockServerContainer mockPollServer = createMockServerContainer();

    @Autowired
    private CamelContext context;

    @BeforeAll
    public static void setUp() throws Exception {
        mockPollServer.start();
        System.setProperty("rest-poll.port", mockPollServer.getServerPort().toString());
        var mockPollerverClient = createMockServerClient(mockPollServer);
        mockPollerverClient
                .when(POLL_SERVER_REQUEST)
                .respond(response().withBody(POLL_SERVER_RESPONSE).withStatusCode(200));

        setupKafka(kafka, KAFKA_PORT);
        createTopic(kafka, TOPIC_NAME);
        assertEquals(0, countMessages(kafka, TOPIC_NAME));
    }

    @AfterAll
    public static void afterAll() throws Exception {
        mockPollServer.stop();
    }

    @Test
    void verifySuccessHttpPollKafkaScenario() throws Exception {
        int messageCount = 3;
        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyCompleted(messageCount).create();

        boolean done = notify.matches(10, TimeUnit.SECONDS);

        Assertions.assertTrue(done);
        var mockPollServerClient = createMockServerClient(mockPollServer);
        mockPollServerClient.verify(POLL_SERVER_REQUEST, VerificationTimes.exactly(messageCount));
        assertEquals(POLL_SERVER_RESPONSE, readMessage(kafka, TOPIC_NAME).stdOut().strip());
        assertEquals(messageCount, countMessages(kafka, TOPIC_NAME));
        verifyMetrics(GATEWAY_TYPE_HTTP_POLL_KAFKA, messageCount, 0, 0);
    }
}

