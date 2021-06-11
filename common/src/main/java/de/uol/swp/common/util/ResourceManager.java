package de.uol.swp.common.util;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.*;

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

    public static String getIfAvailableElse(String alternative, String key, Object... args) {
        return isAvailable() ? get(key, args) : alternative;
    }

    public static String getIfAvailableElse(String alternative, String key) {
        return isAvailable() ? get(key) : alternative;
    }

    public static boolean isAvailable() {
        return (resourceBundle != null);
    }
}
