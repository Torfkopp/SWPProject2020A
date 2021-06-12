package de.uol.swp.common.util;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.*;

/**
 * A class to manage all access to the resource bundle
 *
 * @author Temmo Junkhoff
 * @since 2021-06-07
 */
public class ResourceManager {

    private static final ResourceBundle resourceBundle;
    @Inject
    @Named("lang")
    private static String lang;

    static {
        if (Strings.isNullOrEmpty(lang)) resourceBundle = null;
        else {
            String[] splitLang = lang.split("_");
            Locale locale;
            switch (splitLang.length) {
                case 1:
                    locale = new Locale(splitLang[0]);
                    break;
                case 2:
                    locale = new Locale(splitLang[0], splitLang[1]);
                    break;
                case 3:
                    locale = new Locale(splitLang[0], splitLang[1], splitLang[2]);
                    break;
                default:
                    locale = Locale.UK;
            }
            resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", locale);
        }
    }

    /**
     * Gets a specified key from the resource bundle.
     *
     * @param key The key whose resource is required
     *
     * @return The internationalized resource
     */
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

    /**
     * Gets a specified key from the resource bundle and injects arguments.
     *
     * @param key  The key whose resource is required
     * @param args The args which should be injected into the resource
     *
     * @return The internationalized resource with the arguments injected
     */
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

    /**
     * Gets a specified key from the resource bundle and injects arguments or returns the specified alternative.
     *
     * @param alternative The alternative that should be returned if the resource bundle is unavailable
     * @param key         The key whose resource is required
     * @param args        The args which should be injected into the resource
     *
     * @return The internationalized resource with the arguments injected or the alternative
     */
    public static String getIfAvailableElse(String alternative, String key, Object... args) {
        if (isAvailable()) return get(key, args);
        try {
            return String.format(alternative, args);
        } catch (NullPointerException | IllegalFormatException | ClassCastException e) {
            return alternative;
        }
    }

    /**
     * Gets a specified key from the resource bundle or returns the specified alternative.
     *
     * @param alternative The alternative that should be returned if the resource bundle is unavailable
     * @param key         The key whose resource is required
     *
     * @return The internationalized resource or the alternative
     */
    public static String getIfAvailableElse(String alternative, String key) {
        return isAvailable() ? get(key) : alternative;
    }

    /**
     * Checks if the resource bundle is available
     *
     * @return True if the resource bundle is available, false otherwise
     */
    public static boolean isAvailable() {
        return (resourceBundle != null);
    }
}
