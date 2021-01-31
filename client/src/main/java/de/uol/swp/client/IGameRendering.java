package de.uol.swp.client;

import de.uol.swp.common.game.map.GameMapManagement;
import de.uol.swp.common.game.map.Hexes.*;
import de.uol.swp.common.game.map.IEdge;
import de.uol.swp.common.game.map.IIntersection;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;

public interface IGameRendering {

    double HEXHEIGHTFACTOR = 1.0 / 5.5;
    double HEXWIDTHFACTOR = HEXHEIGHTFACTOR * (Math.sqrt(3) / 2);
    double SETTLEMENTSIZEFACTOR = HEXHEIGHTFACTOR / 5.0;
    double CITYSIZEFACTOR = SETTLEMENTSIZEFACTOR * 1.25;
    double ROADWIDTHFACTOR = SETTLEMENTSIZEFACTOR / 4.0;

    default void drawGameMap(GameMapManagement gameMapManagement, Canvas canvas) {
        double width = canvas.getWidth(), height = canvas.getHeight();
        GraphicsContext mapCtx = canvas.getGraphicsContext2D();

        //Sets an effectiveHeight depending on the height and width of the game map
        double effectiveHeight = (HEXWIDTHFACTOR * height * 7 < width) ? height : width / (7 * HEXHEIGHTFACTOR * (Math.sqrt(3) / 2));

        //Get hexes, intersections and edges in a usable format from the gameMapManagement
        IGameHex[][] hexes = gameMapManagement.getHexesAsJaggedArray();
        IIntersection[][] intersections = gameMapManagement.getIntersectionsAsJaggedArray();
        IEdge[][] edges = gameMapManagement.getEdgesAsJaggedArrayWithNullFiller();

        //Call functions to draw hexes, intersections and edges
        drawHexTiles(hexes, effectiveHeight, mapCtx);
        drawIntersectionsAndEdges(intersections, edges, effectiveHeight, mapCtx);
    }

    private void drawHexTiles(IGameHex[][] hexes, double effectiveHeight, GraphicsContext mapCtx) {
        double currentY = 0;
        //double currentY = hexHeight / 2.0;
        for (IGameHex[] hex : hexes) {
            double currentX = 0;
            //Set the indentation for the current row of hex tiles
            if (hex.length % 2 == 0) { //Row with an even amount of hex tiles
                currentX += (HEXWIDTHFACTOR * effectiveHeight) / 2.0;
                currentX += ((hexes[hexes.length / 2].length - 1 - hex.length) / 2.0) * (HEXWIDTHFACTOR * effectiveHeight);
            } else {//Row with an odd amount of hex tiles
                currentX += ((hexes[hexes.length / 2].length - hex.length) / 2.0) * (HEXWIDTHFACTOR * effectiveHeight);
            }

            for (IGameHex iGameHex : hex) {
                renderHex(currentX, currentY, iGameHex, effectiveHeight, mapCtx);
                currentX += (HEXWIDTHFACTOR * effectiveHeight);
            }
            currentY += ((HEXHEIGHTFACTOR * effectiveHeight) / 4) * 3;
        }
    }

    private void drawIntersectionsAndEdges(IIntersection[][] intersections, IEdge[][] edges, double effectiveHeight, GraphicsContext mapCtx) {
        goThroughHalfMap(true, true, intersections, edges, effectiveHeight, mapCtx);
        goThroughHalfMap(false, false, intersections, edges, effectiveHeight, mapCtx);
    }

    private void goThroughHalfMap(boolean topHalf, boolean startFirstSubRowWithOne,
                                  IIntersection[][] intersections, IEdge[][] edges, double effectiveHeight,
                                  GraphicsContext mapCtx) {
        double currentY = (topHalf) ? ((HEXHEIGHTFACTOR * effectiveHeight) * (3.0 / 4.0)) : ((effectiveHeight / 2) + ((HEXHEIGHTFACTOR * effectiveHeight) / 4));
        for (int y = ((topHalf) ? 0 : intersections.length / 2); y < ((topHalf) ? intersections.length / 2 : intersections.length); y++) {
            double rowStartX = ((intersections[intersections.length / 2].length - intersections[y].length) / 4.0) * (HEXWIDTHFACTOR * effectiveHeight);
            double currentX = rowStartX + (HEXWIDTHFACTOR * effectiveHeight);
            if (topHalf) currentX += (HEXWIDTHFACTOR * effectiveHeight) / 2.0;
            goThroughSubRow(startFirstSubRowWithOne ? 1 : 0, false, currentX, currentY, intersections[y], edges[y],
                    effectiveHeight, mapCtx);

            currentX = rowStartX + (HEXWIDTHFACTOR * effectiveHeight);
            if (!topHalf) currentX += (HEXWIDTHFACTOR * effectiveHeight) / 2.0;
            currentY += ((HEXHEIGHTFACTOR * effectiveHeight) / 4.0);
            goThroughSubRow(startFirstSubRowWithOne ? 0 : 1, true, currentX, currentY, intersections[y], edges[y],
                    effectiveHeight, mapCtx);
            currentY += (HEXHEIGHTFACTOR * effectiveHeight) / 2.0;
        }
    }

    private void goThroughSubRow(int startOn, boolean renderEdges, double currentX, double currentY,
                                 IIntersection[] intersections, IEdge[] edges, double effectiveHeight, GraphicsContext mapCtx) {
        for (int x = startOn, xEdges = 0; x < intersections.length; x = x + 2, xEdges = xEdges + 3) {
            if (renderEdges) {
                renderEdges(currentX, currentY, Arrays.copyOfRange(edges, xEdges, xEdges + 3), effectiveHeight, mapCtx);
            }
            renderIntersection(currentX, currentY, intersections[x], effectiveHeight, mapCtx);
            currentX += (HEXWIDTHFACTOR * effectiveHeight);
        }
    }

    private Color getPlayerColor(String player) {
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

    private Color getPlayerColor(int player) {
        return getPlayerColor(String.valueOf(player));
    }

    private void renderEdges(double currentX, double currentY, IEdge[] edges, double effectiveHeight, GraphicsContext mapCtx) {
        IEdge leftRoad = edges[0];
        IEdge downRoad = edges[1];
        IEdge rightRoad = edges[2];
        //leftRoad
        mapCtx.setLineWidth(ROADWIDTHFACTOR * effectiveHeight);

        if (leftRoad != null && leftRoad.getState() != 0) {
            mapCtx.setStroke(getPlayerColor(leftRoad.getState()));
            mapCtx.strokeLine(currentX, currentY, currentX - ((HEXWIDTHFACTOR * effectiveHeight) / 2.0), currentY - ((HEXHEIGHTFACTOR * effectiveHeight) / 4.0));
        }

        if (rightRoad != null && rightRoad.getState() != 0) {
            mapCtx.setStroke(getPlayerColor(rightRoad.getState()));
            mapCtx.strokeLine(currentX, currentY, currentX + ((HEXWIDTHFACTOR * effectiveHeight) / 2.0), currentY - ((HEXHEIGHTFACTOR * effectiveHeight) / 4.0));
        }

        if (downRoad != null && downRoad.getState() != 0) {
            mapCtx.setStroke(getPlayerColor(downRoad.getState()));
            mapCtx.strokeLine(currentX, currentY, currentX, currentY + ((HEXHEIGHTFACTOR * effectiveHeight) / 2.0));
        }
    }

    private void renderIntersection(double x, double y, IIntersection intersection, double effectiveHeight, GraphicsContext mapCtx) {
        String state = intersection.getState();
        if (state.equals("f")) { //Free intersection
            //Free intersections don't need to be marked, but it could easily be added here
        } else if (state.equals("b")) { //Blocked intersection
            //Blocked intersections don't need to be marked, but it could easily be added here
        } else if (state.endsWith("s")) { //Intersection with settlement
            mapCtx.setFill(getPlayerColor(state));
            mapCtx.fillOval(x - ((SETTLEMENTSIZEFACTOR * effectiveHeight) / 2.0), y - ((SETTLEMENTSIZEFACTOR * effectiveHeight) / 2.0), (SETTLEMENTSIZEFACTOR * effectiveHeight), (SETTLEMENTSIZEFACTOR * effectiveHeight));
        } else if (state.endsWith("c")) { //Intersection with city
            mapCtx.setFill(getPlayerColor(state));
            mapCtx.fillRoundRect(x - ((CITYSIZEFACTOR * effectiveHeight) / 2.0), y - ((CITYSIZEFACTOR * effectiveHeight) / 2.0), (CITYSIZEFACTOR * effectiveHeight), (CITYSIZEFACTOR * effectiveHeight),
                    (CITYSIZEFACTOR * effectiveHeight) / 2.0, (CITYSIZEFACTOR * effectiveHeight) / 2.0);
        }
    }

    private void renderHex(double x, double y, IGameHex hex, double effectiveHeight, GraphicsContext mapCtx) {
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
            return;
        }
        //Draw a hexagon in the set Color with a black 2px border around it
        double[] xCords = {x, x + (HEXWIDTHFACTOR * effectiveHeight) / 2, x + (HEXWIDTHFACTOR * effectiveHeight), x + (HEXWIDTHFACTOR * effectiveHeight), x + (HEXWIDTHFACTOR * effectiveHeight) / 2, x};
        double[] yCords = {y + ((HEXHEIGHTFACTOR * effectiveHeight) / 4), y, y + ((HEXHEIGHTFACTOR * effectiveHeight) / 4), y + ((HEXHEIGHTFACTOR * effectiveHeight) / 4) * 3,
                y + (HEXHEIGHTFACTOR * effectiveHeight), y + ((HEXHEIGHTFACTOR * effectiveHeight) / 4) * 3};
        mapCtx.fillPolygon(xCords, yCords, 6);
        mapCtx.setStroke(Color.BLACK);
        mapCtx.setLineWidth(2);
        mapCtx.strokePolygon(xCords, yCords, 6);

        //Draw Robber
        mapCtx.setLineWidth(5);
        mapCtx.setStroke(Color.BLACK);
        if (hex.isRobberOnField()) {
            mapCtx.strokePolygon( new double[]{x + (HEXWIDTHFACTOR * effectiveHeight) * (1.0 / 4.0),
                    x + (HEXWIDTHFACTOR * effectiveHeight) * (3.0 / 4.0),
                    x + (HEXWIDTHFACTOR * effectiveHeight) * (2.0 / 4.0)},
            new double[]{y + (HEXHEIGHTFACTOR * effectiveHeight) * (2.75 / 4.0),
                    y + (HEXHEIGHTFACTOR * effectiveHeight) * (2.75 / 4.0),
                    y + (HEXHEIGHTFACTOR * effectiveHeight) * (1.125 / 4.0)},
            3);
        }

        //Draw Harbors
        if (hex instanceof IHarborHex) {
            mapCtx.setStroke(Color.SLATEGREY);
            mapCtx.setLineWidth((HEXWIDTHFACTOR * effectiveHeight) / 5.0);
            switch (((IHarborHex) hex).getSide()) {
                case 0:
                    mapCtx.strokeLine(x + (HEXWIDTHFACTOR * effectiveHeight) * (1.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (11.0 / 16.0),
                            x + (HEXWIDTHFACTOR * effectiveHeight) * (1.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (5.0 / 16.0));
                    break;
                case 1:
                    mapCtx.strokeLine(x + (HEXWIDTHFACTOR * effectiveHeight) * (1.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (2.0 / 8.0),
                            x + (HEXWIDTHFACTOR * effectiveHeight) * (4.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (1.0 / 16.0));
                    break;
                case 2:
                    mapCtx.strokeLine(x + (HEXWIDTHFACTOR * effectiveHeight) * (4.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (1.0 / 16.0),
                            x + (HEXWIDTHFACTOR * effectiveHeight) * (7.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (2.0 / 8.0));
                    break;
                case 3:
                    mapCtx.strokeLine(x + (HEXWIDTHFACTOR * effectiveHeight) * (7.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (5.0 / 16.0),
                            x + (HEXWIDTHFACTOR * effectiveHeight) * (7.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (11.0 / 16.0));
                    break;
                case 4:
                    mapCtx.strokeLine(x + (HEXWIDTHFACTOR * effectiveHeight) * (7.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (11.0 / 16.0),
                            x + (HEXWIDTHFACTOR * effectiveHeight) * (4.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (7.0 / 8.0));
                    break;
                case 5:
                    mapCtx.strokeLine(x + (HEXWIDTHFACTOR * effectiveHeight) * (4.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (7.0 / 8.0),
                            x + (HEXWIDTHFACTOR * effectiveHeight) * (1.0 / 8.0),
                            y + (HEXHEIGHTFACTOR * effectiveHeight) * (11.0 / 16.0));
                    break;
            }
            String text = "";
            switch (((IHarborHex) hex).getResource()) {
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
            mapCtx.fillText(text, x + (HEXWIDTHFACTOR * effectiveHeight) / 8.0, y + (HEXHEIGHTFACTOR * effectiveHeight) / 2.0);
        }
    }
}
