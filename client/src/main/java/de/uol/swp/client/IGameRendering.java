package de.uol.swp.client;

import de.uol.swp.common.game.map.Hexes.*;
import de.uol.swp.common.game.map.IEdge;
import de.uol.swp.common.game.map.IGameMapManagement;
import de.uol.swp.common.game.map.IIntersection;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;

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
    double HEXHEIGHTFACTOR = 1.0 / 5.5;
    double HEXWIDTHFACTOR = HEXHEIGHTFACTOR * (Math.sqrt(3) / 2);
    double SETTLEMENTSIZEFACTOR = HEXHEIGHTFACTOR / 5.0;
    double CITYSIZEFACTOR = SETTLEMENTSIZEFACTOR * 1.25;
    double ROADWIDTHFACTOR = SETTLEMENTSIZEFACTOR / 4.0;
    double ROBBERLINEWIDTHFACTOR = ROADWIDTHFACTOR;

    /**
     * drawGameMap Method
     * <p>
     * This method draws the game map represented in the given GameMapManagement on the given canvas
     * This method is the only one that ever needs to be accessed from outside this interface.
     *
     * @param gameMapManagement A GameMapManagement providing the game map to draw
     * @param canvas            A canvas to draw on
     */
    default void drawGameMap(IGameMapManagement gameMapManagement, Canvas canvas) {
        double width = canvas.getWidth(), height = canvas.getHeight();
        GraphicsContext mapCtx = canvas.getGraphicsContext2D();

        //Sets an effectiveHeight depending on the height and width of the game map
        double effectiveHeight = (HEXWIDTHFACTOR * height * 7 < width) ? height :
                width / (7 * HEXHEIGHTFACTOR * (Math.sqrt(3) / 2));

        //Get hexes, intersections, and edges in a usable format from the gameMapManagement
        IGameHex[][] hexes = gameMapManagement.getHexesAsJaggedArray();
        IIntersection[][] intersections = gameMapManagement.getIntersectionsAsJaggedArray();
        IEdge[][] edges = gameMapManagement.getEdgesAsJaggedArrayWithNullFiller();

        //Call functions to draw hexes, intersections, and edges
        drawHexTiles(hexes, effectiveHeight, mapCtx);
        drawIntersectionsAndEdges(intersections, edges, effectiveHeight, mapCtx);
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
        double currentY = 0;
        //double currentY = hexHeight / 2.0;
        for (IGameHex[] hex : hexes) {
            double currentX = 0;
            //Set the indentation for the current row of hex tiles
            if (hex.length % 2 == 0) { //Row with an even amount of hex tiles
                currentX += (HEXWIDTHFACTOR * effectiveHeight) / 2.0;
                currentX += ((hexes[hexes.length / 2].length - 1 - hex.length) / 2.0) *
                        (HEXWIDTHFACTOR * effectiveHeight);
            } else {//Row with an odd amount of hex tiles
                currentX += ((hexes[hexes.length / 2].length - hex.length) / 2.0) * (HEXWIDTHFACTOR * effectiveHeight);
            }

            for (IGameHex iGameHex : hex) {
                renderHex(iGameHex, currentX, currentY, effectiveHeight, mapCtx);
                currentX += (HEXWIDTHFACTOR * effectiveHeight);
            }
            currentY += ((HEXHEIGHTFACTOR * effectiveHeight) / 4) * 3;
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
    private void goThroughHalfMap(boolean topHalf, IIntersection[][] intersections,
                                  IEdge[][] edges, double effectiveHeight, GraphicsContext mapCtx) {
        //Sets currentY depending on topHalf
        double currentY = (topHalf) ? ((HEXHEIGHTFACTOR * effectiveHeight) * (3.0 / 4.0)) :
                ((effectiveHeight / 2) + ((HEXHEIGHTFACTOR * effectiveHeight) / 4));
        //Goes through all rows in the current half of the game map
        for (int y = ((topHalf) ? 0 : intersections.length / 2); y < ((topHalf) ? intersections.length / 2 :
                intersections.length); y++) {
            double rowStartX = ((intersections[intersections.length / 2].length - intersections[y].length) / 4.0) *
                    (HEXWIDTHFACTOR * effectiveHeight);
            double currentX = rowStartX + (HEXWIDTHFACTOR * effectiveHeight);
            if (topHalf) currentX += (HEXWIDTHFACTOR * effectiveHeight) / 2.0;
            goThroughSubRow(topHalf ? 1 : 0, false, currentX, currentY, intersections[y],
                    edges[y], effectiveHeight, mapCtx);

            currentX = rowStartX + (HEXWIDTHFACTOR * effectiveHeight);
            if (!topHalf) currentX += (HEXWIDTHFACTOR * effectiveHeight) / 2.0;
            currentY += ((HEXHEIGHTFACTOR * effectiveHeight) / 4.0);
            goThroughSubRow(topHalf ? 0 : 1, true, currentX, currentY, intersections[y],
                    edges[y], effectiveHeight, mapCtx);
            currentY += (HEXHEIGHTFACTOR * effectiveHeight) / 2.0;
        }
    }

    /**
     * goThroughSubRow method
     * <p>
     * This method is called by goThroughHalfMap to draw all intersections and optionally all edges in a given sub row.
     * Every Row is separated in to two sub row which have slightly different y-coordinates.
     *
     * @param startOn         Used to indicate which sub row should be accessed
     * @param renderEdges     Used to indicate whether edges should be drawn
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param intersections   An array containing all intersections
     * @param edges           An array containing all edges
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void goThroughSubRow(int startOn, boolean renderEdges, double currentX, double currentY,
                                 IIntersection[] intersections, IEdge[] edges, double effectiveHeight,
                                 GraphicsContext mapCtx) {
        for (int x = startOn, xEdges = 0; x < intersections.length; x = x + 2, xEdges = xEdges + 3) {
            if (renderEdges) {
                renderEdges(currentX, currentY, Arrays.copyOfRange(edges, xEdges, xEdges + 3),
                        effectiveHeight, mapCtx);
            }
            renderIntersection(currentX, currentY, intersections[x], effectiveHeight, mapCtx);
            currentX += (HEXWIDTHFACTOR * effectiveHeight);
        }
    }

    /**
     * getPlayerColour(String) method
     * <p>
     * This method gets the colour of the indicated player.
     *
     * @param player Indicates a player
     * @return The colour of the indicated player
     */
    private Color getPlayerColour(String player) {
        if (player.startsWith("1")) {
            return Color.BLACK;
        } else if (player.startsWith("2")) {
            return Color.RED;
        } else if (player.startsWith("3")) {
            return Color.PINK;
        } else if (player.startsWith("4")) {
            return Color.WHITE;
        }
        return null;
    }

    /**
     * getPlayerColour(int) method
     * <p>
     * This method gets the colour of the indicated player.
     *
     * @param player Indicates a player
     * @return The colour of the indicated player
     */
    private Color getPlayerColour(int player) {
        return getPlayerColour(String.valueOf(player));
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
        mapCtx.setLineWidth(ROADWIDTHFACTOR * effectiveHeight);

        //Northwest road
        if (edges[0] != null && edges[0].getState() != 0) {
            mapCtx.setStroke(getPlayerColour(edges[0].getState()));
            mapCtx.strokeLine(currentX, currentY,
                    currentX - ((HEXWIDTHFACTOR * effectiveHeight) / 2.0),
                    currentY - ((HEXHEIGHTFACTOR * effectiveHeight) / 4.0));
        }

        //Northeast road
        if (edges[1] != null && edges[1].getState() != 0) {
            mapCtx.setStroke(getPlayerColour(edges[1].getState()));
            mapCtx.strokeLine(currentX, currentY,
                    currentX, currentY + ((HEXHEIGHTFACTOR * effectiveHeight) / 2.0));
        }

        //South road
        if (edges[2] != null && edges[2].getState() != 0) {
            mapCtx.setStroke(getPlayerColour(edges[2].getState()));
            mapCtx.strokeLine(currentX, currentY,
                    currentX + ((HEXWIDTHFACTOR * effectiveHeight) / 2.0),
                    currentY - ((HEXHEIGHTFACTOR * effectiveHeight) / 4.0));
        }
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
        String state = intersection.getState();
        if (state.equals("f")) { //Free intersection
            //Free intersections don't need to be marked, but it could easily be added here
        } else if (state.equals("b")) { //Blocked intersection
            //Blocked intersections don't need to be marked, but it could easily be added here
        } else if (state.endsWith("s")) { //Intersection with settlement
            drawSettlement(state, currentX, currentY, effectiveHeight, mapCtx);
        } else if (state.endsWith("c")) { //Intersection with city
            drawCity(state, currentX, currentY, effectiveHeight, mapCtx);
        }
    }

    /**
     * drawCity method
     * <p>
     * This method draws a city at the given coordinates.
     *
     * @param state           Needed to access the color of player
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void drawCity(String state, double currentX, double currentY, double effectiveHeight,
                          GraphicsContext mapCtx) {
        mapCtx.setFill(getPlayerColour(state));
        mapCtx.fillRoundRect(currentX - ((CITYSIZEFACTOR * effectiveHeight) / 2.0),
                currentY - ((CITYSIZEFACTOR * effectiveHeight) / 2.0),
                (CITYSIZEFACTOR * effectiveHeight),
                (CITYSIZEFACTOR * effectiveHeight),
                (CITYSIZEFACTOR * effectiveHeight) / 2.0,
                (CITYSIZEFACTOR * effectiveHeight) / 2.0);
    }

    /**
     * drawSettlement method
     * <p>
     * This method draws a settlement at the given coordinates.
     *
     * @param state           Needed to access the color of player
     * @param currentX        The current x-coordinate
     * @param currentY        The current y-coordinate
     * @param effectiveHeight The effective height of the game map
     * @param mapCtx          A GraphicsContext needed to draw
     */
    private void drawSettlement(String state, double currentX, double currentY, double effectiveHeight, GraphicsContext mapCtx) {
        mapCtx.setFill(getPlayerColour(state));
        mapCtx.fillOval(currentX - ((SETTLEMENTSIZEFACTOR * effectiveHeight) / 2.0),
                currentY - ((SETTLEMENTSIZEFACTOR * effectiveHeight) / 2.0),
                (SETTLEMENTSIZEFACTOR * effectiveHeight),
                (SETTLEMENTSIZEFACTOR * effectiveHeight));
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
    private void renderHex(IGameHex hex, double currentX, double currentY, double effectiveHeight, GraphicsContext mapCtx) {
        if (setHexColour(hex, mapCtx)) return;

        drawHex(currentX, currentY, effectiveHeight, mapCtx);

        if (hex.isRobberOnField())
            drawRobber(currentX, currentY, effectiveHeight, mapCtx);

        if (hex instanceof IHarborHex)
            drawHarbor(currentX, currentY, (IHarborHex) hex, effectiveHeight, mapCtx);
    }

    /**
     * setHexColour Method
     * <p>
     * This method sets the colour according to a hex tile.
     *
     * @param hex    The hex tile the colour should be set accordingly to
     * @param mapCtx A GraphicsContext needed to set the colour
     * @return True if the colour couldn't be set, false otherwise
     */
    private boolean setHexColour(IGameHex hex, GraphicsContext mapCtx) {
        if (hex instanceof IWaterHex) {
            mapCtx.setFill(Color.CORNFLOWERBLUE);
        } else if (hex instanceof DesertHex) {
            mapCtx.setFill(Color.WHITE);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Hills) {
            mapCtx.setFill(Color.DARKORANGE);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Forest) {
            mapCtx.setFill(Color.DARKGREEN);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Mountains) {
            mapCtx.setFill(Color.DARKGREY);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Fields) {
            mapCtx.setFill(Color.YELLOW);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Pasture) {
            mapCtx.setFill(Color.LIGHTGREEN);
        } else {
            return true;
        }
        return false;
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
        double[] xCords = {currentX,
                currentX + (HEXWIDTHFACTOR * effectiveHeight) / 2,
                currentX + (HEXWIDTHFACTOR * effectiveHeight),
                currentX + (HEXWIDTHFACTOR * effectiveHeight),
                currentX + (HEXWIDTHFACTOR * effectiveHeight) / 2,
                currentX};
        double[] yCords = {currentY + ((HEXHEIGHTFACTOR * effectiveHeight) / 4),
                currentY,
                currentY + ((HEXHEIGHTFACTOR * effectiveHeight) / 4),
                currentY + ((HEXHEIGHTFACTOR * effectiveHeight) / 4) * 3,
                currentY + (HEXHEIGHTFACTOR * effectiveHeight),
                currentY + ((HEXHEIGHTFACTOR * effectiveHeight) / 4) * 3};
        mapCtx.fillPolygon(xCords, yCords, 6);
        mapCtx.setStroke(Color.BLACK);
        mapCtx.setLineWidth(2);
        mapCtx.strokePolygon(xCords, yCords, 6);
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
    private void drawHarbor(double currentX, double currentY, IHarborHex hex, double effectiveHeight, GraphicsContext mapCtx) {
        mapCtx.setStroke(Color.SLATEGREY);
        mapCtx.setLineWidth((HEXWIDTHFACTOR * effectiveHeight) / 5.0);
        switch (hex.getSide()) {
            case 0:
                mapCtx.strokeLine(currentX + (HEXWIDTHFACTOR * effectiveHeight) * (1.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (11.0 / 16.0),
                        currentX + (HEXWIDTHFACTOR * effectiveHeight) * (1.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (5.0 / 16.0));
                break;
            case 1:
                mapCtx.strokeLine(currentX + (HEXWIDTHFACTOR * effectiveHeight) * (1.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (2.0 / 8.0),
                        currentX + (HEXWIDTHFACTOR * effectiveHeight) * (4.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (1.0 / 16.0));
                break;
            case 2:
                mapCtx.strokeLine(currentX + (HEXWIDTHFACTOR * effectiveHeight) * (4.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (1.0 / 16.0),
                        currentX + (HEXWIDTHFACTOR * effectiveHeight) * (7.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (2.0 / 8.0));
                break;
            case 3:
                mapCtx.strokeLine(currentX + (HEXWIDTHFACTOR * effectiveHeight) * (7.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (5.0 / 16.0),
                        currentX + (HEXWIDTHFACTOR * effectiveHeight) * (7.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (11.0 / 16.0));
                break;
            case 4:
                mapCtx.strokeLine(currentX + (HEXWIDTHFACTOR * effectiveHeight) * (7.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (11.0 / 16.0),
                        currentX + (HEXWIDTHFACTOR * effectiveHeight) * (4.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (7.0 / 8.0));
                break;
            case 5:
                mapCtx.strokeLine(currentX + (HEXWIDTHFACTOR * effectiveHeight) * (4.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (7.0 / 8.0),
                        currentX + (HEXWIDTHFACTOR * effectiveHeight) * (1.0 / 8.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (11.0 / 16.0));
                break;
        }
        String text = "";
        switch (hex.getResource()) {
            case Brick:
                text = "Brick";
                break;
            case Lumber:
                text = "Lumber";
                break;
            case Ore:
                text = "Ore";
                break;
            case Grain:
                text = "Grain";
                break;
            case Wool:
                text = "Wool";
                break;
            case Any:
                text = "Any";
                break;
        }
        mapCtx.setFill(Color.BLACK);
        mapCtx.fillText(text, currentX + (HEXWIDTHFACTOR * effectiveHeight) / 8.0,
                currentY + (HEXHEIGHTFACTOR * effectiveHeight) / 2.0);
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
        mapCtx.setLineWidth((ROBBERLINEWIDTHFACTOR * effectiveHeight));
        mapCtx.setStroke(Color.BLACK);
        mapCtx.strokePolygon(new double[]{currentX + (HEXWIDTHFACTOR * effectiveHeight) * (1.0 / 4.0),
                        currentX + (HEXWIDTHFACTOR * effectiveHeight) * (3.0 / 4.0),
                        currentX + (HEXWIDTHFACTOR * effectiveHeight) * (2.0 / 4.0)},
                new double[]{currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (2.75 / 4.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (2.75 / 4.0),
                        currentY + (HEXHEIGHTFACTOR * effectiveHeight) * (1.125 / 4.0)},
                3);
    }
}
