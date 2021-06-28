package de.uol.swp.client.specialisedUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Specialised Class for an ObservableList
 * of lobbies in form of a ISimpleLobby-String-Pair
 *
 * @author Mario Fokken
 * @since 2021-06-17
 */
public class LobbyList {

    private final ObservableList<LobbyListItem> lobbies;

    /**
     * Constructor
     */
    public LobbyList() { lobbies = FXCollections.observableArrayList(); }

    /**
     * Adds a pair to the list
     *
     * @param pair ISimpleLobby-String-Pairs to be put into the list
     */
    public void add(LobbyListItem pair) {lobbies.add(pair);}

    /**
     * Clears the list
     */
    public void clear() { lobbies.clear(); }

    /**
     * Gets the list
     *
     * @return An ObservableList of ISimpleLobby-String-Pairs
     */
    public ObservableList<LobbyListItem> get() {return lobbies;}
}
