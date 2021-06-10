package de.uol.swp.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.Colour;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.gamemapDTO.*;
import de.uol.swp.common.game.map.hexes.IGameHex;
import de.uol.swp.common.game.map.hexes.IHarbourHex;
import de.uol.swp.common.game.map.hexes.IResourceHex;
import de.uol.swp.common.game.map.management.IEdge;
import de.uol.swp.common.game.map.management.MapPoint;
import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static de.uol.swp.common.game.map.management.MapPoint.*;

/**
 * GameRendering Class
 * <p>
 * This class handles the rendering of the game board, the dice, and maps
 * x,y coordinates to their MapPoint pendant.
 *
 * @author Timo Gerken
 * @author Temmo Junkhoff
 * @see de.uol.swp.common.game.map.gamemapDTO.GameMapDTO
 * @see javafx.scene.canvas.Canvas
 * @since 2021-01-31
 */
public class GameRendering {

    //Constants used for the colours
    private static final Color TOKEN_COLOUR = Color.BEIGE;
    private static final Color TEXT_COLOUR = Color.BLACK;
    private static final Color BORDER_COLOUR = Color.BLACK;
    private static final Color ROBBER_COLOUR = Color.BLACK;
    private static final Color HARBOUR_COLOUR = Color.rgb(80, 50, 2);
    private static final Color WATER_COLOUR = Color.CORNFLOWERBLUE;
    private static final Color DESERT_COLOUR = Color.rgb(223, 187, 22);
    private static final Color HILLS_COLOUR = Color.rgb(240, 181, 103);
    private static final Color FOREST_COLOUR = Color.rgb(79, 141, 67);
    private static final Color MOUNTAINS_COLOUR = Color.DARKGREY;
    private static final Color FIELDS_COLOUR = Color.rgb(240, 215, 103);
    private static final Color PASTURE_COLOUR = Color.rgb(197, 240, 103);
    private static final Logger LOG = LogManager.getLogger(GameRendering.class);
    private static final Color BUILDABLE_COLOUR = Color.rgb(150, 150, 150, 0.6);

    public static Color PLAYER_1_COLOUR = Color.BLACK;
    public static Color PLAYER_2_COLOUR = Color.BLACK;
    public static Color PLAYER_3_COLOUR = Color.BLACK;
    public static Color PLAYER_4_COLOUR = Color.BLACK;

    @Inject
    private static ResourceBundle resourceBundle;
    @Inject
    private static IUserService userService;
    @Inject
    @Named("drawHitboxGrid")
    private static boolean drawHitboxGrid;
    @Inject
    @Named("renderingStyle")
    private static String renderingStyle;

    private final double OFFSET_Y = 3.0, OFFSET_X = 3.0;
    private final double hexHeight, hexWidth, settlementSize, citySize, diceSize, diceLineWidth, diceDotSize;
    private final double roadWidth, robberLineWidth, tokenSize, effectiveHeight, effectiveWidth, width, height;
    private final GraphicsContext gfxCtx;
    private final double tokenTextFontSize;
    private final double centerTextFontSize;
    private final double bottomTextFontSize;
    private GameMapDescription gameMapDescription = new GameMapDescription();

    /**
     * Constructor
     *
     * @param canvas The canvas that should be drawn on
     */
    public GameRendering(Canvas canvas) {
        double WIDTH_FACTOR = Math.sqrt(3) / 2;
        double HEX_HEIGHT_FACTOR = 1.0 / 5.5;
        double HEX_WIDTH_FACTOR = HEX_HEIGHT_FACTOR * WIDTH_FACTOR;

        this.gfxCtx = canvas.getGraphicsContext2D();
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
        double height = this.height - OFFSET_Y * 2;
        //Sets an effectiveHeight depending on the height and width of the game map
        this.effectiveHeight = (HEX_WIDTH_FACTOR * height * 7 < width) ? height :
                               (width - OFFSET_X * 2.0) / (7 * HEX_HEIGHT_FACTOR * WIDTH_FACTOR);

        this.effectiveWidth = WIDTH_FACTOR * effectiveHeight;
        this.hexHeight = 1.0 / 5.5 * effectiveHeight;
        this.hexWidth = WIDTH_FACTOR * hexHeight;
        this.settlementSize = hexHeight / 4.0;
        this.citySize = settlementSize * 1.25;
        this.roadWidth = settlementSize / 2.0;
        this.robberLineWidth = roadWidth / 2.0;
        this.tokenSize = hexHeight / 3.0;
        this.diceSize = effectiveHeight / 12.0;
        this.diceLineWidth = diceSize / 16.0;
        this.diceDotSize = diceSize / 4.0;
        this.tokenTextFontSize = (20.0 / 750.0) * effectiveWidth;
        this.bottomTextFontSize = (25.0 / 750.0) * effectiveWidth;
        this.centerTextFontSize = (30.0 / 750.0) * effectiveWidth;
    }

    /**
     * Bind a game map description to this game rendering.
     *
     * @param gameMapDescription The game map description
     *
     * @author Temmo Junkhoff
     * @since 2021-05-07
     */
    public void bindGameMapDescription(GameMapDescription gameMapDescription) {
        if (gameMapDescription == null) this.gameMapDescription = new GameMapDescription();
        this.gameMapDescription = gameMapDescription;
    }

    /**
     * coordinatesToHex method
     * <p>
     * This method maps x,y coordinates on the game canvas to the Hex, Edge,
     * or Intersection that is located in that position.
     *
     * @param x The x coordinate on the canvas
     * @param y The y coordinate on the canvas
     *
     * @return MapPoint representing the element located at the provided x,y position
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @since 2021-03-23
     */
    public MapPoint coordinatesToHex(double x, double y) {
        int row = (int) Math.floor((y - OFFSET_Y) / (hexHeight / 8));
        int col = (int) Math.floor((x - OFFSET_X) / (hexWidth / 8));
        return rowColCoordinatesToMapPoint(row, col);
    }

    /**
     * Redraws the canvas based on the information found in the game map description.
     *
     * @author Temmo Junkhoff
     * @since 2021-05-07
     */
    public void redraw() {
        clearGameMap();
        if (gameMapDescription.getGameMap() != null) {
            drawGameMap(gameMapDescription.getGameMap());
        }
        if ((gameMapDescription.getFirstDie() != null && gameMapDescription.getSecondDie() != null)) {
            drawDice(gameMapDescription.getFirstDie(), gameMapDescription.getSecondDie());
        }
        if (gameMapDescription.getCenterText() != null) {
            showCenterText(gameMapDescription.getCenterText());
        }
        if (gameMapDescription.getBottomText() != null) {
            showBottomText(gameMapDescription.getBottomText());
            gameMapDescription.setBottomText(null);
            {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        redraw();
                        timer.cancel();
                    }
                }, 1500);
            }
        }
    }

    /**
     * Sets the player's colours according
     * to the given map
     *
     * @param playerColourMap Map of player and the colour
     *
     * @author Mario Fokken
     * @since 2021-06-02
     */
    public void setPlayerColours(Map<Player, Colour> playerColourMap) {
        int[] colour;
        for (Player p : playerColourMap.keySet()) {
            colour = playerColourMap.get(p).getColourCode();
            switch (p) {
                case PLAYER_1:
                    PLAYER_1_COLOUR = Color.rgb(colour[0], colour[1], colour[2]);
                    break;
                case PLAYER_2:
                    PLAYER_2_COLOUR = Color.rgb(colour[0], colour[1], colour[2]);
                    break;
                case PLAYER_3:
                    PLAYER_3_COLOUR = Color.rgb(colour[0], colour[1], colour[2]);
                    break;
                case PLAYER_4:
                    PLAYER_4_COLOUR = Color.rgb(colour[0], colour[1], colour[2]);
                    break;
            }
        }
    }

    /**
     * Helper method to clear the game map
     *
     * @author Temmo Junkhoff
     * @implNote The method contents are executed on the JavaFX Application Thread
     * @since 2021-03-29
     */
    private void clearGameMap() {
        Platform.runLater(() -> gfxCtx.clearRect(0, 0, width, height));
    }

    /**
     * drawCity method
     * <p>
     * This method draws a city at the given coordinates.
     *
     * @param owner    Needed to access the color of player
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     *
     * @implNote The method contents are executed on the JavaFX Application Thread
     */
    private void drawCity(Optional<Player> owner, double currentX, double currentY) {
        if (owner.isEmpty()) Platform.runLater(() -> gfxCtx.setFill(BUILDABLE_COLOUR));
        else Platform.runLater(() -> gfxCtx.setFill(getPlayerColour(owner.get())));
        double halfCitySize = citySize / 2.0;
        double x = currentX - halfCitySize;
        double y = currentY - halfCitySize;
        Platform.runLater(() -> gfxCtx.fillRoundRect(x, y, citySize, citySize, halfCitySize, halfCitySize));
    }

    /**
     * drawDice method
     * <p>
     * This method draws the dice.
     *
     * @param die1 The first die
     * @param die2 The second die
     */
    private void drawDice(Integer die1, Integer die2) {
        if (die1 == null || die2 == null || die1 == 0 || die2 == 0) return;
        double startX = OFFSET_X;
        double startY = OFFSET_Y;
        renderDice(die1, startX, startY);
        startX += diceSize + diceSize / 8.0;
        renderDice(die2, startX, startY);
    }

    /**
     * drawGameMap Method
     * <p>
     * This method draws the game map represented in the given IGameMap on the given canvas
     * This method is the only one that ever needs to be accessed from outside this interface.
     *
     * @param gameMap An IGameMap providing the game map to draw
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread.
     * If the subroutines have UI-modifying aspects, those will be called with Platform.runLater()
     */
    private void drawGameMap(IGameMap gameMap) {
        LOG.debug("Drawing Game map");

        //Get hexes, intersections, and edges in a usable format from the IGameMap
        IGameHex[][] hexes = gameMap.getHexes();
        IIntersectionWithEdges[][] intersections = gameMap.getIntersections();
        //Call functions to draw hexes, intersections, and edges
        drawHexTiles(hexes);
        drawIntersectionsAndEdges(intersections);

        if (drawHitboxGrid) drawHitboxGrid();
    }

    /**
     * drawHarbour method
     * <p>
     * This method draws a harbour at the given coordinate in the correct orientation
     * together with a text indicating which resource the harbour harbours.
     *
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     * @param hex      The harbour hex with the information about the harbour
     */
    private void drawHarbour(double currentX, double currentY, IHarbourHex hex) {
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
        String finalText = text;
        double x = currentX + hexWidth / 2.0;
        double y = currentY + hexHeight / 2.0;
        double maxWidth = hexWidth * (6.0 / 8.0);
        Platform.runLater(() -> {
            gfxCtx.setStroke(HARBOUR_COLOUR);
            gfxCtx.setFill(HARBOUR_COLOUR);
            gfxCtx.setLineWidth(hexWidth / 5.0);
            gfxCtx.fillPolygon(xCords, yCords, 4);
            gfxCtx.setTextAlign(TextAlignment.CENTER);
            gfxCtx.setTextBaseline(VPos.CENTER);
            gfxCtx.setFill(TEXT_COLOUR);
            gfxCtx.setFont(Font.font(tokenTextFontSize));
            gfxCtx.fillText(finalText, x, y, maxWidth);
        });
    }

    /**
     * drawHex method
     * <p>
     * This Method draws a hexagon at the given coordinates.
     *
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     *
     * @implNote The method contents are executed on the JavaFX Application Thread
     */
    private void drawHex(String type, double currentX, double currentY) {
        //TODO Change me maybe
        double[] xCords = {currentX, currentX + hexWidth / 2, currentX + hexWidth, currentX + hexWidth,
                           currentX + hexWidth / 2, currentX};
        double[] yCords = {currentY + (hexHeight / 4), currentY, currentY + (hexHeight / 4),
                           currentY + (hexHeight / 4) * 3, currentY + hexHeight, currentY + (hexHeight / 4) * 3};
        Platform.runLater(() -> {
            gfxCtx.fillPolygon(xCords, yCords, 6);
            if (!renderingStyle.equals("plain"))
                gfxCtx.drawImage(new Image("images/hexes/" + renderingStyle + "/" + type + ".png", false), currentX,
                                 currentY, hexWidth, hexHeight);
            gfxCtx.setStroke(BORDER_COLOUR);
            gfxCtx.setLineWidth(2);
            gfxCtx.strokePolygon(xCords, yCords, 6);
        });
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
     * Helper method to draw a hitbox grid
     * <p>
     * This method draws a grid showing the hitboxes on the playing field.
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @implNote The method contents are executed on the JavaFX Application Thread
     * @since 2021-03-27
     */
    private void drawHitboxGrid() {
        double hexWidthBy8 = hexWidth / 8;
        Platform.runLater(() -> {
            gfxCtx.setStroke(Color.RED);
            gfxCtx.setLineWidth(1);
            for (double cy = OFFSET_Y; cy < height; cy += hexWidthBy8)
                gfxCtx.strokeLine(0, cy, width, cy);

            for (double cx = OFFSET_X; cx < width; cx += hexWidthBy8)
                gfxCtx.strokeLine(cx, 0, cx, height);
        });
    }

    /**
     * drawIntersectionsAndEdges method
     * <p>
     * This Method draws the intersections and edges.
     *
     * @param intersections An array containing all intersections
     */
    private void drawIntersectionsAndEdges(IIntersectionWithEdges[][] intersections) {
        goThroughHalfMap(true, intersections);
        goThroughHalfMap(false, intersections);
    }

    /**
     * drawRobber method
     * <p>
     * This method draws a robber at the given coordinates.
     *
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     *
     * @implNote The method contents are executed on the JavaFX Application Thread
     */
    private void drawRobber(double currentX, double currentY) {
        double[] xPoints = {currentX + hexWidth * (1.0 / 4.0), currentX + hexWidth * (3.0 / 4.0),
                            currentX + hexWidth * (2.0 / 4.0)};
        double[] yPoints = {currentY + hexHeight * (2.75 / 4.0), currentY + hexHeight * (2.75 / 4.0),
                            currentY + hexHeight * (1.125 / 4.0)};
        Platform.runLater(() -> {
            if (!renderingStyle.equals("plain"))
                gfxCtx.drawImage(new Image("images/hexes/" + renderingStyle + "/robber.png", false), currentX * 1.05,
                                 currentY * 1.05, hexWidth * 0.6, hexHeight * 0.6);
            else {
                gfxCtx.setLineWidth(robberLineWidth);
                gfxCtx.setStroke(ROBBER_COLOUR);
                gfxCtx.strokePolygon(xPoints, yPoints, 3);
            }
        });
    }

    /**
     * drawSettlement method
     * <p>
     * This method draws a settlement at the given coordinates.
     *
     * @param owner    Needed to access the color of player
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     *
     * @implNote The method contents are executed on the JavaFX Application Thread
     */
    private void drawSettlement(Optional<Player> owner, double currentX, double currentY) {
        double x = currentX - (settlementSize / 2.0);
        double y = currentY - (settlementSize / 2.0);

        if (owner.isEmpty()) Platform.runLater(() -> gfxCtx.setFill(BUILDABLE_COLOUR));
        else Platform.runLater(() -> gfxCtx.setFill(getPlayerColour(owner.get())));
        Platform.runLater(() -> gfxCtx.fillOval(x, y, settlementSize, settlementSize));
    }

    /**
     * drawToken method
     * This method draws the token of a hex at the given coordinates.
     *
     * @param token    The value of the token that should be drawn
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     *
     * @implNote The method contents are executed on the JavaFX Application Thread
     */
    private void drawToken(int token, double currentX, double currentY) {
        double xPos = currentX + (hexWidth - tokenSize) / 2.0;
        double yPos = currentY + (hexHeight - tokenSize) / 2.0;
        double x = currentX + hexWidth / 2.0;
        double y = currentY + hexWidth / 2.0;
        double maxWidth = tokenSize * (7.0 / 8.0);
        String tokenLabel = resourceBundle.getString("game.token." + token);
        Font tokenFont = Font.font(tokenTextFontSize);

        Platform.runLater(() -> {
            gfxCtx.setFill(TOKEN_COLOUR);
            gfxCtx.fillOval(xPos, yPos, tokenSize, tokenSize);
            gfxCtx.setFill(TEXT_COLOUR);
            gfxCtx.setTextAlign(TextAlignment.CENTER);
            gfxCtx.setTextBaseline(VPos.CENTER);
            gfxCtx.setFont(tokenFont);
            gfxCtx.fillText(tokenLabel, x, y, maxWidth);
        });
    }

    /**
     * getHexesInRow method
     * <p>
     * This method returns the amount of Hexes contained in the y row.
     *
     * @param y The row for which to return the Hex count
     *
     * @return The amount of Hexes in this row
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @since 2021-03-14
     */
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
     */
    private void goThroughHalfMap(boolean topHalf, IIntersectionWithEdges[][] intersections) {
        //Sets currentY depending on topHalf
        double currentY = ((topHalf) ? (hexHeight * (3.0 / 4.0)) :
                           ((effectiveHeight / 2) + (hexHeight / 4))) + OFFSET_Y;
        //Goes through all rows in the current half of the game map
        for (int y = ((topHalf) ? 0 : intersections.length / 2); y < ((topHalf) ? intersections.length / 2 :
                                                                      intersections.length); y++) {

            double rowStartX = ((intersections[intersections.length / 2].length - intersections[y].length) / 4.0) * hexWidth;
            double currentX = OFFSET_X + rowStartX + hexWidth;
            if (topHalf) currentX += hexWidth / 2.0;
            goThroughSubRow(topHalf, false, currentX, currentY, intersections[y]);

            currentX = OFFSET_X + rowStartX + hexWidth;
            if (!topHalf) currentX += hexWidth / 2.0;
            currentY += (hexHeight / 4.0);
            goThroughSubRow(!topHalf, true, currentX, currentY, intersections[y]);
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
     * @param intersections An array containing all intersections in the current row
     */
    private void goThroughSubRow(boolean firstSubRow, boolean renderEdges, double currentX, double currentY,
                                 IIntersectionWithEdges[] intersections) {
        for (int x = firstSubRow ? 1 : 0; x < intersections.length; x = x + 2) {
            if (renderEdges) {
                renderEdges(currentX, currentY, intersections[x]);
            }
            renderIntersection(currentX, currentY, intersections[x].getIntersection());
            currentX += hexWidth;
        }
    }

    /**
     * horizontalEdgeToMapPoint method
     * <p>
     * This method maps a row, column coordinate for a horizontal Edge to
     * the neighbouring Hexes and returns an EdgeMapPoint defined by those
     * hexes.
     *
     * @param row The row in which this Edge is located
     * @param col The column in which this Edge is located
     *
     * @return MapPoint representing the Edge
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @since 2021-03-18
     */
    private MapPoint horizontalEdgeToMapPoint(int row, int col) {
        MapPoint left = rowColCoordinatesToMapPoint(row, col - 3);
        MapPoint right = rowColCoordinatesToMapPoint(row, col + 3);
        return EdgeMapPoint(left, right);
    }

    /**
     * renderDice method
     * <p>
     * This method renders a given die at the given coordinates.
     *
     * @param die      The die to be drawn
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     *
     * @implNote The method contents are executed on the JavaFX Application Thread
     */
    private void renderDice(int die, double currentX, double currentY) {
        double middleDotPos = diceSize / 2.0 - diceDotSize / 2.0;
        double firstDotPos = diceDotSize / 2.0;
        double secondDotPos = diceSize - diceDotSize / 2.0 - diceDotSize;
        double arcDimensions = diceSize / 3.0;

        Platform.runLater(() -> {
            gfxCtx.setFill(Color.WHITE);
            gfxCtx.setStroke(Color.BLACK);
            gfxCtx.setLineWidth(diceLineWidth);
            gfxCtx.fillRoundRect(currentX, currentY, diceSize, diceSize, arcDimensions, arcDimensions);
            gfxCtx.strokeRoundRect(currentX, currentY, diceSize, diceSize, arcDimensions, arcDimensions);
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
        });
    }

    /**
     * renderEdges method
     * <p>
     * This Method draws the 3 edges around an intersection at the given coordinates.
     *
     * @param currentX The current x-coordinate
     * @param currentY The current y-coordinate
     *
     * @implNote The method contents are executed on the JavaFX Application Thread
     */
    private void renderEdges(double currentX, double currentY, IIntersectionWithEdges intersection) {
        Platform.runLater(() -> gfxCtx.setLineWidth(roadWidth));
        if (intersection == null) return;
        for (IEdgeWithBuildable edge : intersection.getEdges()) {
            if (edge == null) continue;
            if (edge.isBuildableBy(userService.getLoggedInUser())) {
                Platform.runLater(() -> gfxCtx.setStroke(BUILDABLE_COLOUR));
            } else if (edge.getOwner() == null) {
                continue;
            } else {
                Color playerColour = getPlayerColour(edge.getOwner());
                Platform.runLater(() -> gfxCtx.setStroke(playerColour));
            }
            //Northwest road
            if (edge.getOrientation() == IEdge.Orientation.WEST) {
                double x2 = currentX - (hexWidth / 2.0);
                double y2 = currentY - (hexHeight / 4.0);
                Platform.runLater(() -> gfxCtx.strokeLine(currentX, currentY, x2, y2));
            }

            //South road
            else if (edge.getOrientation() == IEdge.Orientation.SOUTH) {
                double y2 = currentY + (hexHeight / 2.0);
                Platform.runLater(() -> gfxCtx.strokeLine(currentX, currentY, currentX, y2));
            }

            //Northeast road
            if (edge.getOrientation() == IEdge.Orientation.EAST) {
                double x2 = currentX + (hexWidth / 2.0);
                double y2 = currentY - (hexHeight / 4.0);
                Platform.runLater(() -> gfxCtx.strokeLine(currentX, currentY, x2, y2));
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
        if (hex == null) return;
        if (setHexColour(hex).isEmpty()) return;

        drawHex(setHexColour(hex), currentX, currentY);

        if (hex.isRobberOnField()) drawRobber(currentX, currentY);
        if (hex instanceof IResourceHex) drawToken(((IResourceHex) hex).getToken(), currentX, currentY);

        if (hex instanceof IHarbourHex) drawHarbour(currentX, currentY, (IHarbourHex) hex);
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
    private void renderIntersection(double currentX, double currentY, IIntersectionWithBuildable intersection) {
        if (intersection == null) return;
        switch (intersection.getState()) {
            case FREE:
                if (intersection.isBuildableBy(userService.getLoggedInUser()))
                    drawSettlement(Optional.empty(), currentX, currentY);
                //Free intersections don't need to be marked, but it could easily be added here
                break;
            case SETTLEMENT:
                drawSettlement(Optional.of(intersection.getOwner()), currentX, currentY);
                if (intersection.isBuildableBy(userService.getLoggedInUser()))
                    drawCity(Optional.empty(), currentX, currentY);
                break;
            case CITY:
                drawCity(Optional.of(intersection.getOwner()), currentX, currentY);
                break;
        }
    }

    /**
     * rowColCoordinatesToMapPoint method
     * <p>
     * This method takes a pair of row and column coordinates and returns
     * a MapPoint representing the element located at that location.
     *
     * @param row The row location to map to a MapPoint
     * @param col The column location to map to a MapPoint
     *
     * @return MapPoint representing the element in the row, col location
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @since 2021-03-14
     */
    private MapPoint rowColCoordinatesToMapPoint(int row, int col) {
        int col8 = col % 8;
        int hexY = row / 6;
        int hexX;
        int rawCol = col;
        boolean lowerHalf = hexY > 3;

        col = col - (((7 - getHexesInRow(hexY)) / 2) * 8); // left align all rows
        if (row == 0 || row == 1) { // first row
            return InvalidMapPoint();
        } else if (row == 42 || row == 43) { // last row // check if rows are correct
            return InvalidMapPoint();
        } else if ((row % 12) < 6) {// indented hex rows (0, 2, 4, 6)
            if (col < 4 + 1 || col > (getHexesInRow(hexY) * 8) + 4 - 1) return InvalidMapPoint();
            switch (row % 6) {
                case 0:
                    // 0th partial row of an indented hex row
                    // (contains peaks of the current hex row and part of basement of the unindented row above this oene)
                    switch (col8) {
                        case 0:
                        case 7:
                            // "peak" intersection of indented row hex
                            hexX = (((col - 5) / 8) * 2) - 1;
                            if (lowerHalf) hexX++;
                            return IntersectionMapPoint(hexY - 1, hexX);
                        case 1:
                        case 2:
                        case 5:
                        case 6:
                            //edge (righthand downslope road from ^ )
                            return verticalEdgeToMapPoint(row, rawCol);
                        case 3:
                        case 4:
                            //hex in the unindented row above (above "trough" intersection between indented row hexes)
                            hexY = hexY - 1;
                            hexX = (col - 5) / 8;
                            if (lowerHalf) hexX++;
                            return HexMapPoint(hexY, hexX);
                    }
                case 1:
                    // 1st partial row of a hex row
                    // (contains troughs of previous hex row and part of ceiling of current hex row)
                    switch (col8) {
                        case 0:
                        case 7:
                            // part of ceiling of current hex row
                            hexX = (col - 5) / 8;
                            return HexMapPoint(hexY, hexX);
                        case 1:
                        case 2:
                        case 5:
                        case 6:
                            //edge
                            return verticalEdgeToMapPoint(row, rawCol);
                        case 3:
                        case 4:
                            // trough intersection between indented row hexes
                            hexX = (((col - 5) / 8) * 2);
                            if (lowerHalf) hexX++;
                            return IntersectionMapPoint(hexY - 1, hexX);
                    }
                case 2:
                    // 2nd partial row of a hex row
                    // (contains top left and top right intersections of a hex and hex itself)
                    if (col8 == 3 || col8 == 4) {
                        // one of the intersections bordering the hex
                        hexX = (((col - 5) / 8) * 2);
                        if (lowerHalf) hexX++;
                        return IntersectionMapPoint(hexY - 1, hexX);
                    } else {
                        hexX = (col - 5) / 8;
                        return HexMapPoint(hexY, hexX);
                    }
                case 3:
                case 4:
                    // 3rd and 4th partials rows of a hex row
                    // (contains bordering edges of a hex and hex itself)
                    if (col8 == 3 || col8 == 4) {
                        // bordering edge
                        return horizontalEdgeToMapPoint(row, rawCol);
                    } else {
                        hexX = (col - 5) / 8;
                        return HexMapPoint(hexY, hexX);
                    }
                case 5:
                    // 5th partial row of a hex row
                    // (contains bottom left and bottom right intersections of a hex and hex itself)
                    if (col8 == 3 || col8 == 4) {
                        // on of the intersections bordering the hex
                        hexX = (((col - 5) / 8) * 2) + 1;
                        if (lowerHalf) hexX--;
                        return IntersectionMapPoint(hexY, hexX);
                    } else {
                        // Hex
                        hexX = (col - 5) / 8;
                        return HexMapPoint(hexY, hexX);
                    }
            }
        } else {
            // unindented hex rows (1, 3, 5, 7)
            if (col < 1 || col > (getHexesInRow(hexY) * 8) - 1) return InvalidMapPoint();
            switch (row % 6) {
                case 0:
                    switch (col8) {
                        case 0: // part of hex left above
                        case 7: // part of hex right above
                            hexY = hexY - 1;
                            hexX = (col - 1) / 8;
                            if (lowerHalf) hexX++;
                            return HexMapPoint(hexY, hexX);
                        case 1: // upward edge to peak intersection of hex
                        case 2: // upward edge to peak intersection of hex
                        case 5: // downward edge from peak intersection of hex
                        case 6: // downward edge from peak intersection of hex
                            return verticalEdgeToMapPoint(row, rawCol);
                        case 3: // peak intersection
                        case 4: // peak intersection
                            hexX = (((col - 1) / 8) * 2) - 1;
                            if (lowerHalf) hexX++;
                            return IntersectionMapPoint(hexY - 1, hexX);
                    }
                case 1:
                    switch (col8) {
                        case 0:
                        case 7:
                            // top right or top left intersection of hex
                            hexX = ((col - 1) / 8) * 2;
                            if (lowerHalf) hexX++;  // (((((((hexX++)++)--)++)--)--)++)
                            return IntersectionMapPoint(hexY - 1, hexX);
                        case 1:
                        case 2:
                        case 5:
                        case 6:
                            // upward edge towards peak
                            return verticalEdgeToMapPoint(row, rawCol);
                        case 3:
                        case 4:
                            // part of ceiling of current hex
                            hexX = (col - 1) / 8;
                            if (lowerHalf) hexX--;
                            return HexMapPoint(hexY, hexX);
                    }
                case 3:
                case 4:
                    if (col8 == 7 || col8 == 0) {
                        // Edge
                        return horizontalEdgeToMapPoint(row, rawCol);
                    } else {
                        hexX = (col - 1) / 8;
                        return HexMapPoint(hexY, hexX);
                    }
                case 2:
                    if (lowerHalf) hexY--;
                case 5:
                    if (col8 == 7 || col8 == 0) {
                        // peak intersection
                        hexX = ((col - 1) / 8) * 2;
                        if (lowerHalf && row % 6 == 2) hexX++;
                        return IntersectionMapPoint(hexY, hexX);
                    } else {
                        hexX = (col - 1) / 8;
                        return HexMapPoint(hexY, hexX);
                    }
            }
        }
        return InvalidMapPoint();
    }

    /**
     * setHexColour Method
     * <p>
     * This method sets the colour according to a hex tile.
     *
     * @param hex The hex tile the colour should be set accordingly to
     *
     * @return True if the colour couldn't be set, false otherwise
     *
     * @implNote The method contents are executed on the JavaFX Application Thread
     */
    private String setHexColour(IGameHex hex) {
        if (hex == null) return "";
        switch (hex.getType()) {
            case WATER:
            case HARBOUR:
                Platform.runLater(() -> gfxCtx.setFill(WATER_COLOUR));
                return "water";
            case DESERT:
                Platform.runLater(() -> gfxCtx.setFill(DESERT_COLOUR));
                return "desert";
            case RESOURCE:
                switch (((IResourceHex) hex).getResource()) {
                    case BRICK:
                        Platform.runLater(() -> gfxCtx.setFill(HILLS_COLOUR));
                        return "hills";
                    case LUMBER:
                        Platform.runLater(() -> gfxCtx.setFill(FOREST_COLOUR));
                        return "forest";
                    case ORE:
                        Platform.runLater(() -> gfxCtx.setFill(MOUNTAINS_COLOUR));
                        return "mountains";
                    case GRAIN:
                        Platform.runLater(() -> gfxCtx.setFill(FIELDS_COLOUR));
                        return "fields";
                    case WOOL:
                        Platform.runLater(() -> gfxCtx.setFill(PASTURE_COLOUR));
                        return "pasture";
                    default:
                        return "";
                }
            default:
                return "";
        }
    }

    /**
     * Shows a text notification on the canvas
     *
     * @param text The text to display
     *
     * @author Temmo Junkhoff
     * @author Aldin Dervisi
     * @implNote The method contents are executed on the JavaFX Application Thread
     * @since 2021-04-08
     */
    private void showBottomText(String text) {
        if (text == null) return;
        Font bottomTextFont = Font.font(bottomTextFontSize);
        double bottomTextX = width / 2.0;
        double bottomTextY = height * (3.0 / 4.0);
        Platform.runLater(() -> {
            gfxCtx.setTextAlign(TextAlignment.CENTER);
            gfxCtx.setTextBaseline(VPos.CENTER);
            gfxCtx.setFill(Color.BLACK);
            gfxCtx.setFont(bottomTextFont);
            gfxCtx.fillText(text, bottomTextX, bottomTextY);
        });
    }

    /**
     * Shows a winner notification on the canvas
     *
     * @param text The text to display
     *
     * @author Temmo Junkhoff
     * @author Maximilian Lindner
     * @implNote The method contents are executed on the JavaFX Application Thread
     * @since 2021-03-29
     */
    private void showCenterText(String text) {
        if (text == null) return;
        Font centerTextFont = Font.font(centerTextFontSize);
        double centerTextX = width / 2.0;
        double centerTextY = height / 2.0;
        Platform.runLater(() -> {
            gfxCtx.setTextAlign(TextAlignment.CENTER);
            gfxCtx.setTextBaseline(VPos.CENTER);
            gfxCtx.setFill(Color.BLACK);
            gfxCtx.setFont(centerTextFont);
            gfxCtx.fillText(text, centerTextX, centerTextY);
        });
    }

    /**
     * verticalEdgeToMapPoint method
     * <p>
     * This method maps a row, column coordinate for a vertical Edge to
     * the neighbouring Hexes and returns an EdgeMapPoint defined by those
     * hexes.
     *
     * @param row The row in which this Edge is located
     * @param col The column in which this Edge is located
     *
     * @return MapPoint representing the Edge
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @since 2021-03-18
     */
    private MapPoint verticalEdgeToMapPoint(int row, int col) {
        MapPoint left = rowColCoordinatesToMapPoint(row - 2, col);
        MapPoint right = rowColCoordinatesToMapPoint(row + 2, col);
        return EdgeMapPoint(left, right);
    }

    /**
     * A class to store a description of the current state of the game map.
     *
     * @author Temmo Junkhoff
     * @since 2021-05-07
     */
    public static class GameMapDescription {

        IGameMap gameMap;
        Integer firstDie;
        Integer secondDie;
        String bottomText;
        String centerText;

        /**
         * Constructor
         *
         * @author Temmo Junkhoff
         * @since 2021-05-07
         */
        public GameMapDescription() {
            gameMap = null;
            firstDie = null;
            secondDie = null;
            bottomText = null;
            centerText = null;
        }

        /**
         * Clears the description.
         *
         * @author Temmo Junkhoff
         * @since 2021-05-07
         */
        public void clear() {
            gameMap = null;
            firstDie = null;
            secondDie = null;
            bottomText = null;
            centerText = null;
        }

        /**
         * Gets the bottom text.
         *
         * @return The bottom text
         *
         * @author Temmo Junkhoff
         * @since 2021-05-07
         */
        public String getBottomText() {
            return bottomText;
        }

        /**
         * Sets The bottom text.
         *
         * @param bottomText The bottom text
         *
         * @author Temmo Junkhoff
         * @since 2021-05-07
         */
        public void setBottomText(String bottomText) {
            this.bottomText = bottomText;
        }

        /**
         * Gets the center text.
         *
         * @return The center text
         *
         * @author Temmo Junkhoff
         * @since 2021-05-07
         */
        public String getCenterText() {
            return centerText;
        }

        /**
         * Sets The center text.
         *
         * @param centerText The center text
         *
         * @author Temmo Junkhoff
         * @since 2021-05-07
         */
        public void setCenterText(String centerText) {
            this.centerText = centerText;
        }

        /**
         * Gets the first die.
         *
         * @return The first die
         *
         * @author Temmo Junkhoff
         * @since 2021-05-07
         */
        public Integer getFirstDie() {
            return firstDie;
        }

        /**
         * Gets the game map.
         *
         * @return The game map
         *
         * @author Temmo Junkhoff
         * @since 2021-05-07
         */
        public IGameMap getGameMap() {
            return gameMap;
        }

        /**
         * Sets The game map.
         *
         * @param gameMap The game map
         *
         * @author Temmo Junkhoff
         * @since 2021-05-07
         */
        public void setGameMap(IGameMap gameMap) {
            this.gameMap = gameMap;
        }

        /**
         * Gets the second die.
         *
         * @return The second die
         *
         * @author Temmo Junkhoff
         * @since 2021-05-07
         */
        public Integer getSecondDie() {
            return secondDie;
        }

        /**
         * Sets The dice.
         *
         * @param firstDie  The first die
         * @param secondDie The second die
         *
         * @author Temmo Junkhoff
         * @since 2021-05-07
         */
        public void setDice(int firstDie, int secondDie) {
            this.firstDie = firstDie;
            this.secondDie = secondDie;
        }
    }
}
