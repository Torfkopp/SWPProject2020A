package de.uol.swp.client;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.uol.swp.client.di.ClientModule;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.exception.ChangePasswordExceptionMessage;
import de.uol.swp.common.user.exception.RegistrationExceptionMessage;
import de.uol.swp.common.user.exception.UserDeletionExceptionMessage;
import de.uol.swp.common.user.response.*;
import io.netty.channel.Channel;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.ResourceBundle;

/**
 * The application class of the client
 * <p>
 * This class handles the startup of the application, as well as, incoming login
 * and registration responses and error messages
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.ConnectionListener
 * @see javafx.application.Application
 * @since 2017-03-17
 */
@SuppressWarnings("UnstableApiUsage")
public class ClientApp extends Application implements ConnectionListener {

    private static final Logger LOG = LogManager.getLogger(ClientApp.class);

    @Inject
    private static ResourceBundle resourceBundle;
    private String host;
    private int port;

    private ClientUserService userService;

    private User user;

    private ClientConnection clientConnection;

    private EventBus eventBus;

    private SceneManager sceneManager;

    // -----------------------------------------------------
    // Java FX Methods
    // ----------------------------------------------------

    /**
     * Default startup method for javafx applications
     *
     * @param args Any arguments given when starting the application
     *
     * @since 2017-03-17
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        Parameters p = getParameters();
        List<String> args = p.getRaw();

        if (args.size() != 2) {
            host = "localhost";
            port = 8889;
            System.err.println("Usage: " + ClientConnection.class.getSimpleName() + " host port");
            System.err.println("Using default port " + port + " on " + host);
        } else {
            host = args.get(0);
            port = Integer.parseInt(args.get(1));
        }
        // do not establish a connection here
        // if a connection is established in this stage, no GUI is shown and
        // the exceptions are only visible in console!
    }

    @Override
    public void start(Stage primaryStage) {

        // Client app is created by Java, so injection must
        // be handled here manually
        Injector injector = Guice.createInjector(new ClientModule());

        // get user service from guice; is needed for logout
        this.userService = injector.getInstance(ClientUserService.class);

        // get event bus from guice
        eventBus = injector.getInstance(EventBus.class);
        // Register this class for de.uol.swp.client.events (e.g. for exceptions)
        eventBus.register(this);

        // Client app is created by Java, so injection must
        // be handled here manually
        SceneManagerFactory sceneManagerFactory = injector.getInstance(SceneManagerFactory.class);
        this.sceneManager = sceneManagerFactory.create(primaryStage);

        ClientConnectionFactory connectionFactory = injector.getInstance(ClientConnectionFactory.class);
        clientConnection = connectionFactory.create(host, port);
        clientConnection.addConnectionListener(this);
        // JavaFX Thread should not be blocked too long!
        Thread t = new Thread(() -> {
            try {
                clientConnection.start();
            } catch (Exception e) {
                exceptionOccurred(e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void stop() {
        if (userService != null && user != null) {
            userService.logout(user);
            user = null;
        }
        eventBus.unregister(this);
        // Important: Close the connection, so the connection thread can terminate.
        //            Else the client application will not stop
        LOG.trace("Trying to shutting down client ...");
        if (clientConnection != null) {
            clientConnection.close();
        }
        LOG.info("ClientConnection shutdown");
    }

    @Override
    public void connectionEstablished(Channel ch) {
        sceneManager.showLoginScreen();
    }

    @Override
    public void exceptionOccurred(String e) {
        sceneManager.showServerError(e);
    }

    /**
     * Handles a successful password changing process
     * <p>
     * If an ChangePasswordSuccessfulResponse object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the MainScreen window.
     *
     * @param message The ChangePasswordSuccessfulResponse object detected on the EventBus
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @see de.uol.swp.client.SceneManager
     * @since 2020-12-03
     */
    @Subscribe
    private void onChangePasswordSuccessfulMessage(ChangePasswordSuccessfulResponse message) {
        LOG.info("Password change was successful.");
        sceneManager.showMainScreen(user);
    }

    /**
     * Handles an unsuccessful registration
     * <p>
     * If a RegistrationExceptionMessage object is detected on the EventBus, this
     * method is called. It tells the SceneManager to show the sever error alert.
     * If the loglevel is set to Error or higher, "Registration error " and the
     * error message are written to the log.
     *
     * @param message The RegistrationExceptionMessage object detected on the EventBus
     *
     * @see de.uol.swp.client.SceneManager
     * @since 2019-09-02
     */
    @Subscribe
    private void onRegistrationExceptionMessage(RegistrationExceptionMessage message) {
        sceneManager.showServerError(resourceBundle.getString("error.server.registration") + ' ' + message);
        LOG.error("Registration error " + message);
    }

    /**
     * Handles a successful login
     * <p>
     * If an LoginSuccessfulResponse object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the main menu, and sets
     * this clients user to the user found in the object. If the loglevel is set
     * to DEBUG or higher, "User logged in successfully " and the username of the
     * logged in user are written to the log.
     *
     * @param message The LoginSuccessfulResponse object detected on the EventBus
     *
     * @see de.uol.swp.client.SceneManager
     * @since 2017-03-17
     */
    @Subscribe
    private void onLoginSuccessfulResponse(LoginSuccessfulResponse message) {
        LOG.debug("Received LoginSuccessfulResponse for User " + message.getUser().getUsername());
        this.user = message.getUser();
        sceneManager.showMainScreen(user);
    }

    /**
     * Handles an unsuccessful password changing process
     * <p>
     * If an ChangePasswordExceptionMessage object is detected on the EventBus, this
     * method is called. It tells the SceneManager to show the sever error alert.
     *
     * @param message The ChangePasswordExceptionMessage object detected on the EventBus
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @see de.uol.swp.client.SceneManager
     * @since 2020-12-03
     */
    @Subscribe
    private void onChangePasswordExceptionMessage(ChangePasswordExceptionMessage message) {
        sceneManager.showServerError(resourceBundle.getString("error.server.changepw") + ' ' + message);
        LOG.error("Change Password error: " + message);
    }

    /**
     * Handles a successful registration
     * <p>
     * If a RegistrationSuccessfulResponse object is detected on the EventBus, this
     * method is called. It tells the SceneManager to show the login window. If
     * the loglevel is set to INFO or higher, "Registration Successful." is written
     * to the log.
     *
     * @param message The RegistrationSuccessfulResponse object detected on the EventBus
     *
     * @see de.uol.swp.client.SceneManager
     * @since 2019-09-02
     */
    @Subscribe
    private void onRegistrationSuccessfulMessage(RegistrationSuccessfulResponse message) {
        LOG.info("Registration was successful.");
        sceneManager.showLoginScreen();
    }

    /**
     * Handles an unsuccessful User deletion
     * <p>
     * If a UserDeletionExceptionMessage object is detected on the EventBus, this method is called.
     * It tells the SceneManager to show the ServerError alert. If the loglevel is set to Error or
     * higher, "User Deletion error " and the error message are written to the log.
     *
     * @param message The UserDeletionExceptionMessage object detected on the EventBus
     *
     * @see de.uol.swp.client.SceneManager
     * @since 2020-12-17
     */
    @Subscribe
    private void onUserDeletionExceptionMessage(UserDeletionExceptionMessage message) {
        sceneManager.showServerError(resourceBundle.getString("error.server.deleteuser") + ' ' + message);
        LOG.error("User Deletion error: " + message);
    }

    /**
     * Handles a successful User deletions
     * <p>
     * If a UserDeletionSuccessfulResponse object is detected on the EventBus, this method is called.
     * It tells the SceneManager to show the login window. If the loglevel is set to INFO or higher,
     * "Deletion of user successful." is written to the log.
     *
     * @param message The UserDeletionSuccessfulResponse object detected on the EventBus
     *
     * @see de.uol.swp.client.SceneManager
     * @since 2020-12-17
     */
    @Subscribe
    private void onUserDeletionSuccessfulMessage(UserDeletionSuccessfulResponse message) {
        LOG.info("Deletion of user was successful.");
        sceneManager.showLoginScreen();
    }

    /**
     * Handles errors produced by the EventBus
     * <p>
     * If an DeadEvent object is detected on the EventBus, this method is called.
     * It writes "DeadEvent detected " and the error message of the detected DeadEvent
     * object to the log if the loglevel is set to ERROR or higher.
     *
     * @param deadEvent The DeadEvent object found on the EventBus
     *
     * @since 2019-08-07
     */
    @Subscribe
    private void onDeadEvent(DeadEvent deadEvent) {
        LOG.error("DeadEvent detected: " + deadEvent);
    }
}
