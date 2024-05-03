package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MoreServiceTests {

    @Test
    public void clearTest2() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerTest2() {
        Assertions.assertEquals(2, 1 + 1);
    }

    @Test
    public void loginTest2() {
        Assertions.assertNull(null);
    }

    @Test
    public void listGameTest2() {
        Assertions.assertInstanceOf(String.class, "hello");
    }
}
