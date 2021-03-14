package de.uol.swp.client;

import com.google.inject.Inject;
import de.uol.swp.common.game.map.*;
import de.uol.swp.common.game.map.Hexes.IGameHex;
import de.uol.swp.common.game.map.Hexes.IHarborHex;
import de.uol.swp.common.game.map.Hexes.IResourceHex;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;

import static de.uol.swp.common.game.map.BetterMapPoint.*;

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
 * @see de.uol.swp.common.game.map.GameMap
 * @see javafx.scene.canvas.Canvas
 * @since 2021-01-31
 */
public class GameRendering {

    public static final Color PLAYER_1_COLOUR = Color.BLUE;
    public static final Color PLAYER_2_COLOUR = Color.RED;
    public static final Color PLAYER_3_COLOUR = Color.rgb(255, 69, 0);
    public static final Color PLAYER_4_COLOUR = Color.rgb(255, 127, 124);
    //Constants used for the colours
    private static final Color TOKEN_COLOUR = Color.BEIGE;
    private static final Color TEXT_COLOUR = Color.BLACK;
    private static final Color BORDER_COLOUR = Color.BLACK;
    private static final Color ROBBER_COLOUR = Color.BLACK;
    private static final Color HARBOR_COLOUR = Color.rgb(80, 50, 2);
    private static final Color WATER_COLOUR = Color.CORNFLOWERBLUE;
    private static final Color DESERT_COLOUR = Color.rgb(223, 187, 22);
    private static final Color HILLS_COLOUR = Color.rgb(240, 181, 103);
    private static final Color FOREST_COLOUR = Color.rgb(79, 141, 67);
    private static final Color MOUNTAINS_COLOUR = Color.DARKGREY;
    private static final Color FIELDS_COLOUR = Color.rgb(240, 215, 103);
    private static final Color PASTURE_COLOUR = Color.rgb(197, 240, 103);
    @Inject
    private static ResourceBundle resourceBundle;

    private final double OFFSET_Y = 3.0, OFFSET_X = 3.0;
    private final double hexHeight, hexWidth, settlementSize, citySize, diceSize, diceLineWidth, diceDotSize;
    private final double roadWidth, robberLineWidth, tokenSize, effectiveHeight, effectiveWidth, width, height;
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
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
        double height = this.height - OFFSET_Y * 2;
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
        this.diceSize = effectiveHeight / 12.0;
        this.diceLineWidth = diceSize / 16.0;
        this.diceDotSize = diceSize / 4.0;
    }

    public BetterMapPoint mapClickToHex(double x, double y) {
        int row = (int) Math.floor((y - OFFSET_Y) / (hexHeight / 8));
        int col = (int) Math.floor((x - OFFSET_X) / (hexWidth / 8));
        int col8 = col % 8;
        int hexY = row / 6;
        int hexX = 0;

        //TODO: Check for click into Offset region
        //TODO: Filter out right nothing
        //TODO: Intersections (bottom half), Hexes should be working
        if (row == 0 || row == 1) { // first row
            return InvalidMapPoint();
        } else if (row == 52 || row == 51) { // last row // check if rows are correct
            return InvalidMapPoint();
        } else if ((row % 12) < 6) {// indented hex rows (0, 2, 4, 6)
            System.out.println("indented");
            switch (row % 6) {
                case 0:
                    switch (col8) {
                        case 0:
                        case 7:
                            return IntersectionMapPoint(hexY - 1, ((((col + 1) / 8) * 2) - 1));
                    }
                case 1:
                    switch (col8) {
                        case 3:
                        case 4:
                            return IntersectionMapPoint(hexY - 1, (((col - 4) / 8) - 1) * 2);
                    }
                case 2:
                case 3:
                case 4:
                case 5:
                    if (col8 == 3 || col8 == 4) { // Edge

                    } else { // Hex
                        hexX = (col - 4) / 8;
                        hexX = hexX - ((7 - getHexesInRow(hexY)) / 2);
                        return HexMapPoint(hexY, hexX);
                    }
            }
        } else {// unindented hex rows (1, 3, 5, 7)
            System.out.println("Not indented");
            switch (row % 6) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    if (col8 == 7 || col8 == 0) { // Edge

                    } else { // Hex
                        hexX = col / 8;
                        hexX = hexX - ((7 - getHexesInRow(hexY)) / 2);
                        return HexMapPoint(hexY, hexX);
                    }
            }
        }
        System.out.println("Not Caught");
        return InvalidMapPoint();
    }

    /**
     * drawDice method
     * <p>
     * This method draws the dice.
     *
     * @param die1 The first die
     * @param die2 The second die
     */
    public void drawDice(int die1, int die2) {
        gfxCtx.clearRect(OFFSET_X, OFFSET_Y, 2 * diceSize + diceSize / 8.0 + 2 * diceLineWidth,
                         diceSize + 2 * diceLineWidth);
        double startX = OFFSET_X;
        double startY = OFFSET_Y;
        renderDice(die1, startX, startY);
        startX += diceSize + diceSize / 8.0;
        renderDice(die2, startX, startY);
    }

    /**
     * drawGameMap Method
     * <p>
     * This method draws the game map represented in the given GameMapManagement on the given canvas
     * This method is the only one that ever needs to be accessed from outside this interface.
     *
     * @param gameMapManagement A GameMapManagement providing the game map to draw
     */
    public void drawGameMap(IGameMap gameMapManagement) {
        LOG.debug("Drawing Game map");

        gfxCtx.setFill(Color.BLACK);
        gfxCtx.fillRect(0, 0, width, height);

        //Get hexes, intersections, and edges in a usable format from the gameMapManagement
        IGameHex[][] hexes = gameMapManagement.getHexesAsJaggedArray();
        IIntersection[][] intersections = gameMapManagement.getIntersectionsAsJaggedArray();

        //Call functions to draw hexes, intersections, and edges
        drawHexTiles(hexes);
        drawIntersectionsAndEdges(intersections, gameMapManagement);
        gfxCtx.setStroke(Color.RED);
        gfxCtx.setLineWidth(1);
        for (double cy = OFFSET_Y; cy < height; cy += hexHeight / 8)
            gfxCtx.strokeLine(0, cy, width, cy);

        for (double cx = OFFSET_X; cx < width; cx += hexWidth / 8)
            gfxCtx.strokeLine(cx, 0, cx, height);
    }

    private int getHexesInRow(int y) {
        switch (y) {
            case 0:
            case 6:
                return 4;
            case 1:
            case 5:
                return 5;
            case 2:
            case 4:
                return 6;
            case 3:
            default:
                return 7;
        }
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
     * @param intersections     An array containing all intersections
     * @param gameMapManagement A GameMapManagement providing the game map to draw
     */
    private void drawIntersectionsAndEdges(IIntersection[][] intersections, IGameMap gameMapManagement) {
        goThroughHalfMap(true, intersections, gameMapManagement);
        goThroughHalfMap(false, intersections, gameMapManagement);
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
     * @param topHalf           A boolean indicating which half of the map needs to be drawn on
     * @param intersections     An array containing all intersections
     * @param gameMapManagement A GameMapManagement providing the game map to draw
     */
    private void goThroughHalfMap(boolean topHalf, IIntersection[][] intersections, IGameMap gameMapManagement) {

        //Sets currentY depending on topHalf
        double currentY = ((topHalf) ? (hexHeight * (3.0 / 4.0)) :
                           ((effectiveHeight / 2) + (hexHeight / 4))) + OFFSET_Y;
        //Goes through all rows in the current half of the game map
        for (int y = ((topHalf) ? 0 : intersections.length / 2); y < ((topHalf) ? intersections.length / 2 :
                                                                      intersections.length); y++) {

            double rowStartX = ((intersections[intersections.length / 2].length - intersections[y].length) / 4.0) * hexWidth;
            double currentX = OFFSET_X + rowStartX + hexWidth;
            if (topHalf) currentX += hexWidth / 2.0;
            goThroughSubRow(topHalf, false, currentX, currentY, intersections[y], gameMapManagement);

            currentX = OFFSET_X + rowStartX + hexWidth;
            if (!topHalf) currentX += hexWidth / 2.0;
            currentY += (hexHeight / 4.0);
            goThroughSubRow(!topHalf, true, currentX, currentY, intersections[y], gameMapManagement);
            currentY += hexHeight / 2.0;
        }
    }

    /**
     * goThroughSubRow method
     * <p>
     * This method is called by goThroughHalfMap to draw all intersections and optionally all edges in a given sub row.
     * Every Row is separated in to two sub row which have slightly different y-coordinates.
     *
     * @param firstSubRow       Used to indicate which sub row should be accessed
     * @param renderEdges       Used to indicate whether edges should be drawn
     * @param currentX          The current x-coordinate
     * @param currentY          The current y-coordinate
     * @param intersections     An array containing all intersections in the current row
     * @param gameMapManagement A GameMapManagement providing the game map to draw
     */
    private void goThroughSubRow(boolean firstSubRow, boolean renderEdges, double currentX, double currentY,
                                 IIntersection[] intersections, IGameMap gameMapManagement) {
        for (int x = firstSubRow ? 1 : 0; x < intersections.length; x = x + 2) {
            if (renderEdges) {
                renderEdges(currentX, currentY, intersections[x], gameMapManagement);
            }
            renderIntersection(currentX, currentY, intersections[x]);
            currentX += hexWidth;
        }
    }

    /**
     * renderDice method
     * <p>
     * This method renders a given die at the given coordinates.
     *
     * @param die      The die to be drawn
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     */
    private void renderDice(int die, double currentX, double currentY) {
        gfxCtx.setFill(Color.WHITE);
        gfxCtx.setStroke(Color.BLACK);
        gfxCtx.setLineWidth(diceLineWidth);
        gfxCtx.fillRoundRect(currentX, currentY, diceSize, diceSize, diceSize / 3.0, diceSize / 3.0);
        gfxCtx.strokeRoundRect(currentX, currentY, diceSize, diceSize, diceSize / 3.0, diceSize / 3.0);

        double middleDotPos = diceSize / 2.0 - diceDotSize / 2.0;
        double firstDotPos = diceDotSize / 2.0;
        double secondDotPos = diceSize - diceDotSize / 2.0 - diceDotSize;

        gfxCtx.setFill(Color.BLACK);
        switch (die) {
            case 1:
                gfxCtx.fillOval(currentX + middleDotPos, currentY + middleDotPos, diceDotSize, diceDotSize);
                break;
            case 2:
                gfxCtx.fillOval(currentX + secondDotPos, currentY + firstDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + firstDotPos, currentY + secondDotPos, diceDotSize, diceDotSize);
                break;
            case 3:
                gfxCtx.fillOval(currentX + middleDotPos, currentY + middleDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + secondDotPos, currentY + firstDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + firstDotPos, currentY + secondDotPos, diceDotSize, diceDotSize);
                break;
            case 4:
                gfxCtx.fillOval(currentX + secondDotPos, currentY + firstDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + firstDotPos, currentY + secondDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + secondDotPos, currentY + secondDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + firstDotPos, currentY + firstDotPos, diceDotSize, diceDotSize);
                break;
            case 5:
                gfxCtx.fillOval(currentX + middleDotPos, currentY + middleDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + secondDotPos, currentY + firstDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + firstDotPos, currentY + secondDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + secondDotPos, currentY + secondDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + firstDotPos, currentY + firstDotPos, diceDotSize, diceDotSize);
                break;
            case 6:
                gfxCtx.fillOval(currentX + secondDotPos, currentY + firstDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + firstDotPos, currentY + secondDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + secondDotPos, currentY + secondDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + firstDotPos, currentY + firstDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + firstDotPos, currentY + middleDotPos, diceDotSize, diceDotSize);
                gfxCtx.fillOval(currentX + secondDotPos, currentY + middleDotPos, diceDotSize, diceDotSize);
                break;
        }
    }

    /**
     * renderEdges method
     * <p>
     * This Method draws the 3 edges around an intersection at the given coordinates.
     *
     * @param currentX          The current x-coordinate
     * @param currentY          The current y-coordinate
     * @param gameMapManagement A GameMapManagement providing the game map to draw
     */
    private void renderEdges(double currentX, double currentY, IIntersection intersection, IGameMap gameMapManagement) {

        gfxCtx.setLineWidth(roadWidth);
        for (IEdge edge : gameMapManagement.incidentEdges(intersection)) {
            //Northwest road
            if (edge.getOwner() == null) continue;
            if (edge.getOrientation() == IEdge.Orientation.WEST) {
                gfxCtx.setStroke(getPlayerColour(edge.getOwner()));
                gfxCtx.strokeLine(currentX, currentY, currentX - (hexWidth / 2.0), currentY - (hexHeight / 4.0));
            }

            //South road
            else if (edge.getOrientation() == IEdge.Orientation.SOUTH) {
                gfxCtx.setStroke(getPlayerColour(edge.getOwner()));
                gfxCtx.strokeLine(currentX, currentY, currentX, currentY + (hexHeight / 2.0));
            }

            //Northeast road
            if (edge.getOrientation() == IEdge.Orientation.EAST) {
                gfxCtx.setStroke(getPlayerColour(edge.getOwner()));
                gfxCtx.strokeLine(currentX, currentY, currentX + (hexWidth / 2.0), currentY - (hexHeight / 4.0));
            }
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
