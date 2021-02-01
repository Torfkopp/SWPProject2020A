package de.uol.swp.common.game.map;

import de.uol.swp.common.game.map.Hexes.*;

/**
 * Management of the gameMap
 *
 * @author Mario Fokken
 * @author Steven Luong
 * @since 2021-01-16
 */
public class GameMapManagement implements IGameMapManagement {

    //The first index (0) of each array is left open to ease mapping
    private final IGameHex[] hexes = new IGameHex[38]; //37 tiles
    private final IEdge[] edges = new IEdge[73]; //72 edges
    private final IIntersection[] intersections = new IIntersection[55]; //54 intersections

    int robberPosition = 37;

    public GameMapManagement() {
        createBeginnerMap();
        createEdges();
        createIntersections();

        hexes[robberPosition].setRobberOnField(true);
    }

    @Override
    public IEdge[][] getEdgesAsJaggedArrayWithNullFiller() {
        IEdge[][] edgeArray;
        edgeArray = new IEdge[6][];
        edgeArray[0] = new IEdge[12];
        edgeArray[1] = new IEdge[15];
        edgeArray[2] = new IEdge[18];
        edgeArray[3] = new IEdge[15];
        edgeArray[4] = new IEdge[12];
        edgeArray[5] = new IEdge[9];

        edgeArray[0][0] = null;
        edgeArray[0][1] = edges[30];
        edgeArray[0][2] = edges[1];
        edgeArray[0][3] = edges[2];
        edgeArray[0][4] = edges[31];
        edgeArray[0][5] = edges[3];
        edgeArray[0][6] = edges[4];
        edgeArray[0][7] = edges[32];
        edgeArray[0][8] = edges[5];
        edgeArray[0][9] = edges[6];
        edgeArray[0][10] = edges[33];
        edgeArray[0][11] = null;

        edgeArray[1][0] = null;
        edgeArray[1][1] = edges[28];
        edgeArray[1][2] = edges[29];
        edgeArray[1][3] = edges[42];
        edgeArray[1][4] = edges[60];
        edgeArray[1][5] = edges[43];
        edgeArray[1][6] = edges[44];
        edgeArray[1][7] = edges[61];
        edgeArray[1][8] = edges[45];
        edgeArray[1][9] = edges[46];
        edgeArray[1][10] = edges[1];
        edgeArray[1][11] = edges[62];
        edgeArray[1][12] = edges[33];
        edgeArray[1][13] = edges[8];
        edgeArray[1][14] = null;

        edgeArray[2][0] = null;
        edgeArray[2][1] = edges[26];
        edgeArray[2][2] = edges[27];
        edgeArray[2][3] = edges[41];
        edgeArray[2][4] = edges[58];
        edgeArray[2][5] = edges[59];
        edgeArray[2][6] = edges[66];
        edgeArray[2][7] = edges[72];
        edgeArray[2][8] = edges[67];
        edgeArray[2][9] = edges[68];
        edgeArray[2][10] = edges[69];
        edgeArray[2][11] = edges[62];
        edgeArray[2][12] = edges[48];
        edgeArray[2][13] = edges[49];
        edgeArray[2][14] = edges[34];
        edgeArray[2][15] = edges[10];
        edgeArray[2][16] = edges[11];
        edgeArray[2][17] = null;

        edgeArray[3][0] = edges[25];
        edgeArray[3][1] = edges[24];
        edgeArray[3][2] = edges[40];
        edgeArray[3][3] = edges[57];
        edgeArray[3][4] = edges[56];
        edgeArray[3][5] = edges[65];
        edgeArray[3][6] = edges[71];
        edgeArray[3][7] = edges[64];
        edgeArray[3][8] = edges[70];
        edgeArray[3][9] = edges[63];
        edgeArray[3][10] = edges[51];
        edgeArray[3][11] = edges[50];
        edgeArray[3][12] = edges[35];
        edgeArray[3][13] = edges[13];
        edgeArray[3][14] = edges[12];

        edgeArray[4][0] = edges[23];
        edgeArray[4][1] = edges[22];
        edgeArray[4][2] = edges[39];
        edgeArray[4][3] = edges[55];
        edgeArray[4][4] = edges[38];
        edgeArray[4][5] = edges[54];
        edgeArray[4][6] = edges[53];
        edgeArray[4][7] = edges[37];
        edgeArray[4][8] = edges[52];
        edgeArray[4][9] = edges[36];
        edgeArray[4][10] = edges[15];
        edgeArray[4][11] = edges[14];

        edgeArray[5][0] = null;
        edgeArray[5][1] = edges[21];
        edgeArray[5][1] = null;
        edgeArray[5][2] = edges[20];
        edgeArray[5][3] = edges[19];
        edgeArray[5][4] = null;
        edgeArray[5][5] = edges[18];
        edgeArray[5][6] = edges[17];
        edgeArray[5][7] = null;
        edgeArray[5][8] = edges[16];

        return edgeArray;
    }

    @Override
    public IGameHex getHex(int place) {
        return hexes[place];
    }

    @Override
    public IGameHex[][] getHexesAsJaggedArray() {
        IGameHex[][] map;
        map = new IGameHex[7][];
        map[0] = new IGameHex[4];
        map[1] = new IGameHex[5];
        map[2] = new IGameHex[6];
        map[3] = new IGameHex[7];
        map[4] = new IGameHex[6];
        map[5] = new IGameHex[5];
        map[6] = new IGameHex[4];

        map[0][0] = getHex(1);
        map[0][1] = getHex(2);
        map[0][2] = getHex(3);
        map[0][3] = getHex(4);
        map[1][0] = getHex(18);
        map[1][1] = getHex(19);
        map[1][2] = getHex(20);
        map[1][3] = getHex(21);
        map[1][4] = getHex(5);
        map[2][0] = getHex(17);
        map[2][1] = getHex(30);
        map[2][2] = getHex(31);
        map[2][3] = getHex(32);
        map[2][4] = getHex(22);
        map[2][5] = getHex(6);
        map[3][0] = getHex(16);
        map[3][1] = getHex(29);
        map[3][2] = getHex(36);
        map[3][3] = getHex(37);
        map[3][4] = getHex(33);
        map[3][5] = getHex(23);
        map[3][6] = getHex(7);
        map[4][0] = getHex(15);
        map[4][1] = getHex(28);
        map[4][2] = getHex(35);
        map[4][3] = getHex(34);
        map[4][4] = getHex(24);
        map[4][5] = getHex(8);
        map[5][0] = getHex(14);
        map[5][1] = getHex(27);
        map[5][2] = getHex(26);
        map[5][3] = getHex(25);
        map[5][4] = getHex(9);
        map[6][0] = getHex(13);
        map[6][1] = getHex(12);
        map[6][2] = getHex(11);
        map[6][3] = getHex(10);

        return map;
    }

    @Override
    public IIntersection[][] getIntersectionsAsJaggedArray() {
        IIntersection[][] intersec;
        intersec = new IIntersection[6][];
        intersec[0] = new IIntersection[7];
        intersec[1] = new IIntersection[9];
        intersec[2] = new IIntersection[11];
        intersec[3] = new IIntersection[11];
        intersec[4] = new IIntersection[9];
        intersec[5] = new IIntersection[7];
        System.arraycopy(intersections, 1, intersec[0], 0, 7);
        System.arraycopy(intersections, 30, intersec[1], 0, 7);
        intersec[1][7] = intersections[8];
        intersec[1][8] = intersections[9];

        intersec[2][0] = intersections[27];
        intersec[2][1] = intersections[28];
        intersec[2][2] = intersections[47];
        intersec[2][3] = intersections[48];
        intersec[2][4] = intersections[49];
        intersec[2][5] = intersections[50];
        intersec[2][6] = intersections[51];
        intersec[2][7] = intersections[36];
        intersec[2][8] = intersections[37];
        intersec[2][9] = intersections[10];
        intersec[2][10] = intersections[11];

        intersec[3][0] = intersections[26];
        intersec[3][1] = intersections[25];
        intersec[3][2] = intersections[46];
        intersec[3][3] = intersections[45];
        intersec[3][4] = intersections[54];
        intersec[3][5] = intersections[53];
        intersec[3][6] = intersections[52];
        intersec[3][7] = intersections[39];
        intersec[3][8] = intersections[38];
        intersec[3][9] = intersections[13];
        intersec[3][10] = intersections[12];

        intersec[4][0] = intersections[24];
        intersec[4][1] = intersections[23];
        intersec[4][2] = intersections[44];
        intersec[4][3] = intersections[43];
        intersec[4][4] = intersections[42];
        intersec[4][5] = intersections[41];
        intersec[4][6] = intersections[40];
        intersec[4][7] = intersections[14];
        intersec[4][8] = intersections[15];

        for (int i = 0; i < 7; i++) {
            intersec[5][i] = intersections[21 - i];
        }
        return intersec;
    }

    @Override
    public int getRobberPos() {
        return robberPosition;
    }

    @Override
    public void moveRobber(int newHex) {
        hexes[robberPosition].setRobberOnField(false);
        robberPosition = newHex;
        hexes[robberPosition].setRobberOnField(true);
    }

    @Override
    public boolean placeRoad(int player, int position) {
        if (roadPlaceable(player, position)) {
            edges[position].setState(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean placeSettlement(int player, int position) {
        if (settlementPlaceable(player, position)) {
            intersections[position].setState(player + "s");
            for (int iPos : intersections[player].getNeighbours()) {
                intersections[iPos].setState("b");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean roadPlaceable(int player, int position) {
        boolean isBuildable = false;
        if (edges[position].getState() == 0) {
            for (int ePos : edges[position].getNeighbours()) {
                if (edges[ePos].getState() == player) isBuildable = true;
            }
            if (!isBuildable) {
                for (int i : edges[position].getNeiInt()) {
                    //ASCII 49 -> 1; 50 -> 2; 51 -> 3; 52 -> 4
                    if (intersections[i].getState().charAt(0) == player + 48) isBuildable = true;
                }
            }
        }
        return isBuildable;
    }

    @Override
    public boolean settlementPlaceable(int player, int position) {
        return intersections[position].getState().equals("f");
    }

    @Override
    public boolean upgradeSettlement(int player, int position) {
        if (intersections[position].getState().equals(player + "s")) {
            intersections[position].setState(player + "c");
            return true;
        }
        return false;
    }

    /**
     * Creates the beginner's map as shown in the manual
     * of "Die Siedler von Catan" [Art.-Nr.: 684617]
     */
    private void createBeginnerMap() {
        //Creating the hexes
        //
        //Circle of water and harbor hexes (clockwise)
        hexes[1] = new HarborHex(1, 4, IHarborHex.resource.Ore);
        hexes[2] = new WaterHex();
        hexes[3] = new HarborHex(2, 5, IHarborHex.resource.Wool);
        hexes[4] = new WaterHex();
        hexes[5] = new HarborHex(3, 0, IHarborHex.resource.Any);
        hexes[6] = new WaterHex();
        hexes[7] = new HarborHex(12, 0, IHarborHex.resource.Any);
        hexes[8] = new WaterHex();
        hexes[9] = new HarborHex(19, 0, IHarborHex.resource.Grain);
        hexes[10] = new WaterHex();
        hexes[11] = new HarborHex(19, 2, IHarborHex.resource.Any);
        hexes[12] = new WaterHex();
        hexes[13] = new HarborHex(17, 2, IHarborHex.resource.Brick);
        hexes[14] = new WaterHex();
        hexes[15] = new HarborHex(8, 2, IHarborHex.resource.Any);
        hexes[16] = new WaterHex();
        hexes[17] = new HarborHex(4, 3, IHarborHex.resource.Lumber);
        hexes[18] = new WaterHex();
        //Outer circle of resource hexes
        hexes[19] = new ResourceHex(IResourceHex.resource.Fields, 4);
        hexes[20] = new ResourceHex(IResourceHex.resource.Forest, 6);
        hexes[21] = new ResourceHex(IResourceHex.resource.Fields, 9);
        hexes[22] = new ResourceHex(IResourceHex.resource.Pasture, 4);
        hexes[23] = new ResourceHex(IResourceHex.resource.Pasture, 10);
        hexes[24] = new ResourceHex(IResourceHex.resource.Forest, 11);
        hexes[25] = new ResourceHex(IResourceHex.resource.Mountains, 11);
        hexes[26] = new ResourceHex(IResourceHex.resource.Fields, 6);
        hexes[27] = new ResourceHex(IResourceHex.resource.Fields, 3);
        hexes[28] = new ResourceHex(IResourceHex.resource.Forest, 3);
        hexes[29] = new ResourceHex(IResourceHex.resource.Pasture, 9);
        hexes[30] = new ResourceHex(IResourceHex.resource.Hills, 2);
        //Inner circle of resource hexes
        hexes[31] = new ResourceHex(IResourceHex.resource.Forest, 5);
        hexes[32] = new ResourceHex(IResourceHex.resource.Pasture, 12);
        hexes[33] = new ResourceHex(IResourceHex.resource.Mountains, 8);
        hexes[34] = new ResourceHex(IResourceHex.resource.Hills, 10);
        hexes[35] = new ResourceHex(IResourceHex.resource.Mountains, 5);
        hexes[36] = new ResourceHex(IResourceHex.resource.Hills, 8);
        //Desert field in the middle
        hexes[37] = new DesertHex();
    }

    /**
     * Creates edges (usable for every map)
     */
    private void createEdges() {
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
    }

    /**
     * Creates intersections (usable for every map)
     */
    private void createIntersections() {
        //Creating the intersections
        //
        //Circle of coast intersections (clockwise)
        intersections[1] = new Intersection(new int[]{1, 18, 19}, new int[]{2, 30}, "f");
        intersections[2] = new Intersection(new int[]{1, 2, 19}, new int[]{1, 3}, "f");
        intersections[3] = new Intersection(new int[]{2, 19, 20}, new int[]{2, 4, 32}, "f");
        intersections[4] = new Intersection(new int[]{2, 3, 20}, new int[]{3, 5}, "f");
        intersections[5] = new Intersection(new int[]{3, 20, 21}, new int[]{4, 6, 34}, "f");
        intersections[6] = new Intersection(new int[]{3, 4, 21}, new int[]{5, 7}, "f");
        intersections[7] = new Intersection(new int[]{4, 5, 21}, new int[]{6, 8}, "f");
        intersections[8] = new Intersection(new int[]{5, 21, 22}, new int[]{7, 9, 35}, "f");
        intersections[9] = new Intersection(new int[]{5, 6, 22}, new int[]{8, 10}, "f");
        intersections[10] = new Intersection(new int[]{6, 22, 23}, new int[]{9, 11, 37}, "f");
        intersections[11] = new Intersection(new int[]{6, 7, 23}, new int[]{10, 12}, "f");
        intersections[12] = new Intersection(new int[]{7, 8, 23}, new int[]{11, 13}, "f");
        intersections[13] = new Intersection(new int[]{8, 23, 24}, new int[]{12, 14, 38}, "f");
        intersections[14] = new Intersection(new int[]{8, 9, 24}, new int[]{13, 15}, "f");
        intersections[15] = new Intersection(new int[]{9, 24, 25}, new int[]{14, 16, 40}, "f");
        intersections[16] = new Intersection(new int[]{9, 10, 25}, new int[]{15, 17}, "f");
        intersections[17] = new Intersection(new int[]{10, 11, 25}, new int[]{16, 18}, "f");
        intersections[18] = new Intersection(new int[]{11, 25, 26}, new int[]{17, 19, 41}, "f");
        intersections[19] = new Intersection(new int[]{11, 12, 26}, new int[]{18, 20}, "f");
        intersections[20] = new Intersection(new int[]{12, 26, 27}, new int[]{19, 21, 43}, "f");
        intersections[21] = new Intersection(new int[]{12, 13, 27}, new int[]{20, 22}, "f");
        intersections[22] = new Intersection(new int[]{13, 14, 27}, new int[]{21, 23}, "f");
        intersections[23] = new Intersection(new int[]{14, 27, 28}, new int[]{22, 24, 44}, "f");
        intersections[24] = new Intersection(new int[]{14, 15, 28}, new int[]{23, 25}, "f");
        intersections[25] = new Intersection(new int[]{15, 28, 29}, new int[]{24, 26, 46}, "f");
        intersections[26] = new Intersection(new int[]{15, 16, 29}, new int[]{25, 27}, "f");
        intersections[27] = new Intersection(new int[]{16, 17, 29}, new int[]{26, 28}, "f");
        intersections[28] = new Intersection(new int[]{18, 29, 30}, new int[]{27, 29, 47}, "f");
        intersections[29] = new Intersection(new int[]{17, 18, 30}, new int[]{28, 30}, "f");
        intersections[30] = new Intersection(new int[]{18, 19, 30}, new int[]{1, 29, 31}, "f");
        //Circle of middle intersections
        intersections[31] = new Intersection(new int[]{19, 30, 31}, new int[]{30, 32, 48}, "f");
        intersections[32] = new Intersection(new int[]{19, 20, 31}, new int[]{3, 31, 33}, "f");
        intersections[33] = new Intersection(new int[]{20, 31, 32}, new int[]{32, 34, 50}, "f");
        intersections[34] = new Intersection(new int[]{20, 21, 32}, new int[]{5, 33, 35}, "f");
        intersections[35] = new Intersection(new int[]{21, 22, 32}, new int[]{8, 34, 36}, "f");
        intersections[36] = new Intersection(new int[]{22, 32, 33}, new int[]{35, 37, 51}, "f");
        intersections[37] = new Intersection(new int[]{22, 23, 33}, new int[]{10, 36, 38}, "f");
        intersections[38] = new Intersection(new int[]{23, 24, 33}, new int[]{13, 37, 39}, "f");
        intersections[39] = new Intersection(new int[]{24, 33, 34}, new int[]{38, 40, 52}, "f");
        intersections[40] = new Intersection(new int[]{24, 25, 34}, new int[]{15, 39, 41}, "f");
        intersections[41] = new Intersection(new int[]{25, 26, 34}, new int[]{18, 40, 42}, "f");
        intersections[42] = new Intersection(new int[]{26, 34, 35}, new int[]{41, 43, 53}, "f");
        intersections[43] = new Intersection(new int[]{26, 27, 35}, new int[]{20, 42, 44}, "f");
        intersections[44] = new Intersection(new int[]{27, 28, 35}, new int[]{23, 43, 45}, "f");
        intersections[45] = new Intersection(new int[]{28, 35, 36}, new int[]{44, 46, 54}, "f");
        intersections[46] = new Intersection(new int[]{28, 29, 36}, new int[]{25, 45, 47}, "f");
        intersections[47] = new Intersection(new int[]{29, 30, 36}, new int[]{28, 46, 48}, "f");
        intersections[48] = new Intersection(new int[]{30, 31, 36}, new int[]{31, 47, 49}, "f");
        //Circle of inner intersections
        intersections[49] = new Intersection(new int[]{31, 36, 37}, new int[]{48, 50, 54}, "f");
        intersections[50] = new Intersection(new int[]{31, 32, 37}, new int[]{33, 49, 51}, "f");
        intersections[51] = new Intersection(new int[]{32, 33, 37}, new int[]{36, 50, 52}, "f");
        intersections[52] = new Intersection(new int[]{33, 34, 37}, new int[]{39, 51, 53}, "f");
        intersections[53] = new Intersection(new int[]{34, 35, 37}, new int[]{42, 52, 54}, "f");
        intersections[54] = new Intersection(new int[]{35, 36, 37}, new int[]{45, 49, 53}, "f");
        //----------------------------------------------------------------------------------------------------
    }
}
