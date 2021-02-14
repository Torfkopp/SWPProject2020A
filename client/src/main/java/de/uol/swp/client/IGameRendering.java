package de.uol.swp.client;

import de.uol.swp.common.game.map.Hexes.IGameHex;
import de.uol.swp.common.game.map.Hexes.IHarborHex;
import de.uol.swp.common.game.map.Hexes.IResourceHex;
import de.uol.swp.common.game.map.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * IGameRendering Interface
 * <p>
 * Interface that can be used to draw a game map on a canvas.
 * This provides one default method drawGameMap that can be called to draw a given game map on a given canvas.
 *
 * @author Timo Gerken
 * @author Temmo Junkhoff
 * @implNote No methods of this interface need to be implemented
 * @see de.uol.swp.common.game.map.GameMapManagement
 * @see javafx.scene.canvas.Canvas
 * @since 2021-01-31
 */
public interface IGameRendering {

    //Constants used to calculate different values relative to the size of the canvas
    double OFFSET_Y = 3.0;
    double OFFSET_X = 3.0;
    double HEX_HEIGHT_FACTOR = 1.0 / 5.5;
    double HEX_WIDTH_FACTOR = HEX_HEIGHT_FACTOR * (Math.sqrt(3) / 2);
    double SETTLEMENT_SIZE_FACTOR = HEX_HEIGHT_FACTOR / 4.0;
    double CITY_SIZE_FACTOR = SETTLEMENT_SIZE_FACTOR * 1.25;
    double ROAD_WIDTH_FACTOR = SETTLEMENT_SIZE_FACTOR / 2.0;
    double ROBBER_LINE_WIDTH_FACTOR = ROAD_WIDTH_FACTOR / 2.0;
    double TOKEN_SIZE_FACTOR = HEX_HEIGHT_FACTOR / 3.0;

    //Constants used for the colours
    Color TOKEN_COLOUR = Color.BEIGE;
    Color TEXT_COLOUR = Color.BLACK;
    Color BORDER_COLOUR = Color.BLACK;
    Color ROBBER_COLOUR = Color.BLACK;

    Color HARBOR_COLOUR = Color.SLATEGREY;
    Color WATER_COLOUR = Color.CORNFLOWERBLUE;
    Color DESERT_COLOUR = Color.WHITE;
    Color HILLS_COLOUR = Color.DARKORANGE;
    Color FOREST_COLOUR = Color.DARKGREEN;
    Color MOUNTAINS_COLOUR = Color.DARKGREY;
    Color FIELDS_COLOUR = Color.YELLOW;
    Color PASTURE_COLOUR = Color.LIGHTGREEN;

    Color PLAYER_1_COLOUR = Color.BLUE;
    Color PLAYER_2_COLOUR = Color.RED;
    Color PLAYER_3_COLOUR = Color.PURPLE;
    Color PLAYER_4_COLOUR = Color.WHITE;

    ResourceBundleWrapper resourceBundleWrapper = new ResourceBundleWrapper();
    Logger LOG = LogManager.getLogger(IGameRendering.class);

    /**
     * drawCity method
     * <p>
     * This method draws a city at the given coordinates.
     *
     * @param owner           Needed to access the color of player
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void drawCity(Player owner, double currentX, double currentY, double effectiveHeight,
                          GraphicsContext mapCtx) {
        mapCtx.setFill(getPlayerColour(owner));
        mapCtx.fillRoundRect(currentX - ((CITY_SIZE_FACTOR * effectiveHeight) / 2.0),
                             currentY - ((CITY_SIZE_FACTOR * effectiveHeight) / 2.0),
                             (CITY_SIZE_FACTOR * effectiveHeight), (CITY_SIZE_FACTOR * effectiveHeight),
                             (CITY_SIZE_FACTOR * effectiveHeight) / 2.0, (CITY_SIZE_FACTOR * effectiveHeight) / 2.0);
    }

    /**
     * drawGameMap Method
     * <p>
     * This method draws the game map represented in the given GameMapManagement on the given canvas
     * This method is the only one that ever needs to be accessed from outside this interface.
     *
     * @param gameMapManagement A GameMapManagement providing the game map to draw
     * @param canvas            A canvas to draw on
     * @param resourceBundle    The ResourceBundle used for internationalisation
     */
    default void drawGameMap(IGameMapManagement gameMapManagement, Canvas canvas, ResourceBundle resourceBundle) {
        LOG.debug("Drawing Game map");
        resourceBundleWrapper.set(resourceBundle);
        double width = canvas.getWidth(), height = canvas.getHeight() - OFFSET_Y * 2;
        GraphicsContext mapCtx = canvas.getGraphicsContext2D();

        //Sets an effectiveHeight depending on the height and width of the game map
        double effectiveHeight = (HEX_WIDTH_FACTOR * height * 7 < width) ? height :
                                 (width - OFFSET_X * 2.0) / (7 * HEX_HEIGHT_FACTOR * (Math.sqrt(3) / 2));

        //Get hexes, intersections, and edges in a usable format from the gameMapManagement
        IGameHex[][] hexes = gameMapManagement.getHexesAsJaggedArray();
        IIntersection[][] intersections = gameMapManagement.getIntersectionsAsJaggedArray();
        IEdge[][] edges = gameMapManagement.getEdgesAsJaggedArrayWithNullFiller();

        //Call functions to draw hexes, intersections, and edges
        drawHexTiles(hexes, effectiveHeight, mapCtx);
        drawIntersectionsAndEdges(intersections, edges, effectiveHeight, mapCtx);
    }

    /**
     * drawHarbor method
     * <p>
     * This method draws a harbor at the given coordinate in the correct orientation
     * together with a text indicating which resource the harbor harbors.
     *
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param hex             The harbor hex with the information about the harbor
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void drawHarbor(double currentX, double currentY, IHarborHex hex, double effectiveHeight,
                            GraphicsContext mapCtx) {
        mapCtx.setStroke(HARBOR_COLOUR);
        mapCtx.setFill(HARBOR_COLOUR);
        mapCtx.setLineWidth((HEX_WIDTH_FACTOR * effectiveHeight) / 5.0);
        double yDistance = (HEX_HEIGHT_FACTOR * effectiveHeight) * (1.0 / 64.0);
        double yExtend = (HEX_HEIGHT_FACTOR * effectiveHeight) * (5.0 / 32.0);
        double xDistance = (HEX_WIDTH_FACTOR * effectiveHeight) * (1.0 / 32.0);
        double xExtend = (HEX_WIDTH_FACTOR * effectiveHeight) * (5.0 / 16.0);
        double[] xCords, yCords;
        switch (hex.getSide()) {
            case WEST:
                xCords = new double[]{currentX + xDistance, currentX + xExtend / 2.0, currentX + xExtend / 2.0,
                                      currentX + xDistance,};
                yCords = new double[]{currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (1.0 / 4.0),
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (1.0 / 4.0) + yExtend,
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (3.0 / 4.0) - yExtend,
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (3.0 / 4.0),};
                break;
            case NORTHWEST:
                xCords = new double[]{currentX + xDistance, currentX + xExtend,
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) / 2.0,
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) / 2.0,};
                yCords = new double[]{currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (1.0 / 4.0),
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (1.0 / 4.0),
                                      currentY + yExtend, currentY + yDistance,};
                break;
            case NORTHEAST:
                xCords = new double[]{currentX + (HEX_WIDTH_FACTOR * effectiveHeight) * (1.0 / 2.0),
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) * (1.0 / 2.0),
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) - xExtend,
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) - xDistance,};
                yCords = new double[]{currentY + yDistance, currentY + yExtend,
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (1.0 / 4.0),
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (1.0 / 4.0),};
                break;
            case EAST:
                xCords = new double[]{currentX + (HEX_WIDTH_FACTOR * effectiveHeight) - xDistance,
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) - xExtend / 2.0,
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) - xExtend / 2.0,
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) - xDistance,};
                yCords = new double[]{currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (1.0 / 4.0),
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (1.0 / 4.0) + yExtend,
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (3.0 / 4.0) - yExtend,
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (3.0 / 4.0),};
                break;
            case SOUTHEAST:
                xCords = new double[]{currentX + (HEX_WIDTH_FACTOR * effectiveHeight) - xDistance,
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) - xExtend,
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) * (1.0 / 2.0),
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) * (1.0 / 2.0)};
                yCords = new double[]{currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (3.0 / 4.0),
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (3.0 / 4.0),
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) - yExtend,
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) - yDistance,};
                break;
            case SOUTHWEST:
                xCords = new double[]{currentX + xDistance, currentX + xExtend,
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) * (1.0 / 2.0),
                                      currentX + (HEX_WIDTH_FACTOR * effectiveHeight) * (1.0 / 2.0)};
                yCords = new double[]{currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (3.0 / 4.0),
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (3.0 / 4.0),
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) - yExtend,
                                      currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) - yDistance,};
                break;
            default:
                xCords = null;
                yCords = null;
        }
        mapCtx.fillPolygon(xCords, yCords, 4);

        String text = "";
        switch (hex.getResource()) {
            case BRICK:
                text = resourceBundleWrapper.get().getString("game.resources.brick");
                break;
            case LUMBER:
                text = resourceBundleWrapper.get().getString("game.resources.lumber");
                break;
            case ORE:
                text = resourceBundleWrapper.get().getString("game.resources.ore");
                break;
            case GRAIN:
                text = resourceBundleWrapper.get().getString("game.resources.grain");
                break;
            case WOOL:
                text = resourceBundleWrapper.get().getString("game.resources.wool");
                break;
            case ANY:
                text = resourceBundleWrapper.get().getString("game.resources.any");
                break;
        }
        mapCtx.setFill(TEXT_COLOUR);
        mapCtx.fillText(text, currentX + (HEX_WIDTH_FACTOR * effectiveHeight) / 8.0,
                        currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (4.0 / 8.0),
                        (HEX_WIDTH_FACTOR * effectiveHeight) * (6.0 / 8.0));
    }

    /**
     * drawHex method
     * <p>
     * This Method draws a hexagon at the given coordinates.
     *
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void drawHex(double currentX, double currentY, double effectiveHeight, GraphicsContext mapCtx) {
        double[] xCords = {currentX, currentX + (HEX_WIDTH_FACTOR * effectiveHeight) / 2,
                           currentX + (HEX_WIDTH_FACTOR * effectiveHeight),
                           currentX + (HEX_WIDTH_FACTOR * effectiveHeight),
                           currentX + (HEX_WIDTH_FACTOR * effectiveHeight) / 2, currentX};
        double[] yCords = {currentY + ((HEX_HEIGHT_FACTOR * effectiveHeight) / 4), currentY,
                           currentY + ((HEX_HEIGHT_FACTOR * effectiveHeight) / 4),
                           currentY + ((HEX_HEIGHT_FACTOR * effectiveHeight) / 4) * 3,
                           currentY + (HEX_HEIGHT_FACTOR * effectiveHeight),
                           currentY + ((HEX_HEIGHT_FACTOR * effectiveHeight) / 4) * 3};
        mapCtx.fillPolygon(xCords, yCords, 6);
        mapCtx.setStroke(BORDER_COLOUR);
        mapCtx.setLineWidth(2);
        mapCtx.strokePolygon(xCords, yCords, 6);
    }

    /**
     * drawHexTiles method
     * <p>
     * This method draws the hex tiles when given an array of hexes, a height, and a GraphicsContext.
     *
     * @param hexes           An array of hex tiles
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void drawHexTiles(IGameHex[][] hexes, double effectiveHeight, GraphicsContext mapCtx) {
        double currentY = OFFSET_Y;
        for (IGameHex[] hex : hexes) {
            double currentX = OFFSET_X;
            //Set the indentation for the current row of hex tiles
            if (hex.length % 2 == 0) { //Row with an even amount of hex tiles
                currentX += (HEX_WIDTH_FACTOR * effectiveHeight) / 2.0;
                currentX += ((hexes[hexes.length / 2].length - 1 - hex.length) / 2.0) * (HEX_WIDTH_FACTOR * effectiveHeight);
            } else {//Row with an odd amount of hex tiles
                currentX += ((hexes[hexes.length / 2].length - hex.length) / 2.0) * (HEX_WIDTH_FACTOR * effectiveHeight);
            }

            for (IGameHex iGameHex : hex) {
                renderHex(iGameHex, currentX, currentY, effectiveHeight, mapCtx);
                currentX += (HEX_WIDTH_FACTOR * effectiveHeight);
            }
            currentY += ((HEX_HEIGHT_FACTOR * effectiveHeight) / 4) * 3;
        }
    }

    /**
     * drawIntersectionsAndEdges method
     * <p>
     * This Method draws the intersections and edges.
     *
     * @param intersections   An array containing all intersections
     * @param edges           An array containing all edges
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void drawIntersectionsAndEdges(IIntersection[][] intersections, IEdge[][] edges, double effectiveHeight,
                                           GraphicsContext mapCtx) {
        goThroughHalfMap(true, intersections, edges, effectiveHeight, mapCtx);
        goThroughHalfMap(false, intersections, edges, effectiveHeight, mapCtx);
    }

    /**
     * drawRobber method
     * <p>
     * This method draws a robber at the given coordinates.
     *
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void drawRobber(double currentX, double currentY, double effectiveHeight, GraphicsContext mapCtx) {
        mapCtx.setLineWidth((ROBBER_LINE_WIDTH_FACTOR * effectiveHeight));
        mapCtx.setStroke(ROBBER_COLOUR);
        mapCtx.strokePolygon(new double[]{currentX + (HEX_WIDTH_FACTOR * effectiveHeight) * (1.0 / 4.0),
                                          currentX + (HEX_WIDTH_FACTOR * effectiveHeight) * (3.0 / 4.0),
                                          currentX + (HEX_WIDTH_FACTOR * effectiveHeight) * (2.0 / 4.0)},
                             new double[]{currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (2.75 / 4.0),
                                          currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (2.75 / 4.0),
                                          currentY + (HEX_HEIGHT_FACTOR * effectiveHeight) * (1.125 / 4.0)}, 3);
    }

    /**
     * drawSettlement method
     * <p>
     * This method draws a settlement at the given coordinates.
     *
     * @param owner           Needed to access the color of player
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void drawSettlement(Player owner, double currentX, double currentY, double effectiveHeight,
                                GraphicsContext mapCtx) {
        mapCtx.setFill(getPlayerColour(owner));
        mapCtx.fillOval(currentX - ((SETTLEMENT_SIZE_FACTOR * effectiveHeight) / 2.0),
                        currentY - ((SETTLEMENT_SIZE_FACTOR * effectiveHeight) / 2.0),
                        (SETTLEMENT_SIZE_FACTOR * effectiveHeight), (SETTLEMENT_SIZE_FACTOR * effectiveHeight));
    }

    private void drawToken(int token, double currentX, double currentY, double effectiveHeight,
                           GraphicsContext mapCtx) {
        mapCtx.setFill(TOKEN_COLOUR);
        double xPos = currentX + ((HEX_WIDTH_FACTOR * effectiveHeight) - (TOKEN_SIZE_FACTOR * effectiveHeight)) / 2.0;
        double yPos = currentY + ((HEX_HEIGHT_FACTOR * effectiveHeight) - (TOKEN_SIZE_FACTOR * effectiveHeight)) / 2.0;
        mapCtx.fillOval(xPos, yPos, (TOKEN_SIZE_FACTOR * effectiveHeight), (TOKEN_SIZE_FACTOR * effectiveHeight));
        mapCtx.setFill(TEXT_COLOUR);
        mapCtx.fillText(resourceBundleWrapper.get().getString("game.token." + token),
                        xPos + (TOKEN_SIZE_FACTOR * effectiveHeight) * (1.0 / 4.0),
                        yPos + (TOKEN_SIZE_FACTOR * effectiveHeight) * (3.0 / 4.0),
                        (TOKEN_SIZE_FACTOR * effectiveHeight) / 2.0);
    }

    /**
     * getPlayerColour method
     * <p>
     * This method gets the colour of the indicated player.
     *
     * @param player Indicates a player
     *
     * @return The colour of the indicated player
     */
    private Color getPlayerColour(Player player) {
        switch (player) {
            case PLAYER_1:
                return PLAYER_1_COLOUR;
            case PLAYER_2:
                return PLAYER_2_COLOUR;
            case PLAYER_3:
                return PLAYER_3_COLOUR;
            case PLAYER_4:
                return PLAYER_4_COLOUR;
        }
        return null;
    }

    /**
     * goThroughHalfMap method
     * <p>
     * This methods is called by drawIntersectionsAndEdges to draw all intersections and edges in the top or bottom
     * half of the map.
     *
     * @param topHalf         A boolean indicating which half of the map needs to be drawn on
     * @param intersections   An array containing all intersections
     * @param edges           An array containing all edges
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void goThroughHalfMap(boolean topHalf, IIntersection[][] intersections, IEdge[][] edges,
                                  double effectiveHeight, GraphicsContext mapCtx) {
        //Sets currentY depending on topHalf
        double currentY = ((topHalf) ? ((HEX_HEIGHT_FACTOR * effectiveHeight) * (3.0 / 4.0)) :
                           ((effectiveHeight / 2) + ((HEX_HEIGHT_FACTOR * effectiveHeight) / 4))) + OFFSET_Y;
        //Goes through all rows in the current half of the game map
        for (int y = ((topHalf) ? 0 : intersections.length / 2); y < ((topHalf) ? intersections.length / 2 :
                                                                      intersections.length); y++) {
            double rowStartX = ((intersections[intersections.length / 2].length - intersections[y].length) / 4.0) * (HEX_WIDTH_FACTOR * effectiveHeight);
            double currentX = OFFSET_X + rowStartX + (HEX_WIDTH_FACTOR * effectiveHeight);
            if (topHalf) currentX += (HEX_WIDTH_FACTOR * effectiveHeight) / 2.0;
            goThroughSubRow(topHalf, false, currentX, currentY, intersections[y], edges[y], effectiveHeight, mapCtx);

            currentX = OFFSET_X + rowStartX + (HEX_WIDTH_FACTOR * effectiveHeight);
            if (!topHalf) currentX += (HEX_WIDTH_FACTOR * effectiveHeight) / 2.0;
            currentY += ((HEX_HEIGHT_FACTOR * effectiveHeight) / 4.0);
            goThroughSubRow(!topHalf, true, currentX, currentY, intersections[y], edges[y], effectiveHeight, mapCtx);
            currentY += (HEX_HEIGHT_FACTOR * effectiveHeight) / 2.0;
        }
    }

    /**
     * goThroughSubRow method
     * <p>
     * This method is called by goThroughHalfMap to draw all intersections and optionally all edges in a given sub row.
     * Every Row is separated in to two sub row which have slightly different y-coordinates.
     *
     * @param firstSubRow     Used to indicate which sub row should be accessed
     * @param renderEdges     Used to indicate whether edges should be drawn
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param intersections   An array containing all intersections
     * @param edges           An array containing all edges
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void goThroughSubRow(boolean firstSubRow, boolean renderEdges, double currentX, double currentY,
                                 IIntersection[] intersections, IEdge[] edges, double effectiveHeight,
                                 GraphicsContext mapCtx) {
        for (int x = firstSubRow ? 1 : 0, xEdges = 0; x < intersections.length; x = x + 2, xEdges = xEdges + 3) {
            if (renderEdges) {
                renderEdges(currentX, currentY, Arrays.copyOfRange(edges, xEdges, xEdges + 3), effectiveHeight, mapCtx);
            }
            renderIntersection(currentX, currentY, intersections[x], effectiveHeight, mapCtx);
            currentX += (HEX_WIDTH_FACTOR * effectiveHeight);
        }
    }

    /**
     * renderEdges method
     * <p>
     * This Method draws the 3 edges around an intersection at the given coordinates.
     *
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param edges           An array containing all edges
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void renderEdges(double currentX, double currentY, IEdge[] edges, double effectiveHeight,
                             GraphicsContext mapCtx) {
        mapCtx.setLineWidth(ROAD_WIDTH_FACTOR * effectiveHeight);

        //Northwest road
        if (edges[0] != null && edges[0].getOwner() != null) {
            mapCtx.setStroke(getPlayerColour(edges[0].getOwner()));
            mapCtx.strokeLine(currentX, currentY, currentX - ((HEX_WIDTH_FACTOR * effectiveHeight) / 2.0),
                              currentY - ((HEX_HEIGHT_FACTOR * effectiveHeight) / 4.0));
        }

        //Northeast road
        if (edges[1] != null && edges[1].getOwner() != null) {
            mapCtx.setStroke(getPlayerColour(edges[1].getOwner()));
            mapCtx.strokeLine(currentX, currentY, currentX, currentY + ((HEX_HEIGHT_FACTOR * effectiveHeight) / 2.0));
        }

        //South road
        if (edges[2] != null && edges[2].getOwner() != null) {
            mapCtx.setStroke(getPlayerColour(edges[2].getOwner()));
            mapCtx.strokeLine(currentX, currentY, currentX + ((HEX_WIDTH_FACTOR * effectiveHeight) / 2.0),
                              currentY - ((HEX_HEIGHT_FACTOR * effectiveHeight) / 4.0));
        }
    }

    /**
     * renderHex method
     * <p>
     * This Method draws the given hex at the given coordinates.
     *
     * @param hex             The hex tile that needs to be drawn
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void renderHex(IGameHex hex, double currentX, double currentY, double effectiveHeight,
                           GraphicsContext mapCtx) {
        if (!setHexColour(hex, mapCtx)) return;

        drawHex(currentX, currentY, effectiveHeight, mapCtx);

        if (hex.isRobberOnField()) drawRobber(currentX, currentY, effectiveHeight, mapCtx);
        if (hex instanceof IResourceHex)
            drawToken(((IResourceHex) hex).getToken(), currentX, currentY, effectiveHeight, mapCtx);

        if (hex instanceof IHarborHex) drawHarbor(currentX, currentY, (IHarborHex) hex, effectiveHeight, mapCtx);
    }

    /**
     * renderIntersection method
     * <p>
     * This method renders an intersection.
     * It draws a settlement or a city on it when the intersection is marked as such.
     *
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param intersection    The intersection to draw
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void renderIntersection(double currentX, double currentY, IIntersection intersection,
                                    double effectiveHeight, GraphicsContext mapCtx) {
        switch (intersection.getState()) {
            case FREE:
                //Free intersections don't need to be marked, but it could easily be added here
                break;
            case BLOCKED:
                //Blocked intersections don't need to be marked, but it could easily be added here
                break;
            case SETTLEMENT:
                drawSettlement(intersection.getOwner(), currentX, currentY, effectiveHeight, mapCtx);
                break;
            case CITY:
                drawCity(intersection.getOwner(), currentX, currentY, effectiveHeight, mapCtx);
                break;
        }
    }

    /**
     * setHexColour Method
     * <p>
     * This method sets the colour according to a hex tile.
     *
     * @param hex    The hex tile the colour should be set accordingly to
     * @param mapCtx A GraphicsContext needed to set the colour
     *
     * @return True if the colour couldn't be set, false otherwise
     */
    private boolean setHexColour(IGameHex hex, GraphicsContext mapCtx) {
        switch (hex.getType()) {
            case WATER:
            case HARBOR:
                mapCtx.setFill(WATER_COLOUR);
                break;
            case DESERT:
                mapCtx.setFill(DESERT_COLOUR);
                break;
            case RESOURCE:
                switch (((IResourceHex) hex).getResource()) {
                    case HILLS:
                        mapCtx.setFill(HILLS_COLOUR);
                        break;
                    case FOREST:
                        mapCtx.setFill(FOREST_COLOUR);
                        break;
                    case MOUNTAINS:
                        mapCtx.setFill(MOUNTAINS_COLOUR);
                        break;
                    case FIELDS:
                        mapCtx.setFill(FIELDS_COLOUR);
                        break;
                    case PASTURE:
                        mapCtx.setFill(PASTURE_COLOUR);
                        break;
                    default:
                        return false;
                }
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * ResourceBundleWrapper
     * This class is a wrapper for the ResourceBundle to be able to access it like an attribute.
     * Using a wrapper class it can be set after declaration even if it is "static final".
     *
     * @since 2021-02-05
     */
    class ResourceBundleWrapper {

        private ResourceBundle resourceBundle = null;

        public ResourceBundleWrapper() {
        }

        public ResourceBundle get() {
            return resourceBundle;
        }

        public void set(ResourceBundle resourceBundle) {
            this.resourceBundle = resourceBundle;
        }
    }
}
