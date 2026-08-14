package EX;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class testLength {

    @Test
    public void testStringLength() {
        String hello = "Hello, world! This is long enough";
        int length = hello.length();
        assertTrue(length > 15,
                "Длина строки (" + length + ") меньше или равна 15. Текст: \"" + hello + "\"");
    }
}
