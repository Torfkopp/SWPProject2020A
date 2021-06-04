package de.uol.swp.common;

import java.awt.*;

/**
 * Enum for the colours
 *
 * @author Mario Fokken
 * @since 2021-06-02
 */
public enum Colour {
    BLUE(Color.BLUE),
    RED(Color.RED),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    DARK_BLUE(new Color(0, 0, 100)),
    CRIMSON(new Color(128, 0, 0)),
    DARK_GREEN(new Color(0, 100, 0)),
    ORANGE(new Color(255, 69, 0)),
    PURPLE(new Color(128, 0, 128)),
    HOT_PINK(new Color(255, 105, 180)),
    KONGO_ROSA(new Color(255, 127, 124)),
    AQUA(new Color(0, 255, 255)),
    BROWN(new Color(98, 74, 46)),
    BRONZE(new Color(147, 72, 48)),
    SILVER(new Color(192, 192, 192)),

    TEMMO(new Color(226, 176, 7));

    private final Color colour;

    Colour(Color colour) {this.colour = colour;}

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    /**
     * Gets the colour
     *
     * @return awt.Color
     */
    public Color getColour() {
        return colour;
    }

    /**
     * Gets the Colours colourCode
     *
     * @return Array of red, green, blue value
     */
    public int[] getColourCode() {
        return new int[]{colour.getRed(), colour.getGreen(), colour.getBlue()};
    }
}
