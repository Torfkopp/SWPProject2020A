package de.uol.swp.client.di;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import de.uol.swp.common.util.ResourceManager;
import javafx.fxml.FXMLLoader;

/**
 * Class that provides instances of the FXMLLoader
 *
 * @author Marco Grawunder
 * @since 2019-09-18
 */
public class FXMLLoaderProvider implements Provider<FXMLLoader> {

    @Inject
    private Injector injector;

    @Override
    public FXMLLoader get() {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceManager.getResourceBundle());
        loader.setControllerFactory(injector::getInstance);
        return loader;
    }
}
