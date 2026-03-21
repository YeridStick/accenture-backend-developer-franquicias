package co.franquicias.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class PostgreSQLConnectionPoolTest {

    @Test
    void connectionPool_ShouldBeCreated() {
        PostgreSQLConnectionPool poolConfig = new PostgreSQLConnectionPool();
        
        ReflectionTestUtils.setField(poolConfig, "host", "localhost");
        ReflectionTestUtils.setField(poolConfig, "port", 5432);
        ReflectionTestUtils.setField(poolConfig, "database", "testdb");
        ReflectionTestUtils.setField(poolConfig, "username", "user");
        ReflectionTestUtils.setField(poolConfig, "password", "pass");
        ReflectionTestUtils.setField(poolConfig, "schema", "public");
        ReflectionTestUtils.setField(poolConfig, "initialSize", 5);
        ReflectionTestUtils.setField(poolConfig, "maxSize", 10);
        ReflectionTestUtils.setField(poolConfig, "maxIdleTime", 30);

        ConnectionPool pool = poolConfig.connectionPool();
        
        assertNotNull(pool);
        // We can't easily check the internal config of the pool without deep reflection
        // but at least we verify it doesn't throw exceptions during creation.
    }
}
