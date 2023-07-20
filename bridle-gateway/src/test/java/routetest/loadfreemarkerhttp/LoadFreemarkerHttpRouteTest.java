package routetest.loadfreemarkerhttp;

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
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static com.bridle.configuration.routes.HttpPollHttpConfiguration.GATEWAY_TYPE_HTTP_POLL_HTTP;
import static com.bridle.configuration.routes.SchedulerHttpConfiguration.GATEWAY_TYPE_SCHEDULER_HTTP;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static utils.MetricsTestUtils.verifyMetrics;
import static utils.MockServerContainerUtils.createMockServerClient;
import static utils.MockServerContainerUtils.createMockServerContainer;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/load-freemarker-http/application.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
@AutoConfigureMetrics
public class LoadFreemarkerHttpRouteTest {


    public static final String MESSAGE_BODY = """
            {
              "field1": "234",
              "field2": "test",
              "field3": "2022-01-24 12:35",
              "field4": "something"
            }""";

    public static final HttpRequest CALL_SERVER_REQUEST =
            request().withMethod("POST").withPath("/person").withBody(MESSAGE_BODY);

    @Container
    public static MockServerContainer mockServer = createMockServerContainer();

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext context;

    @BeforeAll
    public static void setUp() throws Exception {
        mockServer.start();
        System.setProperty("rest-call.port", mockServer.getServerPort().toString());

        var mockServerClient = createMockServerClient(mockServer);
        mockServerClient.when(CALL_SERVER_REQUEST).respond(response("OK").withStatusCode(200));
    }

    @AfterAll
    public static void afterAll() throws Exception {
        mockServer.stop();
    }

    @Test
    void verifySuccessSchedulerHttpScenario() throws Exception {
        int messageCount = 3;
        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyCompleted(messageCount).create();

        boolean done = notify.matches(10, TimeUnit.SECONDS);

        Assertions.assertTrue(done);
        var mockCallServerClient = createMockServerClient(mockServer);
        mockCallServerClient.verify(CALL_SERVER_REQUEST, VerificationTimes.exactly(messageCount));
        verifyMetrics(GATEWAY_TYPE_SCHEDULER_HTTP, messageCount, 0, 0);
    }
}

