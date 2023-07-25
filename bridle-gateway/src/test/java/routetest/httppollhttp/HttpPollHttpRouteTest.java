package routetest.httppollhttp;

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
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static com.bridle.configuration.routes.HttpPollHttpConfiguration.GATEWAY_TYPE_HTTP_POLL_HTTP;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static utils.MetricsTestUtils.verifyMetrics;
import static utils.MockServerContainerUtils.createMockServerClient;
import static utils.MockServerContainerUtils.createMockServerContainer;

@SpringBootTest(classes = {App.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/http-poll-http/application.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
@AutoConfigureMetrics
public class HttpPollHttpRouteTest {

    public static final HttpRequest CALL_SERVER_REQUEST =
            request().withMethod("POST").withPath("/person").withBody("52.255");

    private static final HttpRequest POLL_SERVER_REQUEST = request().withMethod("GET").withPath("/salary");

    @Container
    public static MockServerContainer mockPollServer = createMockServerContainer();

    @Container
    public static MockServerContainer mockCallServer = createMockServerContainer();

    @Autowired
    private CamelContext context;

    @BeforeAll
    public static void setUp() throws Exception {
        mockPollServer.start();
        System.setProperty("rest-poll.port", mockPollServer.getServerPort().toString());
        var mockPollerverClient = createMockServerClient(mockPollServer);
        mockPollerverClient.when(POLL_SERVER_REQUEST).respond(response().withBody("52.255").withStatusCode(200));

        mockCallServer.start();
        System.setProperty("endpoints.rest-call-endpoint.mandatory.port", mockCallServer.getServerPort().toString());
        var mockCallServerClient = createMockServerClient(mockCallServer);
        mockCallServerClient.when(CALL_SERVER_REQUEST).respond(response().withBody("OK").withStatusCode(200));
    }

    @AfterAll
    public static void afterAll() throws Exception {
        mockPollServer.stop();
        mockCallServer.stop();
    }

    @Test
    void verifySuccessHttpPollHttpScenario() throws Exception {
        int messageCount = 3;
        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyCompleted(messageCount).create();

        boolean done = notify.matches(10, TimeUnit.SECONDS);

        Assertions.assertTrue(done);
        var mockPollServerClient = createMockServerClient(mockPollServer);
        mockPollServerClient.verify(POLL_SERVER_REQUEST, VerificationTimes.exactly(messageCount));
        var mockCallServerClient = createMockServerClient(mockCallServer);
        mockCallServerClient.verify(CALL_SERVER_REQUEST, VerificationTimes.exactly(messageCount));
        verifyMetrics(GATEWAY_TYPE_HTTP_POLL_HTTP, messageCount, 0, 0);
    }
}

