package routetest.httpkafka;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
}
