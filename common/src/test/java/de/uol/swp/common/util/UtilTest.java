package de.uol.swp.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilTest {

    private static final Void a = null;
    private static final String b = "I am a String";
    private static final String c = "I am another String";
    private static final String d = "I am a String";

    @Test
    void testEquals() {
        assertFalse(Util.equals(a, b));

        assertFalse(Util.equals(b, c));
        assertTrue(Util.equals(b, b));
        assertTrue(Util.equals(b, d));
    }
}