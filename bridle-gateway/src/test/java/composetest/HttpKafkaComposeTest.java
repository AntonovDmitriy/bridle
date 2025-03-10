package composetest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;
import java.time.Duration;
import java.util.function.Predicate;

import static com.bridle.configuration.routes.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;
import static utils.MetricsTestUtils.parseMessagesAmount;
import static utils.MetricsTestUtils.verifyMetrics;

class HttpKafkaComposeTest {

    private static final String ROUTE_NAME = GATEWAY_TYPE_HTTP_KAFKA;

    private static final Predicate<String> APP_STARTS_TO_RECIEVE_LOAD_PREDICATE =
            s -> parseMessagesAmount(s, ROUTE_NAME) > 0;

    @Container
    private static final DockerComposeContainer<?> ENVIRONMENT = initEnvironment();

    private static final String COMPOSE_FILE_PATH = "compose/demo-http-kafka-compose.yml";

    private static final String SERVICE_NAME_GATEWAY = "gateway";

    private static final int SERVICE_PORT = 8080;

    private static DockerComposeContainer initEnvironment() {
        return new DockerComposeContainer<>(new File(COMPOSE_FILE_PATH))
                .withExposedService(SERVICE_NAME_GATEWAY,
                                    SERVICE_PORT,
                                    new WaitAllStrategy()
                                            .withStrategy(Wait.forLogMessage(".*Started App in.*\\n", 1))
                                            .withStrategy(Wait
                                                                  .forHttp("/actuator/prometheus")
                                                                  .forResponsePredicate(
                                                                          APP_STARTS_TO_RECIEVE_LOAD_PREDICATE)
                                                                  .forStatusCode(200))
                                            .withStartupTimeout(Duration.ofMinutes(10)))
                .withBuild(true)
                .withLocalCompose(true)
                .withStartupTimeout(Duration.ofMinutes(10));
    }

    @BeforeAll
    static void init() {
        ENVIRONMENT.start();
    }

    @AfterAll
    static void afterAll() {
        ENVIRONMENT.stop();
    }

    @Test
    void testService() {
        String address = ENVIRONMENT.getServiceHost(SERVICE_NAME_GATEWAY, SERVICE_PORT);
        Integer port = ENVIRONMENT.getServicePort(SERVICE_NAME_GATEWAY, SERVICE_PORT);
        verifyMetrics(ROUTE_NAME,
                      result -> result > 0,
                      result -> result == 0,
                      result -> result == 0,
                      buildPrometheusEndpoint(address, port));
    }

    private String buildPrometheusEndpoint(String address, Integer port) {
        return String.format("http://%s:%d/actuator/prometheus", address, port);
    }
}
