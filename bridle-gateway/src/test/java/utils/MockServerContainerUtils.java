package utils;

import org.jetbrains.annotations.NotNull;
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

    @NotNull
    public static MockServerClient createMockServerClient(MockServerContainer mockServerContainer) {
        return new MockServerClient(mockServerContainer.getHost(), mockServerContainer.getServerPort());
    }
}
