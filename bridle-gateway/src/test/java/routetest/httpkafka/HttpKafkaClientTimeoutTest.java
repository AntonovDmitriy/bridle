package routetest.httpkafka;

import com.bridle.App;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import routetest.utils.EndpointSendEventNotifier;
import utils.KafkaContainerUtils;

import java.util.concurrent.TimeUnit;

import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/http-kafka/application-redelivery.yml"})
@CamelSpringBootTest
@Testcontainers
@DirtiesContext
public class HttpKafkaClientTimeoutTest {

    public static final String HTTP_SERVER_URL = "http://localhost:8080/camel/myapi";

    public static final String REQUEST_BODY = "Request Body";

    private static final String TOPIC_NAME = "routetest";

    @Container
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
            .withEnv("KAFKA_DELETE_TOPIC_ENABLE", "true")
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false");

    @Autowired
    private CamelContext context;

    @BeforeAll
    public static void setUp() throws Exception {
        KafkaContainerUtils.setupKafka(kafka, KAFKA_PORT);
    }

    @NotNull
    private static ResponseEntity<String> sendValidHttpRequestWithTinyTimeout() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(REQUEST_BODY, headers);

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(1);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate.exchange(HTTP_SERVER_URL, HttpMethod.POST, requestEntity, String.class);
    }

    @Test
    void verifySuccessMessageWithClientTimeout() throws Exception {
        KafkaContainerUtils.createTopic(kafka, TOPIC_NAME);
        NotifyBuilder notifier = new NotifyBuilder(context).whenDone(1).create();

        Assertions.assertThrows(ResourceAccessException.class,
                                HttpKafkaClientTimeoutTest::sendValidHttpRequestWithTinyTimeout);
        notifier.matches(10, TimeUnit.SECONDS);

        assertEquals(REQUEST_BODY, KafkaContainerUtils.readMessage(kafka, TOPIC_NAME).stdOut().strip());
        assertEquals(1, KafkaContainerUtils.countMessages(kafka, TOPIC_NAME));
        KafkaContainerUtils.deleteTopic(kafka, TOPIC_NAME);
    }

    @Test
    void verifyRedeliveryWithClientTimeoutAndFinalError() throws Exception {
        EndpointSendEventNotifier notifierRedeliveredMessage = new EndpointSendEventNotifier(KAFKA_OUT_COMPONENT_NAME);
        context.getManagementStrategy().addEventNotifier(notifierRedeliveredMessage);
        NotifyBuilder notifier = new NotifyBuilder(context).whenFailed(1).create();

        Assertions.assertThrows(ResourceAccessException.class,
                                HttpKafkaClientTimeoutTest::sendValidHttpRequestWithTinyTimeout);
        notifier.matches(10, TimeUnit.SECONDS);

        assertEquals(3, notifierRedeliveredMessage.getCounter());
        context.getManagementStrategy().removeEventNotifier(notifierRedeliveredMessage);
    }

    @Test
    void verifyRedeliveryWithClientTimeoutAndFinalSuccess() throws Exception {
        EndpointSendEventNotifier notifierRedeliveredSuccess = new EndpointSendEventNotifier(KAFKA_OUT_COMPONENT_NAME);
        notifierRedeliveredSuccess.runActionWhenCounterExactlyEquals(2, e -> {
            try {
                KafkaContainerUtils.createTopic(kafka, TOPIC_NAME);
            } catch (Exception ignored) {
            }
        });
        context.getManagementStrategy().addEventNotifier(notifierRedeliveredSuccess);
        NotifyBuilder notifier = new NotifyBuilder(context).whenFailed(1).create();

        Assertions.assertThrows(ResourceAccessException.class,
                                HttpKafkaClientTimeoutTest::sendValidHttpRequestWithTinyTimeout);
        notifier.matches(10, TimeUnit.SECONDS);

        assertEquals(3, notifierRedeliveredSuccess.getCounter());
        assertEquals(REQUEST_BODY, KafkaContainerUtils.readMessage(kafka, TOPIC_NAME).stdOut().strip());
        assertEquals(1, KafkaContainerUtils.countMessages(kafka, TOPIC_NAME));
        KafkaContainerUtils.deleteTopic(kafka, TOPIC_NAME);
    }
}
