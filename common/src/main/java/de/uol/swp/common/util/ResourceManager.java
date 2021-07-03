package de.uol.swp.common.util;

import com.google.common.base.Strings;
import de.uol.swp.common.I18nWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * A class to manage all access to the resource bundle
 *
 * @author Temmo Junkhoff
 * @since 2021-06-07
 */
public class ResourceManager {

    private static final Logger LOG = LogManager.getLogger(ResourceManager.class);
    private static ResourceBundle resourceBundle = null;

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
                LOG.error("Couldn't find resource \"{}\"", key);
                return resourceBundle.getString("missingresource");
            } catch (NullPointerException | MissingResourceException | ClassCastException otherError) {
                LOG.error("---- Also could't find backup resource; using generic text");
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
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof I18nWrapper[]) {
                    // Call toString on each array element
                    var x = Arrays.toString((I18nWrapper[]) args[i]);
                    // remove the array brackets
                    x = x.substring(x.indexOf("[") + 1, x.lastIndexOf("]"));
                    // match every comma that is preceded by a digit and followed by a space and return String to array
                    args[i] = x.replaceAll("(?<=[\\d]),(?=[ ])", "");
                }
            }
            return String.format(resourceBundle.getString(key), args);
        } catch (NullPointerException | MissingResourceException | ClassCastException | IllegalFormatException error) {
            try {
                LOG.error("Couldn't find or format resource \"{}\"", key);
                return resourceBundle.getString("missingresource");
            } catch (NullPointerException | MissingResourceException | ClassCastException otherError) {
                LOG.error("---- Also couldn't find backup resource; using generic text");
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
     * Gets the resource bundle.
     * This method should not be used, it's only needed to allow I18n in fxml files
     *
     * @return The resource bundle
     *
     * @author Temmo Junkhoff
     */
    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static void initialize(String lang) {
        Locale locale;
        if (Strings.isNullOrEmpty(lang)) locale = Locale.UK;
        else {
            String[] splitLang = lang.split("_");
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
        }
        try {
            resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", locale);
        } catch (NullPointerException | MissingResourceException e) {
            resourceBundle = null;
        }
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
