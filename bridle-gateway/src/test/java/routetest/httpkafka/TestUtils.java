package routetest.httpkafka;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;

public class TestUtils {

    public static final String PROMETHEUS_URI = "http://localhost:8080/actuator/prometheus";

    public static <T> ResponseEntity<T> sendHttpRequest(String uri,
                                                 Class<T> responseType,
                                                 HttpMethod httpMethod,
                                                 HttpEntity<?> requestEntity) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                uri,
                httpMethod,
                requestEntity,
                responseType);
    }

    public static String getStringResources(String path) throws IOException {
        return new String(Files.readAllBytes(new ClassPathResource(path).getFile().toPath()));
    }
}
