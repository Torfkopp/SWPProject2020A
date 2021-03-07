package de.uol.swp.common;

import com.google.inject.Inject;

import java.io.Serializable;
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

    /**
     * Constructor taking only an attribute name
     *
     * @param attributeName the attribute name
     */
    public I18nWrapper(String attributeName) {
        this.attributeName = attributeName;
        this.insertString = null;
    }

    /**
     * Constructor taking a attribute name and a string that should be embedded in the text
     *
     * @param attributeName the attribute name
     * @param insertString  the insert string
     */
    public I18nWrapper(String attributeName, String insertString) {
        this.attributeName = attributeName;
        this.insertString = insertString;
    }

    @Override
    public String toString() {
        if (resourceBundle != null) {
            if (insertString == null) return resourceBundle.getString(attributeName);
            else return String.format(resourceBundle.getString(attributeName), insertString);
        } else return null;
    }
}
