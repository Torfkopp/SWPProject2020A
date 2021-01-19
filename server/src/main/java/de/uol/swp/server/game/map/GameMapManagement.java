package de.uol.swp.server.game.map;

/**
 * Management of the gameMap
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public class GameMapManagement implements IGameMapManagement {

    //The first index (0) of each array is left open to ease mapping
    IGameHex[] hexes = new IGameHex[38]; //37 tiles
    IEdge[] edges = new IEdge[73]; //72 edges
    IIntersection[] intersections = new IIntersection[55]; //54 intersections

    int robberPosition = 37;

    /**
     * Creates the beginner's map as shown in the manual
     * of "Die Siedler von Catan" [Art.-Nr.: 684617]
     */
    public GameMapManagement() {
        //Creating the hexes
        //
        //Circle of water and harbor hexes (clockwise)
        hexes[1] = new HarborHex(1, IGameHex.IHarborHex.resource.Ore);
        hexes[2] = new WaterHex();
        hexes[3] = new HarborHex(2, IGameHex.IHarborHex.resource.Wool);
        hexes[4] = new WaterHex();
        hexes[5] = new HarborHex(3, IGameHex.IHarborHex.resource.Any);
        hexes[6] = new WaterHex();
        hexes[7] = new HarborHex(12, IGameHex.IHarborHex.resource.Any);
        hexes[8] = new WaterHex();
        hexes[9] = new HarborHex(19, IGameHex.IHarborHex.resource.Grain);
        hexes[10] = new WaterHex();
        hexes[11] = new HarborHex(19, IGameHex.IHarborHex.resource.Any);
        hexes[12] = new WaterHex();
        hexes[13] = new HarborHex(17, IGameHex.IHarborHex.resource.Brick);
        hexes[14] = new WaterHex();
        hexes[15] = new HarborHex(8, IGameHex.IHarborHex.resource.Any);
        hexes[16] = new WaterHex();
        hexes[17] = new HarborHex(4, IGameHex.IHarborHex.resource.Lumber);
        hexes[18] = new WaterHex();
        //Outer circle of resource hexes (clockwise)
        hexes[19] = new ResourceHex(IGameHex.IResourceHex.resource.Fields, 4);
        hexes[20] = new ResourceHex(IGameHex.IResourceHex.resource.Forest, 6);
        hexes[21] = new ResourceHex(IGameHex.IResourceHex.resource.Fields, 9);
        hexes[22] = new ResourceHex(IGameHex.IResourceHex.resource.Pasture, 4);
        hexes[23] = new ResourceHex(IGameHex.IResourceHex.resource.Pasture, 10);
        hexes[24] = new ResourceHex(IGameHex.IResourceHex.resource.Forest, 11);
        hexes[25] = new ResourceHex(IGameHex.IResourceHex.resource.Mountains, 11);
        hexes[26] = new ResourceHex(IGameHex.IResourceHex.resource.Fields, 6);
        hexes[27] = new ResourceHex(IGameHex.IResourceHex.resource.Fields, 3);
        hexes[28] = new ResourceHex(IGameHex.IResourceHex.resource.Forest, 3);
        hexes[29] = new ResourceHex(IGameHex.IResourceHex.resource.Pasture, 9);
        hexes[30] = new ResourceHex(IGameHex.IResourceHex.resource.Hills, 2);
        //Inner circle of resource hexes (clockwise)
        hexes[31] = new ResourceHex(IGameHex.IResourceHex.resource.Forest, 5);
        hexes[32] = new ResourceHex(IGameHex.IResourceHex.resource.Pasture, 12);
        hexes[33] = new ResourceHex(IGameHex.IResourceHex.resource.Mountains, 8);
        hexes[34] = new ResourceHex(IGameHex.IResourceHex.resource.Hills, 10);
        hexes[35] = new ResourceHex(IGameHex.IResourceHex.resource.Mountains, 5);
        hexes[36] = new ResourceHex(IGameHex.IResourceHex.resource.Hills, 8);
        //Desert field in the middle
        hexes[37] = new DesertHex();
        //----------------------------------------------------------------------------------------------------
        //Creating the edges
        //
        //Circle of coast edges (clockwise)
        edges[1] = new Edge(new int[]{1, 2}, new int[]{2, 30}, 0);
        //...
        //----------------------------------------------------------------------------------------------------
        //Creating the intersections
        //
        //Circle of coast intersections (clockwise)
        intersections[1] = new Intersection(new int[]{1, 18, 19}, new int[]{2, 30}, "f");
        //...
        //----------------------------------------------------------------------------------------------------

        //todo: Gucken, obs eine schönere Möglichkeit gibt. Gehe davon aus, dass es so funktionieren würde,
        //      aber man müsste (71) + (53) weitere Zeilen hinzufügen :D.

    }

    @Override
    public IGameHex getHex(int place) {
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
    public int getRobberPos() {
        return robberPosition;
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
        if (!isBuildable) {
            for (int i : edges[player].getNeiInt()) {
                if (intersections[i].getState().charAt(0) == player) isBuildable = true;
            }
        }
        return isBuildable;
    }
}
