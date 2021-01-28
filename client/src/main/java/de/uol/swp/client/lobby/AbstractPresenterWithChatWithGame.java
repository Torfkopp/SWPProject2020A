package de.uol.swp.client.lobby;

import de.uol.swp.client.AbstractPresenterWithChat;
import de.uol.swp.common.game.map.GameMapManagement;
import de.uol.swp.common.game.map.Hexes.DesertHex;
import de.uol.swp.common.game.map.Hexes.IGameHex;
import de.uol.swp.common.game.map.Hexes.IResourceHex;
import de.uol.swp.common.game.map.Hexes.IWaterHex;
import de.uol.swp.common.game.map.IIntersection;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

abstract public class AbstractPresenterWithChatWithGame extends AbstractPresenterWithChat {
    @FXML
    private Canvas gameMapCanvas;
    private GraphicsContext mapCtx;
    private double width, height, hexHeight, hexWidth, settlementSize, citySize, startX;
    private int maxHexesInRow, maxIntersectionsInRow;

    @Override
    @FXML
    protected void initialize() {
        super.initialize();
        width = gameMapCanvas.getWidth();
        height = gameMapCanvas.getHeight();
        hexHeight = gameMapCanvas.getHeight() / 7.0;
        hexWidth = (Math.sqrt(3) / 2) * hexHeight;
        settlementSize =  hexHeight / 5.0;
        citySize = hexHeight / 4.0;
        mapCtx = gameMapCanvas.getGraphicsContext2D();
        maxHexesInRow = 7;
        maxIntersectionsInRow = 11;
        //startX = hexWidth / 2.0;
        startX = 0;
    }

    protected void renderGameMap(GameMapManagement gameMapManagement) {
        IGameHex[][] hexes = gameMapManagement.getHexesAsJaggedArray();
        IIntersection[][] intersections = gameMapManagement.getIntersectionsAsJaggedArray();


        mapCtx.setFill(Color.LIGHTBLUE);
        mapCtx.fillRect(0, 0, width, height);
        mapCtx.setStroke(Color.BLACK);
        mapCtx.strokeRect(0, 0, width, height);

        drawHexTiles(hexes);
        drawIntersections(intersections);

        //Edges
        //Robber

    }

    private void drawHexTiles(IGameHex[][] hexes) {
        double currentX = 0;
        double currentY = 0;
        //double currentY = hexHeight / 2.0;
        for (int y = 0; y < hexes.length; y++) {
            currentX = startX;
            if (hexes[y].length % 2 == 0) {
                //Even Row
                currentX += hexWidth / 2.0;
                currentX += ((maxHexesInRow - 1 - hexes[y].length) / 2.0) * hexWidth;
            } else {
                //Odd Row
                currentX += ((maxHexesInRow - hexes[y].length) / 2.0) * hexWidth;
            }
            for (int x = 0; x < hexes[y].length; x++) {
                renderHex(currentX, currentY, hexes[y][x]);
                currentX += hexWidth;
            }
            currentY += (hexHeight / 4) * 3;
        }
    }

    private void drawIntersections(IIntersection[][] intersections) {
        double currentX = startX;
        double currentY = 0;
        int shorterRowFirst = 1;
        currentY = hexHeight * (3.0 / 4.0);
        for (int y = 0; y < 3; y++){
            double rowStartX = ((maxIntersectionsInRow - intersections[y].length) / 4.0 ) * hexWidth;
            currentX = rowStartX + hexWidth + startX + hexWidth / 2.0;
            for (int x = 1; x < intersections[y].length; x = x + 2){
                renderIntersection(currentX, currentY, intersections[y][x]);
                currentX += hexWidth;
            }

            currentX = rowStartX + hexWidth;
            currentY += (hexHeight / 4.0);
            for (int x = 0; x < intersections[y].length; x = x + 2){
                renderIntersection(currentX, currentY, intersections[y][x]);
                currentX += hexWidth;
            }
            currentY += hexHeight / 2.0;
        }

        for (int y = 3; y < intersections.length; y++){
            double rowStartX = ((maxIntersectionsInRow - intersections[y].length) / 4.0 ) * hexWidth;
            currentX = rowStartX + hexWidth;
            for (int x = 0; x < intersections[y].length; x = x + 2){
                renderIntersection(currentX, currentY, intersections[y][x]);
                currentX += hexWidth;
            }

            currentX = rowStartX + hexWidth + startX + hexWidth / 2.0;
            currentY += (hexHeight / 4.0);
            for (int x = 1; x < intersections[y].length; x = x + 2){
                renderIntersection(currentX, currentY, intersections[y][x]);
                currentX += hexWidth;
            }
            currentY += hexHeight / 2.0;
        }
    }

    private void renderIntersection(double x, double y, IIntersection intersection){
        String state = intersection.getState();
        System.out.println(state);
        if(state.equals("f")){ //Free
            //Nothing to do here
        } else if(state.equals("b")) { //Blocked
            //Maybe render something
        } else if(state.endsWith("s")){ //Settlement
            setColor(state);
            drawSettlement(x, y);
        } else if(state.endsWith("c")){ //City
            setColor(state);
            drawCity(x, y);
        }
    }

    private void setColor(String player){
        if (player.startsWith("1")){
            mapCtx.setFill(Color.BLACK);
        } else if (player.startsWith("2")){
            mapCtx.setFill(Color.RED);
        } else if (player.startsWith("3")){
            mapCtx.setFill(Color.ORANGE);
        } else if (player.startsWith("4")){
            mapCtx.setFill(Color.WHITE);
        }
    }

    private void renderHex(double x, double y, IGameHex hex) {
        mapCtx.setStroke(Color.BLACK);
        mapCtx.setLineWidth(2);
        if (hex instanceof IWaterHex) {
            mapCtx.setFill(Color.BLUE);
            Hexagon(x, y);
        } else if (hex instanceof DesertHex) {
            mapCtx.setFill(Color.WHITE);
            Hexagon(x, y);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Hills) {
            mapCtx.setFill(Color.DARKORANGE);
            Hexagon(x, y);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Forest) {
            mapCtx.setFill(Color.DARKGREEN);
            Hexagon(x, y);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Mountains) {
            mapCtx.setFill(Color.DARKGREY);
            Hexagon(x, y);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Fields) {
            mapCtx.setFill(Color.YELLOW);
            Hexagon(x, y);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Pasture) {
            mapCtx.setFill(Color.LIGHTGREEN);
            Hexagon(x, y);
        }
    }

    private void Hexagon(double x, double y) {
        double[] xCords = {x, x + hexWidth / 2, x + hexWidth, x + hexWidth, x + hexWidth / 2, x};
        double[] yCords = {y + (hexHeight / 4), y, y + (hexHeight / 4), y + (hexHeight / 4) * 3, y + hexHeight, y + (hexHeight / 4) * 3};
        mapCtx.fillPolygon(xCords, yCords, 6);
        mapCtx.strokePolygon(xCords, yCords, 6);
    }

    private void drawSettlement(double x, double y) {
        mapCtx.fillOval(x - (settlementSize / 2.0), y - (settlementSize / 2.0), settlementSize, settlementSize);
    }

    private void drawCity(double x, double y) {
        mapCtx.fillRoundRect(x - (citySize / 2.0), y - (citySize / 2.0), citySize, citySize, citySize / 2.0, citySize / 2.0);
    }
}
