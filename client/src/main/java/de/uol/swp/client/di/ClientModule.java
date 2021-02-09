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

import java.io.*;
import java.util.Locale;
import java.util.Properties;
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

    @Override
    protected void configure() {

        //Default Properties
        Properties defaultProps = new Properties();

        //Default language
        defaultProps.setProperty("lang", "en_GB");

        //Reading properties-file
        final Properties properties = new Properties(defaultProps);
        final String filepath = "client" + File.separator + "target" + File.separator + "classes" + File.separator + "config.properties";
        try {
            FileInputStream file = new FileInputStream(filepath);
            properties.load(file);
            file.close();
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find config file: " + filepath);
        } catch (IOException e) {
            System.out.println("Error reading config file");
        }

        //Reading the language property into a locale
        String[] lang = properties.getProperty("lang").split("_");
        Locale locale;
        switch (lang.length) {
            case 1:
                locale = new Locale(lang[0]);
                break;
            case 2:
                locale = new Locale(lang[0], lang[1]);
                break;
            case 3:
                locale = new Locale(lang[0], lang[1], lang[2]);
                break;
            default:
                System.out.println("Invalid Argument in config option \"lang\"");
                locale = Locale.UK;
        }

        //Setting the language
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.SWP2020A", locale);

        //DI stuff
        install(new FactoryModuleBuilder().implement(SceneManager.class, SceneManager.class).
                build(SceneManagerFactory.class));
        install(new FactoryModuleBuilder().implement(ClientConnection.class, ClientConnection.class).
                build(ClientConnectionFactory.class));
        bind(FXMLLoader.class).toProvider(FXMLLoaderProvider.class);
        bind(EventBus.class).toInstance(eventBus);
        bind(ClientUserService.class).to(UserService.class);
        bind(IChatService.class).to(ChatService.class);
        bind(ResourceBundle.class).toInstance(resourceBundle);
        bind(Properties.class).toInstance(properties);
    }
}
