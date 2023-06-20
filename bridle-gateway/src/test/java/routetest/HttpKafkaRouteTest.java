package routetest;

import com.bridle.App;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.KafkaContainer.KAFKA_PORT;

@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/http-kafka/application.yml"})
@CamelSpringBootTest
@Testcontainers
@DirtiesContext
public class HttpKafkaRouteTest {

    private static final String TOPIC_NAME = "routetest";

    @Container
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @BeforeAll
    public static void setUp() throws Exception {
        kafka.start();
        System.setProperty("kafka-out.brokers", "localhost:" + kafka.getMappedPort(KAFKA_PORT).toString());
        kafka.execInContainer("/bin/bash", "-c",
                String.format("kafka-topics --create --bootstrap-server localhost:9092" +
                        " --topic %s --partitions 1 --replication-factor 1", TOPIC_NAME));
    }

    @Test
    public void verifySuccessHttpKafkaScenario() throws Exception {

        String uri = "http://" + "localhost" + ":" + "8080" + "/camel/myapi";
        String requestBody = "Request Body";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        String responseBody = response.getBody();

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals("Success!", responseBody);
    }
}

