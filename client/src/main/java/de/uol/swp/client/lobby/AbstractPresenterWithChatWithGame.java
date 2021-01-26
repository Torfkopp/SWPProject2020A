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
    protected Canvas gameMapCanvas;
    protected GraphicsContext mapCtx;
    protected Double hexHeight;
    protected Double hexWidth;
    private Double settlementSize;
    private Double citySize;

    @Override
    @FXML
    protected void initialize() {
        super.initialize();
        hexHeight = gameMapCanvas.getHeight() / 7.0;
        hexWidth = (Math.sqrt(3) / 2) * hexHeight;
        settlementSize =  hexHeight / 4.0;
        citySize = hexHeight / 3.5;
        mapCtx = gameMapCanvas.getGraphicsContext2D();
    }

    protected void renderGameMap(GameMapManagement gameMapManagement) {
        IGameHex[][] hexes = gameMapManagement.getHexesAsJaggedArray();
        IIntersection[][] intersections = gameMapManagement.getIntersectionsAsJaggedArray();
        double width = gameMapCanvas.getWidth();
        double height = gameMapCanvas.getHeight();
        int maxHexesInRow = hexes[4].length + 1;
        int maxIntersectionsInRow = 11;

        double currentX = 0;
        double currentY = hexHeight / 2.0;
        double startX = hexWidth / 2.0;

        mapCtx.setFill(Color.LIGHTBLUE);
        mapCtx.fillRect(0, 0, width, height);
        mapCtx.setStroke(Color.BLACK);
        mapCtx.strokeRect(0, 0, width, height);

        //Terrains
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

        //Intersections
        currentY = (hexHeight / 2.0 ) + ((hexHeight / 4.0) * 3.0);
        for (int y = 0; y < intersections.length; y++){
            currentX = startX + (hexWidth / 2.0);
            currentX += ((maxIntersectionsInRow - intersections[y].length) / 4.0 ) * hexWidth;

            for (int x = 1; x < intersections[y].length; x = x + 2){
                renderIntersection(currentX, currentY, intersections[y][x]);
                currentX += hexWidth;
            }
            currentY += (hexHeight / 4.0);
            for (int x = 1; x < intersections[y].length; x = x + 2){
                renderIntersection(currentX, currentY, intersections[y][x]);
                currentX += hexWidth;
            }
            currentY = ((hexHeight / 4.0) * 3.0);
        }

        //Edges
        //Intersections
        //Points and Robber

    }

    private void renderIntersection(double x, double y, IIntersection intersection){
        String state = intersection.getState();
        if(state.equals("f")){ //Free
            //Nothing to do here
        } else if(state.equals("b")) { //Blocked
            //TODO: Render something here
        } else if(state.endsWith("s")){ //Settlement
            setColor(state);
            fillSettlement(x, y);
        } else if(state.endsWith("c")){ //City
            setColor(state);
            fillCity(x, y);
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
            fillHexagon(x, y);
        } else if (hex instanceof DesertHex) {
            mapCtx.setFill(Color.WHITE);
            fillHexagon(x, y);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Hills) {
            mapCtx.setFill(Color.DARKORANGE);
            fillHexagon(x, y);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Forest) {
            mapCtx.setFill(Color.DARKGREEN);
            fillHexagon(x, y);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Mountains) {
            mapCtx.setFill(Color.DARKGREY);
            fillHexagon(x, y);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Fields) {
            mapCtx.setFill(Color.YELLOW);
            fillHexagon(x, y);
        } else if (hex instanceof IResourceHex && ((IResourceHex) hex).getResource() == IResourceHex.resource.Pasture) {
            mapCtx.setFill(Color.LIGHTGREEN);
            fillHexagon(x, y);
        }
    }

    private void fillHexagon(double x, double y) {
        double[] xCords = {x, x + hexWidth / 2, x + hexWidth, x + hexWidth, x + hexWidth / 2, x};
        double[] yCords = {y + (hexHeight / 4), y, y + (hexHeight / 4), y + (hexHeight / 4) * 3, y + hexHeight, y + (hexHeight / 4) * 3};
        mapCtx.fillPolygon(xCords, yCords, 6);
        mapCtx.strokePolygon(xCords, yCords, 6);
    }

    private void fillSettlement(double x, double y) {
        mapCtx.fillOval(x + settlementSize / 2.0, y + settlementSize / 2.0, settlementSize, settlementSize);
    }

    private void fillCity(double x, double y) {
        mapCtx.fillOval(x + settlementSize / 2.0, y + settlementSize / 2.0, settlementSize, settlementSize);
        mapCtx.setLineWidth((citySize-settlementSize) / 2.0);
        mapCtx.strokeOval(x + citySize / 2.0, y + citySize / 2.0, citySize, citySize);
    }
}
