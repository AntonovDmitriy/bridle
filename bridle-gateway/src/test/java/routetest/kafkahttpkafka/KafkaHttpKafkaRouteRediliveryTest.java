package routetest.kafkahttpkafka;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
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
import utils.EndpointSendEventNotifier;

import java.util.concurrent.TimeUnit;

import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;
import static utils.KafkaContainerUtils.countMessages;
import static utils.KafkaContainerUtils.createKafkaContainer;
import static utils.KafkaContainerUtils.createTopic;
import static utils.KafkaContainerUtils.deleteTopic;
import static utils.KafkaContainerUtils.readMessage;
import static utils.KafkaContainerUtils.setupKafka;
import static utils.MockServerContainerUtils.createMockServerClient;
import static utils.MockServerContainerUtils.createMockServerContainer;
import static utils.TestUtils.getStringResources;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/kafka-http-kafka/application.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
@AutoConfigureMetrics
public class KafkaHttpKafkaRouteRediliveryTest {

    public static final String HTTP_RESPONSE_BODY = getStringResources("routetest/kafka-http-kafka/http-response.json");

    private static final String TOPIC_NAME_REQUST = "routetest_request";

    private static final String TOPIC_NAME_RESPONSE = "routetest_response";

    @Container
    private static final KafkaContainer kafka = createKafkaContainer();

    private final static String MESSAGE_IN_KAFKA = getStringResources("routetest/kafka-http-kafka/test.json");

    public static final HttpRequest REST_CALL_REQUEST =
            request().withMethod("POST").withPath("/person").withBody(MESSAGE_IN_KAFKA);

    @Container
    public static MockServerContainer mockServer = createMockServerContainer();

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

        mockServer.start();
        System.setProperty("endpoints.rest-call-endpoint.mandatory.port", mockServer.getServerPort().toString());
    }

    @AfterAll
    public static void afterAll() throws Exception {
        mockServer.stop();
    }

    @Test
    void verifySuccessMessageWithRedeliverySettings() throws Exception {
        createTopic(kafka, TOPIC_NAME_REQUST);
        createTopic(kafka, TOPIC_NAME_RESPONSE);
        var mockServerClient = createMockServerClient(mockServer);
        mockServerClient.when(REST_CALL_REQUEST).respond(response(HTTP_RESPONSE_BODY).withStatusCode(200));
        EndpointSendEventNotifier eventNotifierSuccessMessage = new EndpointSendEventNotifier(KAFKA_OUT_COMPONENT_NAME);
        context.getManagementStrategy().addEventNotifier(eventNotifierSuccessMessage);

        int messageCount = 1;
        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyCompleted(messageCount).create();

        producerTemplate.sendBody("kafka-in:" + TOPIC_NAME_REQUST, MESSAGE_IN_KAFKA);

        boolean done = notify.matches(10, TimeUnit.SECONDS);
        Assertions.assertTrue(done);
        var mockCallServerClient = createMockServerClient(mockServer);
        mockCallServerClient.verify(REST_CALL_REQUEST, VerificationTimes.exactly(messageCount));
        assertEquals(HTTP_RESPONSE_BODY, readMessage(kafka, TOPIC_NAME_RESPONSE).stdOut().strip());
        assertEquals(messageCount, countMessages(kafka, TOPIC_NAME_RESPONSE));
        assertEquals(1, eventNotifierSuccessMessage.getCounter());
        deleteTopic(kafka, TOPIC_NAME_REQUST);
        deleteTopic(kafka, TOPIC_NAME_RESPONSE);
        context.getManagementStrategy().removeEventNotifier(eventNotifierSuccessMessage);

    }

    @Test
    void verifyRedeliveryWithFinalError() throws Exception {
        createTopic(kafka, TOPIC_NAME_REQUST);
        createTopic(kafka, TOPIC_NAME_RESPONSE);
        int messageCount = 1;
        EndpointSendEventNotifier notifierRedeliveredMessage = new EndpointSendEventNotifier("rest-call");
        context.getManagementStrategy().addEventNotifier(notifierRedeliveredMessage);
        var mockServerClient = createMockServerClient(mockServer);
        mockServerClient.reset();
        mockServerClient.when(REST_CALL_REQUEST).respond(response(HTTP_RESPONSE_BODY).withStatusCode(500));
        NotifyBuilder notify = new NotifyBuilder(context).whenFailed(messageCount).create();

        producerTemplate.sendBody("kafka-in:" + TOPIC_NAME_REQUST, MESSAGE_IN_KAFKA);

        boolean done = notify.matches(20, TimeUnit.SECONDS);
        Assertions.assertTrue(done);
        assertEquals(3, notifierRedeliveredMessage.getCounter());
        mockServerClient.verify(REST_CALL_REQUEST, VerificationTimes.exactly(3));
        assertEquals(0, countMessages(kafka, TOPIC_NAME_RESPONSE));
        context.getManagementStrategy().removeEventNotifier(notifierRedeliveredMessage);
        deleteTopic(kafka, TOPIC_NAME_REQUST);
        deleteTopic(kafka, TOPIC_NAME_RESPONSE);
    }

    @Test
    void verifyRedeliveryWithFinalSuccess() throws Exception {
        createTopic(kafka, TOPIC_NAME_REQUST);
        createTopic(kafka, TOPIC_NAME_RESPONSE);
        int messageCount = 1;
        int expectedRedeliveryCount = 3;
        var mockServerClient = createMockServerClient(mockServer);
        mockServerClient.reset();
        mockServerClient.when(REST_CALL_REQUEST).respond(response(HTTP_RESPONSE_BODY).withStatusCode(500));
        EndpointSendEventNotifier notifierRedeliveredMessage = new EndpointSendEventNotifier("rest-call");
        notifierRedeliveredMessage.runActionWhenCounterExactlyEquals(2, e -> {
            mockServerClient.reset();
            mockServerClient.when(REST_CALL_REQUEST).respond(response(HTTP_RESPONSE_BODY).withStatusCode(200));
        });
        context.getManagementStrategy().addEventNotifier(notifierRedeliveredMessage);
        NotifyBuilder notify = new NotifyBuilder(context).whenCompleted(messageCount).create();

        producerTemplate.sendBody("kafka-in:" + TOPIC_NAME_REQUST, MESSAGE_IN_KAFKA);

        boolean done = notify.matches(20, TimeUnit.SECONDS);
        Assertions.assertTrue(done);
        assertEquals(expectedRedeliveryCount, notifierRedeliveredMessage.getCounter());
        mockServerClient.verify(REST_CALL_REQUEST, VerificationTimes.exactly(1));
        assertEquals(messageCount, countMessages(kafka, TOPIC_NAME_RESPONSE));
        assertEquals(HTTP_RESPONSE_BODY, readMessage(kafka, TOPIC_NAME_RESPONSE).stdOut().strip());
        context.getManagementStrategy().removeEventNotifier(notifierRedeliveredMessage);
        deleteTopic(kafka, TOPIC_NAME_REQUST);
        deleteTopic(kafka, TOPIC_NAME_RESPONSE);
    }
}

