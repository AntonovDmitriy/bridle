package routetest;

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
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(classes = {App.class})
@TestPropertySource(properties = {"spring.config.location=classpath:routetest/load-freemarker-http/application.yml"})
@CamelSpringBootTest
@DirtiesContext
@Testcontainers
public class LoadFreemarkerHttpRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext context;
    @Container
    public static MockServerContainer mockServer = new MockServerContainer(DockerImageName
            .parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion()));

    @BeforeAll
    public static void setUp() throws Exception {
        mockServer.start();
        System.setProperty("rest-call.port", mockServer.getServerPort().toString());

        var mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
        mockServerClient
                .when(request().withMethod("POST").withPath("/person"))
                .respond(response("OK").withStatusCode(200));

    }

    @AfterAll
    public static void afterAll() throws Exception {
        mockServer.stop();
    }

    @Test
    void verifySuccessLoadFreemarkerHttpScenario() throws Exception {

        NotifyBuilder notify = new NotifyBuilder(context).whenExactlyCompleted(5).create();

        boolean done = notify.matches(10, TimeUnit.SECONDS);
        Assertions.assertTrue(done);
    }
}

