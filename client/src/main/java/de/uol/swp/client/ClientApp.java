package de.uol.swp.client;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uol.swp.client.chat.IChatService;
import de.uol.swp.client.di.ClientModule;
import de.uol.swp.client.game.IGameService;
import de.uol.swp.client.lobby.ILobbyService;
import de.uol.swp.client.scene.ISceneService;
import de.uol.swp.client.sound.ISoundService;
import de.uol.swp.client.trade.ITradeService;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.util.ThreadManager;
import io.netty.channel.Channel;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.prefs.Preferences;

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
    private static final Preferences preferences = Preferences.userNodeForPackage(ClientApp.class);
    private static Injector injector;
    private String host;
    private int port;
    private IUserService userService;
    private ISceneService sceneService;
    private ClientConnection clientConnection;
    private EventBus eventBus;
    private boolean attemptingStoredLogin;

    // -----------------------------------------------------
    // Java FX Methods
    // ----------------------------------------------------

    /**
     * Helper method to instantiate all services.
     * <p>
     * This method is called from the main method of the application and handles
     * preparation of all client-side services so that their first instantiation
     * doesn't take place on the JavaFX Application Thread.
     * <p>
     * This is analogous to how service creation is handled on ServerApp start.
     * <p>
     * Note: The synchronous services are implicitly instantiated as well because
     * their asynchronous wrappers are bound to the interfaces called here.
     *
     * @author Phillip-André Suhr
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @since 2021-05-22
     */
    private static void createServices() {
        injector.getInstance(IUserService.class);
        injector.getInstance(ILobbyService.class);
        injector.getInstance(IChatService.class);
        injector.getInstance(IGameService.class);
        injector.getInstance(ITradeService.class);
        injector.getInstance(ISoundService.class);
    }

    /**
     * Default startup method for javafx applications
     *
     * @param args Any arguments given when starting the application
     *
     * @since 2017-03-17
     */
    public static void main(String[] args) {
        injector = Guice.createInjector(new ClientModule());
        createServices();
        launch(args);
    }

    @Override
    public void init() {
        Parameters p = getParameters();
        List<String> args = p.getRaw();

        if (args.size() != 2) {
            host = "localhost";
            port = 50010;
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
        // get user service from guice; is needed for logout
        this.userService = injector.getInstance(IUserService.class);

        // get event bus from guice
        eventBus = injector.getInstance(EventBus.class);
        // Register this class for de.uol.swp.client.events (e.g. for exceptions)
        eventBus.register(this);

        // Client app is created by Java, so injection must
        // be handled here manually
        this.sceneService = injector.getInstance(ISceneService.class);

        ClientConnectionFactory connectionFactory = injector.getInstance(ClientConnectionFactory.class);
        clientConnection = connectionFactory.create(host, port);
        clientConnection.addConnectionListener(this);
        // JavaFX Thread should not be blocked too long!
        Thread t = new Thread(() -> {
            try {
                clientConnection.start();
            } catch (InterruptedException e) {
                exceptionOccurred(e, e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void stop() {
        if (userService != null && userService.getLoggedInUser() != null) userService.logout(false);
        ThreadManager.shutdown();
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
        if (preferences.getBoolean("rememberMeEnabled", false)) {
            LOG.trace("'Remember Me' enabled, using stored user details for LoginRequest");
            String username = preferences.get("username", "");
            String password = preferences.get("password", "");
            attemptingStoredLogin = true;
            if (!username.equals("") && !password.equals("")) userService.login(username, password, true);
            else {
                LOG.trace("No user details found, showing Login screen");
                attemptingStoredLogin = false;
                sceneService.displayLoginScreen();
            }
        } else {
            LOG.trace("'Remember Me' disabled, showing Login screen");
            attemptingStoredLogin = false;
            sceneService.displayLoginScreen();
        }
    }

    @Override
    public void exceptionOccurred(String e) {
        if (e.startsWith("Cannot auth user ") && attemptingStoredLogin) {
            LOG.trace("Stored user details were incorrect, showing normal login screen");
            attemptingStoredLogin = false;
            sceneService.displayLoginScreen();
        } else {
            sceneService.showServerError(e);
        }
    }

    @Override
    public void exceptionOccurred(Throwable e, String cause) {
        sceneService.showServerError(e, cause);
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
        LOG.error("DeadEvent detected: {}", deadEvent);
    }
}
