package utils;

import org.testcontainers.containers.OracleContainer;

public class OracleContainerUtils {

    public static OracleContainer createOracleContainer() {
        return new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
                .withDatabaseName("testDB")
                .withUsername("testUser")
                .withPassword("testPassword");
    }
}
