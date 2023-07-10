package routetest.httpkafka;

import com.bridle.App;
import org.apache.camel.CamelContext;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import routetest.utils.EndpointSendEventNotifier;

import java.io.IOException;

import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;

@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/http-kafka/application-redelivery.yml"})
@CamelSpringBootTest
@Testcontainers
@DirtiesContext
public class HttpKafkaRouteBasedRedeliveryTest {

    private static final String TOPIC_NAME = "routetest";

    @Container
    private static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
            .withEnv("KAFKA_DELETE_TOPIC_ENABLE", "true")
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false");
    public static final String HTTP_SERVER_URL = "http://localhost:8080/camel/myapi";
    public static final String REQUEST_BODY = "Request Body";

    @Autowired
    private CamelContext context;

    @BeforeAll
    public static void setUp() throws Exception {
        kafka.start();
        System.setProperty("kafka-out.brokers", "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
    }

    private static CommandResult createTopic() throws IOException, InterruptedException {
        org.testcontainers.containers.Container.ExecResult execResult = kafka.execInContainer("/bin/bash", "-c",
                String.format("kafka-topics --create --bootstrap-server localhost:9092" +
                        " --topic %s --partitions 1 --replication-factor 1", TOPIC_NAME));
        return new CommandResult(execResult.getExitCode(), execResult.getStdout(), execResult.getStderr());
    }

    private record CommandResult(int execCode, String stdOut, String stdErr) {
    }

    private static CommandResult deleteTopic() throws IOException, InterruptedException {
        org.testcontainers.containers.Container.ExecResult execResult = kafka.execInContainer("/bin/bash", "-c",
                String.format("kafka-topics --delete --bootstrap-server localhost:9092 --topic %s", TOPIC_NAME));
        return new CommandResult(execResult.getExitCode(), execResult.getStdout(), execResult.getStderr());
    }

    private static CommandResult readMessage() throws IOException, InterruptedException {
        org.testcontainers.containers.Container.ExecResult execResult = kafka.execInContainer("/bin/bash", "-c",
                String.format("kafka-console-consumer --bootstrap-server localhost:9092" +
                        " --topic %s --from-beginning --max-messages 1", TOPIC_NAME));
        return new CommandResult(execResult.getExitCode(), execResult.getStdout(), execResult.getStderr());
    }

    private static int countMessages() throws IOException, InterruptedException {
        org.testcontainers.containers.Container.ExecResult execResult = kafka.execInContainer("/bin/bash", "-c",
                String.format("kafka-run-class kafka.tools.GetOffsetShell --broker-list localhost:9092" +
                        " --topic %s --offsets -1", TOPIC_NAME));
        return Integer.parseInt(execResult.getStdout().strip().split(":")[2]);
    }

    @Test
    void verifySuccessMessageWithRedeliverySettings() throws Exception {
        createTopic();
        EndpointSendEventNotifier eventNotifierSuccessMessage = new EndpointSendEventNotifier(KAFKA_OUT_COMPONENT_NAME);
        context.getManagementStrategy().addEventNotifier(eventNotifierSuccessMessage);

        ResponseEntity<String> httpResponseEntity = sendValidHttpRequest();

        assertEquals(200, httpResponseEntity.getStatusCode().value());
        assertEquals("Success!", httpResponseEntity.getBody());
        assertEquals(1, eventNotifierSuccessMessage.getCounter());
        assertEquals(REQUEST_BODY, readMessage().stdOut().strip());
        assertEquals(1, countMessages());
        context.getManagementStrategy().removeEventNotifier(eventNotifierSuccessMessage);
        System.out.println(countMessages());
    }

    @Test
    void verifyRedeliveryWithFinalError() throws Exception {
        deleteTopic();
        EndpointSendEventNotifier notifierRedeliveredMessage = new EndpointSendEventNotifier(KAFKA_OUT_COMPONENT_NAME);
        context.getManagementStrategy().addEventNotifier(notifierRedeliveredMessage);

        RestClientResponseException exception = Assertions.assertThrows(RestClientResponseException.class,
                HttpKafkaRouteBasedRedeliveryTest::sendValidHttpRequest);

        assertEquals(500, exception.getRawStatusCode());
        assertEquals(3, notifierRedeliveredMessage.getCounter());

        context.getManagementStrategy().removeEventNotifier(notifierRedeliveredMessage);

    }

    @Test
    void verifyRedeliveryWithFinalSuccess() throws Exception {
        deleteTopic();
        EndpointSendEventNotifier notifierRedeliveredSuccess = new EndpointSendEventNotifier(KAFKA_OUT_COMPONENT_NAME);
        notifierRedeliveredSuccess.runActionWhenCounterExactlyEquals(2, e -> {
            try {
                createTopic();
            } catch (Exception ignored) {
            }
        });
        context.getManagementStrategy().addEventNotifier(notifierRedeliveredSuccess);

        ResponseEntity<String> httpResponseEntity = sendValidHttpRequest();

        assertEquals(200, httpResponseEntity.getStatusCode().value());
        assertEquals("Success!", httpResponseEntity.getBody());
        assertEquals(3, notifierRedeliveredSuccess.getCounter());
        assertEquals(REQUEST_BODY, readMessage().stdOut().strip());
        assertEquals(1, countMessages());
    }

    @NotNull
    private static ResponseEntity<String> sendValidHttpRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(REQUEST_BODY, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                HTTP_SERVER_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
    }

}

