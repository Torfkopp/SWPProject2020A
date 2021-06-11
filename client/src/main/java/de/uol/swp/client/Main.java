package de.uol.swp.client;

import java.util.Objects;

/**
 * This class is used as the entry point for the .jar file created by maven
 *
 * @author Marco Grawunder
 * @since 2019-09-05
 */
public class Main {

    /**
     * Entry point of the application if started as .jar file generated via maven.
     *
     * @param args Any arguments given when starting the application
     *
     * @since 2019-09-05
     */
    public static void main(String[] args) {
        System.out.println(Objects.deepEquals(null, null));
        ClientApp.main(args);
    }
}
