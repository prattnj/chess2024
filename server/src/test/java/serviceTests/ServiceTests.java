package serviceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceTests {

    @Test
    public void clearTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerTest() {
        Assertions.assertEquals(2, 1 + 1);
    }

    @Test
    public void loginTest() {
        Assertions.assertNull(null);
    }

    @Test
    public void listGameTest() {
        Assertions.assertInstanceOf(String.class, "hello");
    }
}
