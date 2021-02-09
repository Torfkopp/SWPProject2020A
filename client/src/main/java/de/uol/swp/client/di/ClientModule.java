package de.uol.swp.client.di;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import de.uol.swp.client.*;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.chat.IChatService;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.client.user.UserService;
import javafx.fxml.FXMLLoader;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Module that provides classes needed by the client.
 *
 * @author Marco Grawunder
 * @since 2019-09-18
 */
@SuppressWarnings("UnstableApiUsage")
public class ClientModule extends AbstractModule {

    final EventBus eventBus = new EventBus();
    // ResourceBundle selection. Uncomment the one you want to use and comment the others
    // TODO: should eventually be handled with a user setting or getting the client system locale
    // standard en_GB resource bundle, enabled by default
    final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", Locale.UK);
    // standard improved en_GB resource bundle
    //final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", new Locale("en", "GB", "improved"));
    // standard de_DE resource bundle
    //final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", Locale.GERMANY);
    // unicode-free en_GB resource bundle
    //final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", new Locale("en", "GB", "nounicode"));
    // unicode-free de_DE resource bundle
    //final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", new Locale("de", "DE", "nounicode"));
    // en_GB resource bundle for hearing impaired
    //final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", new Locale("en", "GB", "hearingImpaired"));
    // en_GB resource bundle for blind
    //final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", new Locale("en", "GB", "blind"));
    // standard de_NDS resource bundle
    //final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", new Locale("nds", "DE"));
    // standard degenerate resource bundle
    //final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", new Locale("en", "GB", "UwU"));
    // standard blank resource bundle
    //final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", new Locale("en", "GB", "blank"));

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(SceneManager.class, SceneManager.class).
                build(SceneManagerFactory.class));
        install(new FactoryModuleBuilder().implement(ClientConnection.class, ClientConnection.class).
                build(ClientConnectionFactory.class));
        bind(FXMLLoader.class).toProvider(FXMLLoaderProvider.class);
        bind(EventBus.class).toInstance(eventBus);
        bind(ClientUserService.class).to(UserService.class);
        bind(IChatService.class).to(ChatService.class);
        bind(ResourceBundle.class).toInstance(resourceBundle);
    }
}
