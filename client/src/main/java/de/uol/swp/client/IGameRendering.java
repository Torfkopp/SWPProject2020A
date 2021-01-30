package de.uol.swp.client;

import de.uol.swp.common.game.map.GameMapManagement;
import de.uol.swp.common.game.map.Hexes.*;
import de.uol.swp.common.game.map.IEdge;
import de.uol.swp.common.game.map.IIntersection;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.util.Arrays;

public interface IGameRendering {
    doubleWrapper hexHeight = new doubleWrapper();
    doubleWrapper hexWidth = new doubleWrapper();
    doubleWrapper settlementSize = new doubleWrapper();
    doubleWrapper citySize = new doubleWrapper();
    doubleWrapper roadWidth = new doubleWrapper();
    doubleWrapper startX = new doubleWrapper();
    intWrapper maxHexesInRow = new intWrapper();
    intWrapper maxIntersectionsInRow = new intWrapper();

    class intWrapper {
        int value;
        public intWrapper(){}
        public void set (int value) { this.value = value; }
        public int get() { return value; }
    }

    class doubleWrapper {
        double value;
        public doubleWrapper(){}
        public void set(double value) { this.value = value; }
        public double get() { return value; }
    }

    default void drawGameMap(GameMapManagement gameMapManagement, double width, double height, GraphicsContext mapCtx) {
        hexHeight.set(height / 7.0);
        hexWidth.set((Math.sqrt(3) / 2) * hexHeight.get());
        settlementSize.set(hexHeight.get() / 5.0);
        citySize.set(hexHeight.get() / 4.0);
        roadWidth.set(settlementSize.get() / 4.0);
        maxHexesInRow.set(7);
        maxIntersectionsInRow.set(11);
        //startX = hexWidth / 2.0;
        startX.set(0);

        IGameHex[][] hexes = gameMapManagement.getHexesAsJaggedArray();
        IIntersection[][] intersections = gameMapManagement.getIntersectionsAsJaggedArray();
        IEdge[][] edges = gameMapManagement.getEdgesAsJaggedArrayWithNullFiller();

        mapCtx.setFill(Color.LIGHTBLUE);
        mapCtx.fillRect(0, 0, width, height);
        mapCtx.setStroke(Color.BLACK);
        mapCtx.strokeRect(0, 0, width, height);

        drawHexTiles(hexes, mapCtx);
        drawIntersectionsAndEdges(intersections, edges, mapCtx);
    }

    private void drawHexTiles(IGameHex[][] hexes, GraphicsContext mapCtx) {
        double currentX = 0;
        double currentY = 0;
        //double currentY = hexHeight / 2.0;
        for (int y = 0; y < hexes.length; y++) {
            currentX = startX.get();
            //Set the indentation for the current row of hex tiles
            if (hexes[y].length % 2 == 0) { //Row with an even amount of hex tiles
                currentX += hexWidth.get() / 2.0;
                currentX += ((maxHexesInRow.get() - 1 - hexes[y].length) / 2.0) * hexWidth.get();
            } else {//Row with an odd amount of hex tiles
                currentX += ((maxHexesInRow.get() - hexes[y].length) / 2.0) * hexWidth.get();
            }
            for (int x = 0; x < hexes[y].length; x++) {
                renderHex(currentX, currentY, hexes[y][x], mapCtx);
                currentX += hexWidth.get();
            }
            currentY += (hexHeight.get() / 4) * 3;
        }
    }

    private void drawIntersectionsAndEdges(IIntersection[][] intersections, IEdge[][] edges, GraphicsContext mapCtx) {
        double currentX = startX.get();
        double currentY = 0;
        currentY = hexHeight.get() * (3.0 / 4.0);

        currentY = goThroughHalfMap(true, 0, 3, true,
                intersections, edges, currentY, mapCtx);
        currentY = goThroughHalfMap(false, 3, intersections.length, false,
                intersections, edges, currentY, mapCtx);
    }

    private double goThroughHalfMap(boolean topHalf, int startOn, int endOn, boolean startFirstSubRowWithOne,
                                    IIntersection[][] intersections, IEdge[][] edges, double currentY,
                                    GraphicsContext mapCtx) {
        double currentX;
        for (int y = startOn; y < endOn; y++) {
            double rowStartX = ((maxIntersectionsInRow.get() - intersections[y].length) / 4.0) * hexWidth.get();
            currentX = rowStartX + hexWidth.get() + startX.get();
            if (topHalf) currentX += hexWidth.get() / 2.0;
            goThroughSubRow(startFirstSubRowWithOne ? 1 : 0, false, currentX, currentY, intersections[y], edges[y], mapCtx);

            currentX = rowStartX + hexWidth.get() + startX.get();
            if (!topHalf) currentX += hexWidth.get() / 2.0;
            currentY += (hexHeight.get() / 4.0);
            goThroughSubRow(startFirstSubRowWithOne ? 0 : 1, true, currentX, currentY, intersections[y], edges[y], mapCtx);
            currentY += hexHeight.get() / 2.0;
        }
        return currentY;
    }

    private void goThroughSubRow(int startOn, boolean renderEdges, double currentX, double currentY,
                                 IIntersection[] intersections, IEdge[] edges, GraphicsContext mapCtx) {
        for (int x = startOn, xEdges = 0; x < intersections.length; x = x + 2, xEdges = xEdges + 3) {
            if (renderEdges)
                renderEdges(currentX, currentY, Arrays.copyOfRange(edges, xEdges, xEdges + 3), mapCtx);
            renderIntersection(currentX, currentY, intersections[x], mapCtx);
            currentX += hexWidth.get();
        }
    }


    private Color getColor(String player) {
        if (player.startsWith("1") || player.equals("1")) {
            return Color.BLACK;
        } else if (player.startsWith("2") || player.equals("2")) {
            return Color.RED;
        } else if (player.startsWith("3") || player.equals("3")) {
            return Color.PINK;
        } else if (player.startsWith("4") || player.equals("4")) {
            return Color.WHITE;
        }
        return null;
    }

    private void renderEdges(double currentX, double currentY, IEdge[] edges, GraphicsContext mapCtx) {
        IEdge leftRoad = edges[0];
        IEdge downRoad = edges[1];
        IEdge rightRoad = edges[2];
        //leftRoad
        mapCtx.setLineWidth(roadWidth.get());

        if (leftRoad != null && leftRoad.getState() != 0) {
            mapCtx.setStroke(getColor(String.valueOf(leftRoad.getState())));
            mapCtx.strokeLine(currentX, currentY, currentX - (hexWidth.get() / 2.0), currentY - (hexHeight.get() / 4.0));
        }

        if (rightRoad != null && rightRoad.getState() != 0) {
            mapCtx.setStroke(getColor(String.valueOf(rightRoad.getState())));
            mapCtx.strokeLine(currentX, currentY, currentX + (hexWidth.get() / 2.0), currentY - (hexHeight.get() / 4.0));
        }

        if (downRoad != null && downRoad.getState() != 0) {
            mapCtx.setStroke(getColor(String.valueOf(downRoad.getState())));
            mapCtx.strokeLine(currentX, currentY, currentX, currentY + (hexHeight.get() / 2.0));
        }
    }

    private void renderIntersection(double x, double y, IIntersection intersection, GraphicsContext mapCtx) {
        String state = intersection.getState();
        if (state.equals("f")) { //Free intersection
            //Free intersections don't need to be marked, but it could easily be added here
        } else if (state.equals("b")) { //Blocked intersection
            //Blocked intersections don't need to be marked, but it could easily be added here
        } else if (state.endsWith("s")) { //Intersection with settlement
            mapCtx.setFill(getColor(state));
            mapCtx.fillOval(x - (settlementSize.get() / 2.0), y - (settlementSize.get() / 2.0), settlementSize.get(), settlementSize.get());
        } else if (state.endsWith("c")) { //Intersection with city
            mapCtx.setFill(getColor(state));
            mapCtx.fillRoundRect(x - (citySize.get() / 2.0), y - (citySize.get() / 2.0), citySize.get(), citySize.get(),
                    citySize.get() / 2.0, citySize.get() / 2.0);
        }
    }

    private void renderHex(double x, double y, IGameHex hex, GraphicsContext mapCtx) {
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
        double[] xCords = {x, x + hexWidth.get() / 2, x + hexWidth.get(), x + hexWidth.get(), x + hexWidth.get() / 2, x};
        double[] yCords = {y + (hexHeight.get() / 4), y, y + (hexHeight.get() / 4), y + (hexHeight.get() / 4) * 3,
                y + hexHeight.get(), y + (hexHeight.get() / 4) * 3};
        mapCtx.fillPolygon(xCords, yCords, 6);
        mapCtx.setStroke(Color.BLACK);
        mapCtx.setLineWidth(2);
        mapCtx.strokePolygon(xCords, yCords, 6);

        if (hex instanceof IHarborHex) {
            mapCtx.setStroke(Color.SLATEGREY);
            mapCtx.setLineWidth(hexWidth.get() / 5.0);
            switch (((IHarborHex) hex).getSide()) {
                case 0:
                    mapCtx.strokeLine(x + hexWidth.get() / 8.0, y + hexHeight.get() / 4.0, x + hexWidth.get() / 8.0, y + hexHeight.get() * (3.0 / 4.0));
                    break;
                case 1:
                    mapCtx.strokeLine(x + hexWidth.get() / 8.0, y + hexHeight.get() / 4.0, x + hexWidth.get() * (3.0 / 8.0), y);
                    break;
                case 2:
                    mapCtx.strokeLine(x + hexWidth.get() * (3.0 / 8.0), y, x + hexWidth.get() * (7.0 / 8.0), y + hexHeight.get() / 4.0);
                    break;
                case 3:
                    mapCtx.strokeLine(x + hexWidth.get() * (15.0 / 16.0), y + hexHeight.get() * (5.0 / 16.0), x + hexWidth.get() * (15.0 / 16.0), y + hexHeight.get() * (11.0 / 16.0));
                    break;
                case 4:
                    mapCtx.strokeLine(x + hexWidth.get() * (9.0 / 16.0), y + hexHeight.get() * (15.0 / 16.0), x + hexWidth.get() * (15.0 / 16.0), y + hexHeight.get() * (13.0 / 16.0));
                    break;
                case 5:
                    mapCtx.strokeLine(x + hexWidth.get() / 16.0, y + hexHeight.get() * (13.0 / 16.0), x + hexWidth.get() * (7.0 / 16.0), y + hexHeight.get() * (15.0 / 16.0));
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
                    text = "Whool";
                    break;
                case Any:
                    text = "Any";
                    break;
            }
            mapCtx.setFill(Color.BLACK);
            mapCtx.fillText(text, x + hexWidth.get() / 8.0, y + hexHeight.get() / 2.0);
            return;
        }
    }
}
