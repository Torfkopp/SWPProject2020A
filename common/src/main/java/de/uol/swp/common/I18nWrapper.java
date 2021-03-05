package de.uol.swp.common;

import java.util.ResourceBundle;

public class I18nWrapper {

    private final String attributeName;

    I18nWrapper(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getString(ResourceBundle resourceBundle) {
        return resourceBundle.getString(attributeName);
    }
}
