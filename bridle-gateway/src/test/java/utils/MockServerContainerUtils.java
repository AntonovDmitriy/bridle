package utils;

import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

public class MockServerContainerUtils {
    public static MockServerContainer createMockServerContainer() {
        return new MockServerContainer(DockerImageName
                                               .parse("mockserver/mockserver")
                                               .withTag("mockserver-" + MockServerClient.class
                                                       .getPackage()
                                                       .getImplementationVersion()));
    }
}
