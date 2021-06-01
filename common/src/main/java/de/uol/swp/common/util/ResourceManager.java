package de.uol.swp.common.util;

import com.google.inject.Inject;

import java.util.IllegalFormatException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceManager {

    @Inject
    private static ResourceBundle resourceBundle;

    public static String get(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (NullPointerException | MissingResourceException | ClassCastException error) {
            try {
                return resourceBundle.getString("missingresource");
            } catch (NullPointerException | MissingResourceException | ClassCastException otherError) {
                return "Missing Resource";
            }
        }
    }

    public static String get(String key, Object... args) {
        if (args == null) return get(key);
        try {
            return String.format(resourceBundle.getString(key), args);
        } catch (NullPointerException | MissingResourceException | ClassCastException | IllegalFormatException error) {
            try {
                return resourceBundle.getString("missingresource");
            } catch (NullPointerException | MissingResourceException | ClassCastException otherError) {
                return "Missing Resource";
            }
        }
    }
}
