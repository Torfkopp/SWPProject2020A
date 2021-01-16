package de.uol.swp.common;

import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SerialisationTestHelperTest {

    @Test
    void checkNonSerialisable() {
        assertThrows(RuntimeException.class, () ->
                SerialisationTestHelper.checkSerialisableAndDeserialisable(new NotSerialisable(), NotSerialisable.class));
    }

    @Test
    void checkSerialisable() {
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable("Hallo", String.class));
    }

    private static class NotSerialisable implements Serializable {
        //Code Analysis: "Instantiating a 'Thread' with default 'run()' method" -Mario
        private final Thread thread = new Thread();
    }
}
