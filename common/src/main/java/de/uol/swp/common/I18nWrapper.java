package de.uol.swp.common;

import com.google.inject.Inject;

import java.io.Serializable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A wrapper to delegate the internationalisation of text to the client side.
 * This wrapper can also handle a string that should be embedded in the internationalised text.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @since 2021-03-07
 */
public class I18nWrapper implements Serializable {

    //Will only be injected on the client side with a static injection requested in the ClientModule
    @Inject
    private static ResourceBundle resourceBundle;
    private final String attributeName;
    private final String insertString;
    private final Object[] insertStrings;

    /**
     * Constructor taking only an attribute name
     *
     * @param attributeName The name of the attribute
     */
    public I18nWrapper(String attributeName) {
        this.attributeName = attributeName;
        this.insertString = null;
        this.insertStrings = null;
    }

    /**
     * Constructor taking an attribute name and a string that should be embedded in the text
     *
     * @param attributeName The name of the attribute
     * @param insertString  The String to insert
     */
    public I18nWrapper(String attributeName, String insertString) {
        this.attributeName = attributeName;
        this.insertString = insertString;
        this.insertStrings = null;
    }

    /**
     * Constructor taking an attribute name and any number of strings that
     * should be embedded in the text
     *
     * @param attributeName The name of the attribute
     * @param insertStrings The Strings to insert
     *
     * @author Alwin Bossert
     * @author Sven Ahrens
     * @since 2021-03-19
     */
    public I18nWrapper(String attributeName, Object... insertStrings) {
        this.attributeName = attributeName;
        this.insertString = null;
        this.insertStrings = insertStrings;
    }

    @Override
    public String toString() {
        try {
            if (resourceBundle != null) {
                if (insertString == null && insertStrings == null) return resourceBundle.getString(attributeName);
                else if (insertStrings == null)
                    return String.format(resourceBundle.getString(attributeName), insertString);
                else return String.format(resourceBundle.getString(attributeName), insertStrings);
            } else return null;
        } catch (MissingResourceException exception1) {
            try {
                return resourceBundle.getString("missingproperty");
            } catch (MissingResourceException exception2) {
                return "Missing language property";
            }
        }
    }
}
