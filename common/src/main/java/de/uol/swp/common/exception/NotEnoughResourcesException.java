package de.uol.swp.common.exception;

/**
 * Exception thrown when an Inventory operation cannot be completed successfully
 * due to a lack of the necessary Resources
 *
 * @author Phillip-André Suhr
 * @implNote This Exception extends the IllegalArgumentException which is an
 * unchecked, and as such, it won't be enforced by an IDE. It is strongly recommended
 * to check for this Exception unless other factors make it impossible to occur.
 * @see de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.AbstractInventory
 * @since 2021-07-02
 */
public class NotEnoughResourcesException extends IllegalArgumentException {

    /**
     * Constructor
     *
     * @param s The Exception message
     */
    public NotEnoughResourcesException(String s) {
        super(s);
    }
}
