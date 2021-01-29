package de.uol.swp.client;

import de.uol.swp.common.game.map.GameMapManagement;
import de.uol.swp.common.game.map.Hexes.DesertHex;
import de.uol.swp.common.game.map.Hexes.IGameHex;
import de.uol.swp.common.game.map.Hexes.IResourceHex;
import de.uol.swp.common.game.map.Hexes.IWaterHex;
import de.uol.swp.common.game.map.IIntersection;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public interface IGameRendering {

    default void renderGameMap(GameMapManagement gameMapManagement, double width, double height, GraphicsContext mapCtx) {
        double hexHeight, hexWidth, settlementSize, citySize, startX;
        int maxHexesInRow, maxIntersectionsInRow;

        hexHeight = height / 7.0;
        hexWidth = (Math.sqrt(3) / 2) * hexHeight;
        settlementSize = hexHeight / 5.0;
        citySize = hexHeight / 4.0;
        maxHexesInRow = 7;
        maxIntersectionsInRow = 11;
        //startX = hexWidth / 2.0;
        startX = 0;

        IGameHex[][] hexes = gameMapManagement.getHexesAsJaggedArray();
        IIntersection[][] intersections = gameMapManagement.getIntersectionsAsJaggedArray();

        mapCtx.setFill(Color.LIGHTBLUE);
        mapCtx.fillRect(0, 0, width, height);
        mapCtx.setStroke(Color.BLACK);
        mapCtx.strokeRect(0, 0, width, height);

        drawHexTiles(hexes, startX, hexWidth, hexHeight, maxHexesInRow, mapCtx);
        drawIntersections(intersections, startX, hexWidth, hexHeight, maxIntersectionsInRow, settlementSize, citySize, mapCtx);

        //Edges
        //Robber

    }

    private void drawHexTiles(IGameHex[][] hexes,
                              double startX, double hexWidth, double hexHeight,
                              int maxHexesInRow, GraphicsContext mapCtx) {
        double currentX = 0;
        double currentY = 0;
        //double currentY = hexHeight / 2.0;
        for (int y = 0; y < hexes.length; y++) {
            currentX = startX;
            //Set the indentation for the current row of hex tiles
            if (hexes[y].length % 2 == 0) { //Row with an even amount of hex tiles
                currentX += hexWidth / 2.0;
                currentX += ((maxHexesInRow - 1 - hexes[y].length) / 2.0) * hexWidth;
            } else {//Row with an odd amount of hex tiles
                currentX += ((maxHexesInRow - hexes[y].length) / 2.0) * hexWidth;
            }
            for (int x = 0; x < hexes[y].length; x++) {
                renderHex(currentX, currentY, hexes[y][x], hexWidth, hexHeight, mapCtx);
                currentX += hexWidth;
            }
            currentY += (hexHeight / 4) * 3;
        }
    }

    private void drawIntersections(IIntersection[][] intersections,
                                   double startX, double hexWidth, double hexHeight, int maxIntersectionsInRow,
                                   double settlementSize, double citySize, GraphicsContext mapCtx) {
        double currentX = startX;
        double currentY = 0;
        currentY = hexHeight * (3.0 / 4.0);
        //For loop for the intersections in the top half of the map
        for (int y = 0; y < 3; y++) {
            double rowStartX = ((maxIntersectionsInRow - intersections[y].length) / 4.0) * hexWidth;
            currentX = rowStartX + hexWidth + startX + hexWidth / 2.0;
            for (int x = 1; x < intersections[y].length; x = x + 2) {
                renderIntersection(currentX, currentY, intersections[y][x], settlementSize, citySize, mapCtx);
                currentX += hexWidth;
            }

            currentX = rowStartX + hexWidth;
            currentY += (hexHeight / 4.0);
            for (int x = 0; x < intersections[y].length; x = x + 2) {
                renderIntersection(currentX, currentY, intersections[y][x], settlementSize, citySize, mapCtx);
                currentX += hexWidth;
            }
            currentY += hexHeight / 2.0;
        }

        //For loop for the intersections in the bottom half of the map
        for (int y = 3; y < intersections.length; y++) {
            double rowStartX = ((maxIntersectionsInRow - intersections[y].length) / 4.0) * hexWidth;
            currentX = rowStartX + hexWidth;
            for (int x = 0; x < intersections[y].length; x = x + 2) {
                renderIntersection(currentX, currentY, intersections[y][x], settlementSize, citySize, mapCtx);
                currentX += hexWidth;
            }

            currentX = rowStartX + hexWidth + startX + hexWidth / 2.0;
            currentY += (hexHeight / 4.0);
            for (int x = 1; x < intersections[y].length; x = x + 2) {
                renderIntersection(currentX, currentY, intersections[y][x], settlementSize, citySize, mapCtx);
                currentX += hexWidth;
            }
            currentY += hexHeight / 2.0;
        }
    }

    private void renderIntersection(double x, double y, IIntersection intersection,
                                    double settlementSize, double citySize, GraphicsContext mapCtx) {
        String state = intersection.getState();
        if (state.equals("f")) { //Free intersection
            //Free intersections don't need to be marked, but it could easily be added here
        } else if (state.equals("b")) { //Blocked intersection
            //Blocked intersections don't need to be marked, but it could easily be added here
        } else if (state.endsWith("s")) { //Intersection with settlement
            mapCtx.setFill(setColor(state));
            mapCtx.fillOval(x - (settlementSize / 2.0), y - (settlementSize / 2.0), settlementSize, settlementSize);
        } else if (state.endsWith("c")) { //Intersection with city
            mapCtx.setFill(setColor(state));
            mapCtx.fillRoundRect(x - (citySize / 2.0), y - (citySize / 2.0), citySize, citySize,
                    citySize / 2.0, citySize / 2.0);
        }
    }

    private Color setColor(String player) {
        if (player.startsWith("1")) {
            return Color.BLACK;
        } else if (player.startsWith("2")) {
            return Color.RED;
        } else if (player.startsWith("3")) {
            return Color.ORANGE;
        } else if (player.startsWith("4")) {
            return Color.WHITE;
        }
        return null;
    }

    private void renderHex(double x, double y, IGameHex hex, double hexWidth, double hexHeight, GraphicsContext mapCtx) {
        if (hex instanceof IWaterHex) {
            mapCtx.setFill(Color.BLUE);
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
        double[] xCords = {x, x + hexWidth / 2, x + hexWidth, x + hexWidth, x + hexWidth / 2, x};
        double[] yCords = {y + (hexHeight / 4), y, y + (hexHeight / 4), y + (hexHeight / 4) * 3,
                y + hexHeight, y + (hexHeight / 4) * 3};
        mapCtx.fillPolygon(xCords, yCords, 6);
        mapCtx.setStroke(Color.BLACK);
        mapCtx.setLineWidth(2);
        mapCtx.strokePolygon(xCords, yCords, 6);
    }
}
