import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AppTest {
    @Test
    public void testStartPostgres() {
        assertDoesNotThrow(App::startPostgres);
    }

    @Test
    public void testCreateDB() {
        assertDoesNotThrow(App::runCreateDB);
    }
}
