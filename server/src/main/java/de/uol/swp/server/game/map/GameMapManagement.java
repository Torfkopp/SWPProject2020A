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
        edges[2] = new Edge(new int[]{2, 3}, new int[]{1, 3, 31}, 0);
        edges[3] = new Edge(new int[]{3, 4}, new int[]{2, 4, 31}, 0);
        edges[4] = new Edge(new int[]{4, 5}, new int[]{3, 5, 32}, 0);
        edges[5] = new Edge(new int[]{5, 6}, new int[]{4, 6, 32}, 0);
        edges[6] = new Edge(new int[]{6, 7}, new int[]{5, 7}, 0);
        edges[7] = new Edge(new int[]{7, 8}, new int[]{6, 8, 33}, 0);
        edges[8] = new Edge(new int[]{8, 9}, new int[]{7, 9, 33}, 0);
        edges[9] = new Edge(new int[]{9, 10}, new int[]{8, 10, 34}, 0);
        edges[10] = new Edge(new int[]{10, 11}, new int[]{9, 11, 34}, 0);
        edges[11] = new Edge(new int[]{11, 12}, new int[]{10, 12}, 0);
        edges[12] = new Edge(new int[]{12, 13}, new int[]{11, 13, 35}, 0);
        edges[13] = new Edge(new int[]{13, 14}, new int[]{12, 14, 35}, 0);
        edges[14] = new Edge(new int[]{14, 15}, new int[]{13, 15, 36}, 0);
        edges[15] = new Edge(new int[]{15, 16}, new int[]{14, 16, 36}, 0);
        edges[16] = new Edge(new int[]{16, 17}, new int[]{15, 17}, 0);
        edges[17] = new Edge(new int[]{17, 18}, new int[]{16, 18, 37}, 0);
        edges[18] = new Edge(new int[]{18, 19}, new int[]{17, 19, 37}, 0);
        edges[19] = new Edge(new int[]{19, 20}, new int[]{18, 20, 38}, 0);
        edges[20] = new Edge(new int[]{20, 21}, new int[]{19, 21, 38}, 0);
        edges[21] = new Edge(new int[]{21, 22}, new int[]{20, 22}, 0);
        edges[22] = new Edge(new int[]{22, 23}, new int[]{21, 23, 39}, 0);
        edges[23] = new Edge(new int[]{23, 24}, new int[]{22, 24, 39}, 0);
        edges[24] = new Edge(new int[]{24, 25}, new int[]{23, 25, 40}, 0);
        edges[25] = new Edge(new int[]{25, 26}, new int[]{24, 26, 40}, 0);
        edges[26] = new Edge(new int[]{26, 27}, new int[]{25, 27}, 0);
        edges[27] = new Edge(new int[]{27, 28}, new int[]{26, 28, 41}, 0);
        edges[28] = new Edge(new int[]{28, 29}, new int[]{27, 29, 41}, 0);
        edges[29] = new Edge(new int[]{29, 30}, new int[]{28, 30, 42}, 0);
        edges[30] = new Edge(new int[]{30, 1}, new int[]{29, 1, 42}, 0);
        //Outer circle of edges
        edges[31] = new Edge(new int[]{3, 32}, new int[]{2, 3, 43, 44}, 0);
        edges[32] = new Edge(new int[]{5, 34}, new int[]{4, 5, 45, 46}, 0);
        edges[33] = new Edge(new int[]{8, 35}, new int[]{7, 8, 46, 47}, 0);
        edges[34] = new Edge(new int[]{10, 37}, new int[]{9, 10, 48, 49}, 0);
        edges[35] = new Edge(new int[]{13, 38}, new int[]{12, 13, 49, 50}, 0);
        edges[36] = new Edge(new int[]{15, 40}, new int[]{14, 15, 51, 52}, 0);
        edges[37] = new Edge(new int[]{18, 41}, new int[]{17, 18, 52, 53}, 0);
        edges[38] = new Edge(new int[]{20, 43}, new int[]{19, 20, 54, 55}, 0);
        edges[39] = new Edge(new int[]{23, 44}, new int[]{22, 23, 55, 56}, 0);
        edges[40] = new Edge(new int[]{25, 46}, new int[]{24, 25, 57, 58}, 0);
        edges[41] = new Edge(new int[]{28, 47}, new int[]{27, 28, 58, 59}, 0);
        edges[42] = new Edge(new int[]{30, 31}, new int[]{29, 30, 43, 60}, 0);
        //Middle circle of edges
        edges[43] = new Edge(new int[]{31, 32}, new int[]{31, 42, 44, 60}, 0);
        edges[44] = new Edge(new int[]{32, 33}, new int[]{31, 43, 45, 61}, 0);
        edges[45] = new Edge(new int[]{33, 34}, new int[]{32, 44, 46, 61}, 0);
        edges[46] = new Edge(new int[]{34, 35}, new int[]{32, 33, 45, 47}, 0);
        edges[47] = new Edge(new int[]{35, 36}, new int[]{33, 46, 48, 62}, 0);
        edges[48] = new Edge(new int[]{36, 37}, new int[]{34, 47, 49, 62}, 0);
        edges[49] = new Edge(new int[]{37, 38}, new int[]{34, 48, 50, 35}, 0);
        edges[50] = new Edge(new int[]{38, 39}, new int[]{35, 49, 51, 63}, 0);
        edges[51] = new Edge(new int[]{39, 40}, new int[]{36, 50, 52, 63}, 0);
        edges[52] = new Edge(new int[]{40, 41}, new int[]{36, 51, 53, 37}, 0);
        edges[53] = new Edge(new int[]{41, 42}, new int[]{37, 52, 54, 64}, 0);
        edges[54] = new Edge(new int[]{42, 43}, new int[]{38, 53, 55, 64}, 0);
        edges[55] = new Edge(new int[]{43, 44}, new int[]{38, 54, 56, 39}, 0);
        edges[56] = new Edge(new int[]{44, 45}, new int[]{39, 55, 57, 65}, 0);
        edges[57] = new Edge(new int[]{45, 46}, new int[]{40, 56, 58, 65}, 0);
        edges[58] = new Edge(new int[]{46, 47}, new int[]{40, 57, 59, 41}, 0);
        edges[59] = new Edge(new int[]{47, 48}, new int[]{41, 58, 60, 66}, 0);
        edges[60] = new Edge(new int[]{31, 48}, new int[]{42, 43, 59, 66}, 0);
        //Inner circle of edges
        edges[61] = new Edge(new int[]{33, 50}, new int[]{44, 45, 67, 68}, 0);
        edges[62] = new Edge(new int[]{36, 51}, new int[]{47, 48, 68, 69}, 0);
        edges[63] = new Edge(new int[]{39, 52}, new int[]{50, 51, 69, 70}, 0);
        edges[64] = new Edge(new int[]{42, 53}, new int[]{53, 54, 70, 71}, 0);
        edges[65] = new Edge(new int[]{45, 54}, new int[]{56, 57, 71, 72}, 0);
        edges[66] = new Edge(new int[]{48, 49}, new int[]{59, 60, 72, 67}, 0);
        //Innermost circle of edges around hex 37
        edges[67] = new Edge(new int[]{49, 50}, new int[]{61, 66, 68, 72}, 0);
        edges[68] = new Edge(new int[]{50, 51}, new int[]{61, 62, 67, 69}, 0);
        edges[69] = new Edge(new int[]{51, 52}, new int[]{62, 63, 68, 70}, 0);
        edges[70] = new Edge(new int[]{52, 53}, new int[]{63, 64, 69, 71}, 0);
        edges[71] = new Edge(new int[]{53, 54}, new int[]{64, 65, 70, 72}, 0);
        edges[72] = new Edge(new int[]{49, 54}, new int[]{65, 66, 67, 71}, 0);
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
