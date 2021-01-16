package de.uol.swp.server.game;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class gameMap implements iGameMap{

    Map hexes = new HashMap<Integer, iGameHex>();
    Map harbors = new HashMap<Integer, iHarborHex>();

    Map roads = new HashMap<iGameHex, Integer[]>();
    Map intersections = new HashMap<iGameHex, Integer[]>();

    int robberPosition = 10;

    public gameMap(){
        //first row
        hexes.put(1, new gameHex(iGameHex.type.Fields, 4));
        hexes.put(2, new gameHex(iGameHex.type.Forest, 6));
        hexes.put(3, new gameHex(iGameHex.type.Fields,9));
        //second row
        hexes.put(4, new gameHex(iGameHex.type.Hills, 2));
        hexes.put(5, new gameHex(iGameHex.type.Forest, 5));
        hexes.put(6, new gameHex(iGameHex.type.Pasture, 12));
        hexes.put(7, new gameHex(iGameHex.type.Pasture, 4));
        //third row
        hexes.put(8, new gameHex(iGameHex.type.Pasture, 9));
        hexes.put(9, new gameHex(iGameHex.type.Hills,8));
        hexes.put(10, new gameHex(iGameHex.type.Desert, 0));
        hexes.put(11, new gameHex(iGameHex.type.Mountains, 8));
        hexes.put(12, new gameHex(iGameHex.type.Pasture, 10));
        //fourth row
        hexes.put(13, new gameHex(iGameHex.type.Forest, 3));
        hexes.put(14, new gameHex(iGameHex.type.Mountains, 5));
        hexes.put(15, new gameHex(iGameHex.type.Hills, 10));
        hexes.put(16, new gameHex(iGameHex.type.Forest, 11));
        //fifth row
        hexes.put(17, new gameHex(iGameHex.type.Fields, 3));
        hexes.put(18, new gameHex(iGameHex.type.Fields, 6));
        hexes.put(19, new gameHex(iGameHex.type.Mountains, 11));

        harbors.put(hexes.get(1), new harborHex(1 ,iHarborHex.resource.Ore));
        harbors.put(hexes.get(3), new harborHex(2, iHarborHex.resource.Wool));
        harbors.put(hexes.get(5), new harborHex(3, iHarborHex.resource.Any));
        harbors.put(hexes.get(7), new harborHex(12, iHarborHex.resource.Any));
        harbors.put(hexes.get(9), new harborHex(19, iHarborHex.resource.Grain));
        harbors.put(hexes.get(11), new harborHex(19, iHarborHex.resource.Any));
        harbors.put(hexes.get(13), new harborHex(17, iHarborHex.resource.Brick));
        harbors.put(hexes.get(15), new harborHex(8, iHarborHex.resource.Any));
        harbors.put(hexes.get(17), new harborHex(4, iHarborHex.resource.Lumber));

        for(int i = 1; i <= 19; i++){
            roads.put(hexes.get(i), new int[6]);
            intersections.put(hexes.get(i), new int[6]);
        }

        //TODO Vernünftige Speicherung der Straßen und Siedlungen
    }

    @Override
    public boolean placeSettlement(String colour, int position) {
        return false;
    }

    @Override
    public boolean placeStreet(String colour, int position) {
        return false;
    }

    @Override
    public boolean roadPlaceable(String colour, int position) {
        return false;
    }

    @Override
    public boolean settlementPlaceable(String colour, int position) {
        return false;
    }

    @Override
    public boolean upgradeSettlement(String colour, int position) {

    }

    @Override
    public iGameHex getHex(int place) {
        return (iGameHex) hexes.get(place);
    }

    @Override
    public void moveRobber(int newPosition) {
        robberPosition = newPosition;
    }

    public int getRobberPosition() {
        return robberPosition;
    }

}
