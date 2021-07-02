package de.uol.swp.client;

import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * A type used to cache the Images for drawing the game map
 *
 * @author Temmo Junkhoff
 */
class GameAssetImageCache {

    private static final Logger LOG = LogManager.getLogger(GameAssetImageCache.class);
    Map<String, Map<String, Image>> gameAssetImages = new HashMap<>();

    /**
     * Constructor.
     */
    public GameAssetImageCache() {}

    /**
     * Gets a specified asset from a specified rendering style.
     *
     * @param renderingStyle The name of the rendering style needed
     * @param asset          The name of the asset needed
     *
     * @return The Asset that was requested
     */
    public Image getAsset(String renderingStyle, String asset) {
        return gameAssetImages.get(renderingStyle).get(asset);
    }

    /**
     * Reads a specified rendering style from disk
     *
     * @param renderingStyle The rendering style that should be read from disk
     *
     * @return A boolean indicating if reading the style was successful or not
     */
    public boolean readStyle(String renderingStyle) {
        if (renderingStyle.equals("plain")) return false;
        if (gameAssetImages.containsKey(renderingStyle)) return true;
        gameAssetImages.put(renderingStyle, new HashMap<>());
        try {
            putAsset(renderingStyle, "water");
            putAsset(renderingStyle, "desert");
            putAsset(renderingStyle, "fields");
            putAsset(renderingStyle, "hills");
            putAsset(renderingStyle, "mountains");
            putAsset(renderingStyle, "pasture");
            putAsset(renderingStyle, "forest");
            putAsset(renderingStyle, "robber");
            putAsset(renderingStyle, "harbour_east");
            putAsset(renderingStyle, "harbour_northeast");
            putAsset(renderingStyle, "harbour_northwest");
            putAsset(renderingStyle, "harbour_west");
            putAsset(renderingStyle, "harbour_southwest");
            putAsset(renderingStyle, "road_northwest");
            putAsset(renderingStyle, "road_northeast");
            putAsset(renderingStyle, "road_south");
            putAsset(renderingStyle, "settlement");
            putAsset(renderingStyle, "city");
        } catch (CouldNotFindAssetException e) {
            gameAssetImages.remove(renderingStyle);
            return false;
        }
        return true;
    }

    /**
     * Puts an asset into the map used internally to store the images.
     *
     * @param renderingStyle The rendering style for which an image should be read
     * @param assetName      The name of the asset that should be read
     *
     * @throws de.uol.swp.client.GameAssetImageCache.CouldNotFindAssetException When the image could not be read
     */
    private void putAsset(String renderingStyle, String assetName) {
        if (!renderingStyle.equals("plain")) {
            try {
                gameAssetImages.get(renderingStyle)
                               .put(assetName, new Image("images/assets/" + renderingStyle + "/" + assetName + ".png"));
            } catch (IllegalArgumentException e) {
                LOG.error(e.getMessage());
                throw new CouldNotFindAssetException();
            }
        }
    }

    /**
     * An Exception used internally when an asset could not be read.
     */
    private class CouldNotFindAssetException extends RuntimeException {}
}
