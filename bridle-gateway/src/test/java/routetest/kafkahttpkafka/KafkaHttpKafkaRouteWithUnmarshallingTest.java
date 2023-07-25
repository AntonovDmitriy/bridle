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

import java.util.concurrent.TimeUnit;

import static com.bridle.configuration.routes.KafkaHttpKafkaConfiguration.GATEWAY_TYPE_KAFKA_HTTP_KAFKA;
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
import static utils.TestUtils.getStringResources;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "spring.config.location=classpath:routetest/kafka-http-kafka/application-with-unmarshalling.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
@AutoConfigureMetrics
public class KafkaHttpKafkaRouteWithUnmarshallingTest {

    public static final String HTTP_RESPONSE_BODY = getStringResources("routetest/kafka-http-kafka/http-response.json");

    public static final HttpRequest REST_CALL_REQUEST = request().withMethod("POST").withPath("/person");

    private static final String TOPIC_NAME_REQUST = "routetest_request";

    private static final String TOPIC_NAME_RESPONSE = "routetest_response";

    @Container
    private static final KafkaContainer kafka = createKafkaContainer();

    private final static String MESSAGE_IN_KAFKA = getStringResources("routetest/kafka-http-kafka/test.json");

    private final static String EXPECTED_TRANSFORMED_MESSAGE_AFTER_CONSUMER = """
            {
              "system": {
                "name": "cool customer",
                "stream": "new system stream"
              },
              "companyDetails": {
                "name": "XYZ Corp",
                "founded": 1995,
                "locationCount": 2,
                "executiveCount": 2,
                "products": [
                  {
                    "name": "Product A",
                    "category": "Software",
                    "price": 199.99,
                    "versionCount": 2
                  },
                  {
                    "name": "Product B",
                    "category": "Hardware",
                    "price": 99.99,
                    "versionCount": 2
                  }
                ]
              }
            }""";

    private final static String EXPECTED_TRANSFORMED_MESSAGE_AFTER_PRODUCER = """
            {
              "fullName": "John Doe",
              "age": 30,
              "location": "San Francisco, Market Street, 100",
              "hobbiesSummary": [
                {
                  "hobby": "Sport - Basketball",
                  "experience": "5 years"
                },
                {
                  "hobby": "Music - Guitar",
                  "experience": "2 years"
                }
              ]
            }""";

    @Container
    public static MockServerContainer mockServer = createMockServerContainer();

    @Autowired
    private CamelContext context;

    @Autowired
    private ProducerTemplate producerTemplate;

    @BeforeAll
    public static void setUp() throws Exception {
        setupKafka(kafka, KAFKA_PORT);
        createTopic(kafka, TOPIC_NAME_REQUST);
        createTopic(kafka, TOPIC_NAME_RESPONSE);

        mockServer.start();
        System.setProperty("endpoints.rest-call-endpoint.mandatory.port", mockServer.getServerPort().toString());

        var mockServerClient = createMockServerClient(mockServer);
        mockServerClient.when(REST_CALL_REQUEST).respond(response(HTTP_RESPONSE_BODY).withStatusCode(200));
    }

    @AfterAll
    public static void afterAll() throws Exception {
        mockServer.stop();
    }

    @Test
    void verifySuccessKafkaHttpKafkaScenarioWithFullProcessingWithUnmarshalling() throws Exception {
        int messageCount = 1;
        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyCompleted(messageCount).create();

        producerTemplate.sendBody("kafka-in:" + TOPIC_NAME_REQUST, MESSAGE_IN_KAFKA);

        boolean done = notify.matches(10, TimeUnit.SECONDS);
        Assertions.assertTrue(done);
        var mockCallServerClient = createMockServerClient(mockServer);
        mockCallServerClient.verify(REST_CALL_REQUEST.withBody(EXPECTED_TRANSFORMED_MESSAGE_AFTER_CONSUMER),
                                    VerificationTimes.exactly(messageCount));
        assertEquals(EXPECTED_TRANSFORMED_MESSAGE_AFTER_PRODUCER,
                     readMessage(kafka, TOPIC_NAME_RESPONSE).stdOut().strip());
        assertEquals(messageCount, countMessages(kafka, TOPIC_NAME_RESPONSE));
        verifyMetrics(GATEWAY_TYPE_KAFKA_HTTP_KAFKA, messageCount, 0, 0);
    }
}

