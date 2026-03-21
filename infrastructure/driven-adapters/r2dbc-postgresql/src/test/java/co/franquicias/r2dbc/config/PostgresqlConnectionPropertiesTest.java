package co.franquicias.r2dbc.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostgresqlConnectionPropertiesTest {

    @Test
    void testGettersAndSetters() {
        PostgresqlConnectionProperties props = new PostgresqlConnectionProperties();
        props.setDatabase("testdb");
        props.setHost("localhost");
        props.setPassword("pass");
        props.setPort(5432);
        props.setSchema("public");
        props.setUsername("user");

        assertEquals("testdb", props.getDatabase());
        assertEquals("localhost", props.getHost());
        assertEquals("pass", props.getPassword());
        assertEquals(5432, props.getPort());
        assertEquals("public", props.getSchema());
        assertEquals("user", props.getUsername());
    }

    @Test
    void testAllArgsConstructor() {
        PostgresqlConnectionProperties props = new PostgresqlConnectionProperties(
                "db", "schema", "user", "pass", "host", 5432
        );
        assertEquals("db", props.getDatabase());
        assertEquals("host", props.getHost());
    }
}
