package de.uol.swp.client;

import com.google.inject.Inject;
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
 * GameRendering Class
 * <p>
 * Class that can be used to draw a game map on a canvas.
 * This provides one public method drawGameMap that can be called
 * to draw a given game map on a canvas given to the constructor on instantiation.
 *
 * @author Timo Gerken
 * @author Temmo Junkhoff
 * @implNote No methods of this interface need to be implemented
 * @see de.uol.swp.common.game.map.GameMapManagement
 * @see javafx.scene.canvas.Canvas
 * @since 2021-01-31
 */
public class GameRendering {

    public static final Color PLAYER_1_COLOUR = Color.BLUE;
    public static final Color PLAYER_2_COLOUR = Color.RED;
    public static final Color PLAYER_3_COLOUR = Color.PURPLE;
    public static final Color PLAYER_4_COLOUR = Color.WHITE;
    //Constants used for the colours
    private static final Color TOKEN_COLOUR = Color.BEIGE;
    private static final Color TEXT_COLOUR = Color.BLACK;
    private static final Color BORDER_COLOUR = Color.BLACK;
    private static final Color ROBBER_COLOUR = Color.BLACK;
    private static final Color HARBOR_COLOUR = Color.SLATEGREY;
    private static final Color WATER_COLOUR = Color.CORNFLOWERBLUE;
    private static final Color DESERT_COLOUR = Color.WHITE;
    private static final Color HILLS_COLOUR = Color.DARKORANGE;
    private static final Color FOREST_COLOUR = Color.DARKGREEN;
    private static final Color MOUNTAINS_COLOUR = Color.DARKGREY;
    private static final Color FIELDS_COLOUR = Color.YELLOW;
    private static final Color PASTURE_COLOUR = Color.LIGHTGREEN;
    @Inject
    private static ResourceBundle resourceBundle;

    private final double OFFSET_Y = 3.0, OFFSET_X = 3.0;
    private final double hexHeight, hexWidth, settlementSize, citySize;
    private final double roadWidth, robberLineWidth, tokenSize, effectiveHeight, effectiveWidth;
    private final GraphicsContext gfxCtx;

    Logger LOG = LogManager.getLogger(GameRendering.class);

    /**
     * Constructor
     *
     * @param canvas The canvas that should be drawn on
     */
    public GameRendering(Canvas canvas) {
        double WIDTH_FACTOR = (Math.sqrt(3) / 2);
        double HEX_HEIGHT_FACTOR = 1.0 / 5.5;
        double HEX_WIDTH_FACTOR = HEX_HEIGHT_FACTOR * WIDTH_FACTOR;

        this.gfxCtx = canvas.getGraphicsContext2D();
        double width = canvas.getWidth(), height = canvas.getHeight() - OFFSET_Y * 2;
        //Sets an effectiveHeight depending on the height and width of the game map
        this.effectiveHeight = (HEX_WIDTH_FACTOR * height * 7 < width) ? height :
                               (width - OFFSET_X * 2.0) / (7 * HEX_HEIGHT_FACTOR * (Math.sqrt(3) / 2));

        this.effectiveWidth = (Math.sqrt(3) / 2) * effectiveHeight;
        this.hexHeight = 1.0 / 5.5 * effectiveHeight;
        this.hexWidth = (Math.sqrt(3) / 2) * hexHeight;
        this.settlementSize = hexHeight / 4.0;
        this.citySize = settlementSize * 1.25;
        this.roadWidth = settlementSize / 2.0;
        this.robberLineWidth = roadWidth / 2.0;
        this.tokenSize = hexHeight / 3.0;
    }

    /**
     * drawCity method
     * <p>
     * This method draws a city at the given coordinates.
     *
     * @param owner    Needed to access the color of player
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     */
    private void drawCity(Player owner, double currentX, double currentY) {
        gfxCtx.setFill(getPlayerColour(owner));
        gfxCtx.fillRoundRect(currentX - (citySize / 2.0), currentY - (citySize / 2.0), citySize, citySize,
                             citySize / 2.0, citySize / 2.0);
    }

    /**
     * drawDices method
     * <p>
     * This method draws two dices
     */
    public void drawDices(int dice1, int dice2) {
        //TODO make a functioning method to draw two dices
    }

    /**
     * drawGameMap Method
     * <p>
     * This method draws the game map represented in the given GameMapManagement on the given canvas
     * This method is the only one that ever needs to be accessed from outside this interface.
     *
     * @param gameMapManagement A GameMapManagement providing the game map to draw
     */
    public void drawGameMap(IGameMapManagement gameMapManagement) {
        LOG.debug("Drawing Game map");

        //Get hexes, intersections, and edges in a usable format from the gameMapManagement
        IGameHex[][] hexes = gameMapManagement.getHexesAsJaggedArray();
        IIntersection[][] intersections = gameMapManagement.getIntersectionsAsJaggedArray();
        IEdge[][] edges = gameMapManagement.getEdgesAsJaggedArrayWithNullFiller();

        //Call functions to draw hexes, intersections, and edges
        drawHexTiles(hexes);
        drawIntersectionsAndEdges(intersections, edges);
    }

    /**
     * drawHarbor method
     * <p>
     * This method draws a harbor at the given coordinate in the correct orientation
     * together with a text indicating which resource the harbor harbors.
     *
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     * @param hex      The harbor hex with the information about the harbor
     */
    private void drawHarbor(double currentX, double currentY, IHarborHex hex) {
        gfxCtx.setStroke(HARBOR_COLOUR);
        gfxCtx.setFill(HARBOR_COLOUR);
        gfxCtx.setLineWidth(hexWidth / 5.0);
        double yDistance = hexHeight * (1.0 / 64.0);
        double yExtend = hexHeight * (5.0 / 32.0);
        double xDistance = hexWidth * (1.0 / 32.0);
        double xExtend = hexWidth * (5.0 / 16.0);
        double[] xCords, yCords;
        switch (hex.getSide()) {
            case WEST:
                xCords = new double[]{currentX + xDistance, currentX + xExtend / 2.0, currentX + xExtend / 2.0,
                                      currentX + xDistance,};
                yCords = new double[]{currentY + hexHeight * (1.0 / 4.0), currentY + hexHeight * (1.0 / 4.0) + yExtend,
                                      currentY + hexHeight * (3.0 / 4.0) - yExtend,
                                      currentY + hexHeight * (3.0 / 4.0),};
                break;
            case NORTHWEST:
                xCords = new double[]{currentX + xDistance, currentX + xExtend, currentX + hexWidth / 2.0,
                                      currentX + hexWidth / 2.0,};
                yCords = new double[]{currentY + hexHeight * (1.0 / 4.0), currentY + hexHeight * (1.0 / 4.0),
                                      currentY + yExtend, currentY + yDistance,};
                break;
            case NORTHEAST:
                xCords = new double[]{currentX + hexWidth * (1.0 / 2.0), currentX + hexWidth * (1.0 / 2.0),
                                      currentX + hexWidth - xExtend, currentX + hexWidth - xDistance,};
                yCords = new double[]{currentY + yDistance, currentY + yExtend, currentY + hexHeight * (1.0 / 4.0),
                                      currentY + hexHeight * (1.0 / 4.0),};
                break;
            case EAST:
                xCords = new double[]{currentX + hexWidth - xDistance, currentX + hexWidth - xExtend / 2.0,
                                      currentX + hexWidth - xExtend / 2.0, currentX + hexWidth - xDistance,};
                yCords = new double[]{currentY + hexHeight * (1.0 / 4.0), currentY + hexHeight * (1.0 / 4.0) + yExtend,
                                      currentY + hexHeight * (3.0 / 4.0) - yExtend,
                                      currentY + hexHeight * (3.0 / 4.0),};
                break;
            case SOUTHEAST:
                xCords = new double[]{currentX + hexWidth - xDistance, currentX + hexWidth - xExtend,
                                      currentX + hexWidth * (1.0 / 2.0), currentX + hexWidth * (1.0 / 2.0)};
                yCords = new double[]{currentY + hexHeight * (3.0 / 4.0), currentY + hexHeight * (3.0 / 4.0),
                                      currentY + hexHeight - yExtend, currentY + hexHeight - yDistance,};
                break;
            case SOUTHWEST:
                xCords = new double[]{currentX + xDistance, currentX + xExtend, currentX + hexWidth * (1.0 / 2.0),
                                      currentX + hexWidth * (1.0 / 2.0)};
                yCords = new double[]{currentY + hexHeight * (3.0 / 4.0), currentY + hexHeight * (3.0 / 4.0),
                                      currentY + hexHeight - yExtend, currentY + hexHeight - yDistance,};
                break;
            default:
                xCords = null;
                yCords = null;
        }
        gfxCtx.fillPolygon(xCords, yCords, 4);

        String text = "";
        switch (hex.getResource()) {
            case BRICK:
                text = resourceBundle.getString("game.resources.brick");
                break;
            case LUMBER:
                text = resourceBundle.getString("game.resources.lumber");
                break;
            case ORE:
                text = resourceBundle.getString("game.resources.ore");
                break;
            case GRAIN:
                text = resourceBundle.getString("game.resources.grain");
                break;
            case WOOL:
                text = resourceBundle.getString("game.resources.wool");
                break;
            case ANY:
                text = resourceBundle.getString("game.resources.any");
                break;
        }
        gfxCtx.setFill(TEXT_COLOUR);
        gfxCtx.fillText(text, currentX + hexWidth / 8.0, currentY + hexHeight * (4.0 / 8.0), hexWidth * (6.0 / 8.0));
    }

    /**
     * drawHex method
     * <p>
     * This Method draws a hexagon at the given coordinates.
     *
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     */
    private void drawHex(double currentX, double currentY) {
        double[] xCords = {currentX, currentX + hexWidth / 2, currentX + hexWidth, currentX + hexWidth,
                           currentX + hexWidth / 2, currentX};
        double[] yCords = {currentY + (hexHeight / 4), currentY, currentY + (hexHeight / 4),
                           currentY + (hexHeight / 4) * 3, currentY + hexHeight, currentY + (hexHeight / 4) * 3};
        gfxCtx.fillPolygon(xCords, yCords, 6);
        gfxCtx.setStroke(BORDER_COLOUR);
        gfxCtx.setLineWidth(2);
        gfxCtx.strokePolygon(xCords, yCords, 6);
    }

    /**
     * drawHexTiles method
     * <p>
     * This method draws the hex tiles when given an array of hexes, a height, and a GraphicsContext.
     *
     * @param hexes An array of hex tiles
     */
    private void drawHexTiles(IGameHex[][] hexes) {
        double currentY = OFFSET_Y;
        for (IGameHex[] hex : hexes) {
            double currentX = OFFSET_X;
            //Set the indentation for the current row of hex tiles
            if (hex.length % 2 == 0) { //Row with an even amount of hex tiles
                currentX += hexWidth / 2.0;
                currentX += ((hexes[hexes.length / 2].length - 1 - hex.length) / 2.0) * hexWidth;
            } else {//Row with an odd amount of hex tiles
                currentX += ((hexes[hexes.length / 2].length - hex.length) / 2.0) * hexWidth;
            }

            for (IGameHex iGameHex : hex) {
                renderHex(iGameHex, currentX, currentY);
                currentX += hexWidth;
            }
            currentY += (hexHeight / 4) * 3;
        }
    }

    /**
     * drawIntersectionsAndEdges method
     * <p>
     * This Method draws the intersections and edges.
     *
     * @param intersections An array containing all intersections
     * @param edges         An array containing all edges
     */
    private void drawIntersectionsAndEdges(IIntersection[][] intersections, IEdge[][] edges) {
        goThroughHalfMap(true, intersections, edges);
        goThroughHalfMap(false, intersections, edges);
    }

    /**
     * drawRobber method
     * <p>
     * This method draws a robber at the given coordinates.
     *
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     */
    private void drawRobber(double currentX, double currentY) {
        gfxCtx.setLineWidth(robberLineWidth);
        gfxCtx.setStroke(ROBBER_COLOUR);
        gfxCtx.strokePolygon(new double[]{currentX + hexWidth * (1.0 / 4.0), currentX + hexWidth * (3.0 / 4.0),
                                          currentX + hexWidth * (2.0 / 4.0)},
                             new double[]{currentY + hexHeight * (2.75 / 4.0), currentY + hexHeight * (2.75 / 4.0),
                                          currentY + hexHeight * (1.125 / 4.0)}, 3);
    }

    /**
     * drawSettlement method
     * <p>
     * This method draws a settlement at the given coordinates.
     *
     * @param owner    Needed to access the color of player
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     */
    private void drawSettlement(Player owner, double currentX, double currentY) {
        gfxCtx.setFill(getPlayerColour(owner));
        gfxCtx.fillOval(currentX - (settlementSize / 2.0), currentY - (settlementSize / 2.0), settlementSize,
                        settlementSize);
    }

    /**
     * drawToken method
     * This method draws the token of a hex at the given coordinates.
     *
     * @param token    The value of the token that should be drawn
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     */
    private void drawToken(int token, double currentX, double currentY) {
        gfxCtx.setFill(TOKEN_COLOUR);
        double xPos = currentX + (hexWidth - tokenSize) / 2.0;
        double yPos = currentY + (hexHeight - tokenSize) / 2.0;
        gfxCtx.fillOval(xPos, yPos, tokenSize, tokenSize);
        gfxCtx.setFill(TEXT_COLOUR);
        gfxCtx.fillText(resourceBundle.getString("game.token." + token), xPos + tokenSize * (1.0 / 4.0),
                        yPos + tokenSize * (3.0 / 4.0), tokenSize / 2.0);
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
     * @param topHalf       A boolean indicating which half of the map needs to be drawn on
     * @param intersections An array containing all intersections
     * @param edges         An array containing all edges
     */
    private void goThroughHalfMap(boolean topHalf, IIntersection[][] intersections, IEdge[][] edges) {
        //Sets currentY depending on topHalf
        double currentY = ((topHalf) ? (hexHeight * (3.0 / 4.0)) :
                           ((effectiveHeight / 2) + (hexHeight / 4))) + OFFSET_Y;
        //Goes through all rows in the current half of the game map
        for (int y = ((topHalf) ? 0 : intersections.length / 2); y < ((topHalf) ? intersections.length / 2 :
                                                                      intersections.length); y++) {
            double rowStartX = ((intersections[intersections.length / 2].length - intersections[y].length) / 4.0) * hexWidth;
            double currentX = OFFSET_X + rowStartX + hexWidth;
            if (topHalf) currentX += hexWidth / 2.0;
            goThroughSubRow(topHalf, false, currentX, currentY, intersections[y], edges[y]);

            currentX = OFFSET_X + rowStartX + hexWidth;
            if (!topHalf) currentX += hexWidth / 2.0;
            currentY += (hexHeight / 4.0);
            goThroughSubRow(!topHalf, true, currentX, currentY, intersections[y], edges[y]);
            currentY += hexHeight / 2.0;
        }
    }

    /**
     * goThroughSubRow method
     * <p>
     * This method is called by goThroughHalfMap to draw all intersections and optionally all edges in a given sub row.
     * Every Row is separated in to two sub row which have slightly different y-coordinates.
     *
     * @param firstSubRow   Used to indicate which sub row should be accessed
     * @param renderEdges   Used to indicate whether edges should be drawn
     * @param currentX      The current x-coordinate
     * @param currentY      The current y-coordinate
     * @param intersections An array containing all intersections
     * @param edges         An array containing all edges
     */
    private void goThroughSubRow(boolean firstSubRow, boolean renderEdges, double currentX, double currentY,
                                 IIntersection[] intersections, IEdge[] edges) {
        for (int x = firstSubRow ? 1 : 0, xEdges = 0; x < intersections.length; x = x + 2, xEdges = xEdges + 3) {
            if (renderEdges) {
                renderEdges(currentX, currentY, Arrays.copyOfRange(edges, xEdges, xEdges + 3));
            }
            renderIntersection(currentX, currentY, intersections[x]);
            currentX += hexWidth;
        }
    }

    /**
     * renderEdges method
     * <p>
     * This Method draws the 3 edges around an intersection at the given coordinates.
     *
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     * @param edges    An array containing all edges
     */
    private void renderEdges(double currentX, double currentY, IEdge[] edges) {
        gfxCtx.setLineWidth(roadWidth);

        //Northwest road
        if (edges[0] != null && edges[0].getOwner() != null) {
            gfxCtx.setStroke(getPlayerColour(edges[0].getOwner()));
            gfxCtx.strokeLine(currentX, currentY, currentX - (hexWidth / 2.0), currentY - (hexHeight / 4.0));
        }

        //Northeast road
        if (edges[1] != null && edges[1].getOwner() != null) {
            gfxCtx.setStroke(getPlayerColour(edges[1].getOwner()));
            gfxCtx.strokeLine(currentX, currentY, currentX, currentY + (hexHeight / 2.0));
        }

        //South road
        if (edges[2] != null && edges[2].getOwner() != null) {
            gfxCtx.setStroke(getPlayerColour(edges[2].getOwner()));
            gfxCtx.strokeLine(currentX, currentY, currentX + (hexWidth / 2.0), currentY - (hexHeight / 4.0));
        }
    }

    /**
     * renderHex method
     * <p>
     * This Method draws the given hex at the given coordinates.
     *
     * @param hex      The hex tile that needs to be drawn
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     */
    private void renderHex(IGameHex hex, double currentX, double currentY) {
        if (!setHexColour(hex)) return;

        drawHex(currentX, currentY);

        if (hex.isRobberOnField()) drawRobber(currentX, currentY);
        if (hex instanceof IResourceHex) drawToken(((IResourceHex) hex).getToken(), currentX, currentY);

        if (hex instanceof IHarborHex) drawHarbor(currentX, currentY, (IHarborHex) hex);
    }

    /**
     * renderIntersection method
     * <p>
     * This method renders an intersection.
     * It draws a settlement or a city on it when the intersection is marked as such.
     *
     * @param currentX     The current x-coordinate
     * @param currentY     The current y-coordinate
     * @param intersection The intersection to draw
     */
    private void renderIntersection(double currentX, double currentY, IIntersection intersection) {
        switch (intersection.getState()) {
            case FREE:
                //Free intersections don't need to be marked, but it could easily be added here
                break;
            case BLOCKED:
                //Blocked intersections don't need to be marked, but it could easily be added here
                break;
            case SETTLEMENT:
                drawSettlement(intersection.getOwner(), currentX, currentY);
                break;
            case CITY:
                drawCity(intersection.getOwner(), currentX, currentY);
                break;
        }
    }

    /**
     * setHexColour Method
     * <p>
     * This method sets the colour according to a hex tile.
     *
     * @param hex The hex tile the colour should be set accordingly to
     *
     * @return True if the colour couldn't be set, false otherwise
     */
    private boolean setHexColour(IGameHex hex) {
        switch (hex.getType()) {
            case WATER:
            case HARBOR:
                gfxCtx.setFill(WATER_COLOUR);
                break;
            case DESERT:
                gfxCtx.setFill(DESERT_COLOUR);
                break;
            case RESOURCE:
                switch (((IResourceHex) hex).getResource()) {
                    case HILLS:
                        gfxCtx.setFill(HILLS_COLOUR);
                        break;
                    case FOREST:
                        gfxCtx.setFill(FOREST_COLOUR);
                        break;
                    case MOUNTAINS:
                        gfxCtx.setFill(MOUNTAINS_COLOUR);
                        break;
                    case FIELDS:
                        gfxCtx.setFill(FIELDS_COLOUR);
                        break;
                    case PASTURE:
                        gfxCtx.setFill(PASTURE_COLOUR);
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
}
