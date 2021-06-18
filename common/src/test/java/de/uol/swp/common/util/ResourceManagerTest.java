package de.uol.swp.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ResourceManagerTest {

    private static final String missingResource = "Missing Resource";
    private static final String randomKeyThatDoesNotMatter = "i.am.a.random.key.and.i.do.not.matter";
    private static final String alternative = "I am an alternative";
    private static final String formatableAlternative = "I might be an alternative or I might be a %s";
    private static final String alternativeWithUnexpectedFormatting = "I am an alternative %f";

    @Test
    void get() {
        assertEquals(missingResource, ResourceManager.get(null));
        assertEquals(missingResource, ResourceManager.get(randomKeyThatDoesNotMatter));
        assertEquals(missingResource, ResourceManager.get(randomKeyThatDoesNotMatter, 46, null, "name"));
    }

    @Test
    void getIfAvailableElse() {
        assertEquals(alternative, ResourceManager.getIfAvailableElse(alternative, randomKeyThatDoesNotMatter));
        assertEquals(String.format(formatableAlternative, "Tree"),
                     ResourceManager.getIfAvailableElse(formatableAlternative, randomKeyThatDoesNotMatter, "Tree"));
        assertEquals(alternativeWithUnexpectedFormatting, ResourceManager
                .getIfAvailableElse(alternativeWithUnexpectedFormatting, randomKeyThatDoesNotMatter, "Tree"));
    }

    @Test
    void isAvailable() {
        assertFalse(ResourceManager.isAvailable());
    }
}