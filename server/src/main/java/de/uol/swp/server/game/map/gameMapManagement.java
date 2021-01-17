package de.uol.swp.server.game.map;

import java.util.HashMap;
import java.util.Map;

/**
 * Management of the gameMap
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class gameMapManagement implements iGameMapManagement {

    //Warnungen wegen "Raw use of parameterised class 'Map' und
    // ab Z.37 "Unchecked call to 'put(K, V)' as a member of raw type 'java.util.Map'
    Map hexes = new HashMap<Integer, iGameHex>();

    Map edges = new HashMap<Integer, iEdge>(); //Amount: 72

    Map intersections = new HashMap<Integer, iIntersection>(); //Amount: 54

    int robberPosition = 37;

    /**
     * Creates the beginner's map as shown in the manual
     * of "Die Siedler von Catan" [Art.-Nr.: 684617]
     */
    public gameMapManagement() {
        //Creating the hexes
        //
        //Circle of water and harbor hexes (clockwise)
        hexes.put(1, new harborHex(1, iGameHex.iHarborHex.resource.Ore));
        hexes.put(2, new waterHex());
        hexes.put(3, new harborHex(2, iGameHex.iHarborHex.resource.Wool));
        hexes.put(4, new waterHex());
        hexes.put(5, new harborHex(3, iGameHex.iHarborHex.resource.Any));
        hexes.put(6, new waterHex());
        hexes.put(7, new harborHex(12, iGameHex.iHarborHex.resource.Any));
        hexes.put(8, new waterHex());
        hexes.put(9, new harborHex(19, iGameHex.iHarborHex.resource.Grain));
        hexes.put(10, new waterHex());
        hexes.put(11, new harborHex(19, iGameHex.iHarborHex.resource.Any));
        hexes.put(12, new waterHex());
        hexes.put(13, new harborHex(17, iGameHex.iHarborHex.resource.Brick));
        hexes.put(14, new waterHex());
        hexes.put(15, new harborHex(8, iGameHex.iHarborHex.resource.Any));
        hexes.put(16, new waterHex());
        hexes.put(17, new harborHex(4, iGameHex.iHarborHex.resource.Lumber));
        hexes.put(18, new waterHex());
        //Outer circle of resource hexes (clockwise)
        hexes.put(19, new resourceHex(iGameHex.iResourceHex.resource.Fields, 4, new int[]{1, 2, 20, 31, 30, 18}));
        hexes.put(20, new resourceHex(iGameHex.iResourceHex.resource.Forest, 6, new int[]{2, 3, 21, 32, 31, 19}));
        hexes.put(21, new resourceHex(iGameHex.iResourceHex.resource.Fields, 9, new int[]{3, 4, 5, 22, 32, 20}));
        hexes.put(22, new resourceHex(iGameHex.iResourceHex.resource.Pasture, 4, new int[]{21, 5, 6, 23, 33, 32}));
        hexes.put(23, new resourceHex(iGameHex.iResourceHex.resource.Pasture, 10, new int[]{22, 6, 7, 8, 24, 33}));
        hexes.put(24, new resourceHex(iGameHex.iResourceHex.resource.Forest, 11, new int[]{33, 23, 8, 9, 25, 34}));
        hexes.put(25, new resourceHex(iGameHex.iResourceHex.resource.Mountains, 11, new int[]{34, 24, 9, 10, 11, 26}));
        hexes.put(26, new resourceHex(iGameHex.iResourceHex.resource.Fields, 6, new int[]{35, 34, 25, 11, 12, 27}));
        hexes.put(27, new resourceHex(iGameHex.iResourceHex.resource.Fields, 3, new int[]{28, 35, 26, 12, 13, 14}));
        hexes.put(28, new resourceHex(iGameHex.iResourceHex.resource.Forest, 3, new int[]{29, 36, 35, 27, 14, 15}));
        hexes.put(29, new resourceHex(iGameHex.iResourceHex.resource.Pasture, 9, new int[]{17, 30, 36, 28, 15, 16}));
        hexes.put(30, new resourceHex(iGameHex.iResourceHex.resource.Hills, 2, new int[]{18, 19, 31, 36, 29, 17}));
        //Inner circle of resource hexes (clockwise)
        hexes.put(31, new resourceHex(iGameHex.iResourceHex.resource.Forest, 5, new int[]{19, 20, 32, 37, 36, 30}));
        hexes.put(32, new resourceHex(iGameHex.iResourceHex.resource.Pasture, 12, new int[]{20, 21, 22, 33, 37, 31}));
        hexes.put(33, new resourceHex(iGameHex.iResourceHex.resource.Mountains, 8, new int[]{32, 22, 23, 24, 34, 37}));
        hexes.put(34, new resourceHex(iGameHex.iResourceHex.resource.Hills, 10, new int[]{37, 33, 24, 25, 26, 35}));
        hexes.put(35, new resourceHex(iGameHex.iResourceHex.resource.Mountains, 5, new int[]{36, 37, 34, 26, 27, 28}));
        hexes.put(36, new resourceHex(iGameHex.iResourceHex.resource.Hills, 8, new int[]{30, 31, 37, 35, 28, 29}));
        //Desert field in the middle
        hexes.put(37, new desertHex(new int[]{31, 32, 33, 34, 35, 36}));
        //----------------------------------------------------------------------------------------------------
        //Creating the edges
        //
        //Circle of coast edges (clockwise)
        edges.put(1, new edge(new int[]{2, 30}, 0));
        //...
        //----------------------------------------------------------------------------------------------------
        //Creating the intersections
        //
        //Circle of coast intersections (clockwise)
        intersections.put(1, new intersection(new int[]{2, 30}, "f"));
        //...
        //----------------------------------------------------------------------------------------------------

        //todo: Gucken, obs eine schönere Möglichkeit gibt. Gehe davon aus, dass es so funktionieren würde,
        //      aber man müsste (71) + (53) weitere Zeilen hinzufügen :D.

    }

    @Override
    public iGameHex getHex(int place) {
        return (iGameHex) hexes.get(place);
    }

    @Override
    public boolean placeSettlement(int player, int position) {

        intersection i = (intersection) intersections.get(position);
        if (settlementPlaceable(player, position)) {
            i.setState(player + "s");
            for (int iPos : i.getNeighbours()) {
                intersection i2 = (intersection) intersections.get(iPos);
                i2.setState("b");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean placeRoad(int player, int position) {

        if (roadPlaceable(player, position)) {
            edge e = (edge) edges.get(position);
            e.setState(player);
            return true;
        }
        return false;
    }

    @Override
    public void moveRobber(int newHex) {
        robberPosition = newHex;
    }

    @Override
    public boolean upgradeSettlement(int player, int position) {
        intersection i = (intersection) intersections.get(position);
        if (i.getState().equals(player + "s")) {
            i.setState(player + "c");
        }
        return false;
    }

    @Override
    public boolean settlementPlaceable(int player, int position) {
        intersection i = (intersection) intersections.get(position);
        return i.getState().equals("f");
    }

    @Override
    public boolean roadPlaceable(int player, int position) {
        edge e = (edge) edges.get(position);
        boolean isBuildable = false;
        if (e.getState() == 0) {
            for (int ePos : e.getNeighbours()) {
                edge e2 = (edge) edges.get(ePos);
                if (e2.getState() == player) isBuildable = true;
            }
        }
        return isBuildable;
    }
}
