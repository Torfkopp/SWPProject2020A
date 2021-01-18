package de.uol.swp.server.game.map;

/**
 * Management of the gameMap
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class gameMapManagement implements iGameMapManagement {

    //The first index (0) of each array is left open to ease mapping
    iGameHex[] hexes = new iGameHex[38]; //37 tiles
    iEdge[] edges = new iEdge[73]; //72 edges
    iIntersection[] intersections = new iIntersection[55]; //54 intersections

    int robberPosition = 37;

    /**
     * Creates the beginner's map as shown in the manual
     * of "Die Siedler von Catan" [Art.-Nr.: 684617]
     */
    public gameMapManagement() {
        //Creating the hexes
        //
        //Circle of water and harbor hexes (clockwise)
        hexes[1] = new harborHex(1, iGameHex.iHarborHex.resource.Ore);
        hexes[2] = new waterHex();
        hexes[3] = new harborHex(2, iGameHex.iHarborHex.resource.Wool);
        hexes[4] = new waterHex();
        hexes[5] = new harborHex(3, iGameHex.iHarborHex.resource.Any);
        hexes[6] = new waterHex();
        hexes[7] = new harborHex(12, iGameHex.iHarborHex.resource.Any);
        hexes[8] = new waterHex();
        hexes[9] = new harborHex(19, iGameHex.iHarborHex.resource.Grain);
        hexes[10] = new waterHex();
        hexes[11] = new harborHex(19, iGameHex.iHarborHex.resource.Any);
        hexes[12] = new waterHex();
        hexes[13] = new harborHex(17, iGameHex.iHarborHex.resource.Brick);
        hexes[14] = new waterHex();
        hexes[15] = new harborHex(8, iGameHex.iHarborHex.resource.Any);
        hexes[16] = new waterHex();
        hexes[17] = new harborHex(4, iGameHex.iHarborHex.resource.Lumber);
        hexes[18] = new waterHex();
        //Outer circle of resource hexes (clockwise)
        hexes[19] = new resourceHex(iGameHex.iResourceHex.resource.Fields, 4);
        hexes[20] = new resourceHex(iGameHex.iResourceHex.resource.Forest, 6);
        hexes[21] = new resourceHex(iGameHex.iResourceHex.resource.Fields, 9);
        hexes[22] = new resourceHex(iGameHex.iResourceHex.resource.Pasture, 4);
        hexes[23] = new resourceHex(iGameHex.iResourceHex.resource.Pasture, 10);
        hexes[24] = new resourceHex(iGameHex.iResourceHex.resource.Forest, 11);
        hexes[25] = new resourceHex(iGameHex.iResourceHex.resource.Mountains, 11);
        hexes[26] = new resourceHex(iGameHex.iResourceHex.resource.Fields, 6);
        hexes[27] = new resourceHex(iGameHex.iResourceHex.resource.Fields, 3);
        hexes[28] = new resourceHex(iGameHex.iResourceHex.resource.Forest, 3);
        hexes[29] = new resourceHex(iGameHex.iResourceHex.resource.Pasture, 9);
        hexes[30] = new resourceHex(iGameHex.iResourceHex.resource.Hills, 2);
        //Inner circle of resource hexes (clockwise)
        hexes[31] = new resourceHex(iGameHex.iResourceHex.resource.Forest, 5);
        hexes[32] = new resourceHex(iGameHex.iResourceHex.resource.Pasture, 12);
        hexes[33] = new resourceHex(iGameHex.iResourceHex.resource.Mountains, 8);
        hexes[34] = new resourceHex(iGameHex.iResourceHex.resource.Hills, 10);
        hexes[35] = new resourceHex(iGameHex.iResourceHex.resource.Mountains, 5);
        hexes[36] = new resourceHex(iGameHex.iResourceHex.resource.Hills, 8);
        //Desert field in the middle
        hexes[37] = new desertHex();
        //----------------------------------------------------------------------------------------------------
        //Creating the edges
        //
        //Circle of coast edges (clockwise)
        edges[1] = new edge(new int[]{2, 30}, 0);
        //...
        //----------------------------------------------------------------------------------------------------
        //Creating the intersections
        //
        //Circle of coast intersections (clockwise)
        intersections[1] = new intersection(new int[]{1, 18, 19}, new int[]{2, 30}, "f");
        //...
        //----------------------------------------------------------------------------------------------------

        //todo: Gucken, obs eine schönere Möglichkeit gibt. Gehe davon aus, dass es so funktionieren würde,
        //      aber man müsste (71) + (53) weitere Zeilen hinzufügen :D.

    }

    @Override
    public iGameHex getHex(int place) {
        return hexes[place];
    }

    @Override
    public boolean placeSettlement(int player, int position) {

        if (settlementPlaceable(player, position)) {
            intersections[player].setState(player + "s");
            for (int iPos : intersections[player].getNeighbours()) {
                intersections[iPos].setState("b");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean placeRoad(int player, int position) {

        if (roadPlaceable(player, position)) {
            edges[player].setState(player);
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
        if (intersections[player].getState().equals(player + "s")) {
            intersections[player].setState(player + "c");
        }
        return false;
    }

    @Override
    public boolean settlementPlaceable(int player, int position) {
        return intersections[position].getState().equals("f");
    }

    @Override
    public boolean roadPlaceable(int player, int position) {
        boolean isBuildable = false;
        if (edges[player].getState() == 0) {
            for (int ePos : edges[player].getNeighbours()) {
                if (edges[ePos].getState() == player) isBuildable = true;
            }
        }
        return isBuildable;
    }
}
