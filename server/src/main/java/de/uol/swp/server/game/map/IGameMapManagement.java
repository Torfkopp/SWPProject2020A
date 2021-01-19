package de.uol.swp.server.game.map;


/**
 * Interface for the game's map
 *
 * @author Mario
 * @author Steven
 * @since 2021-01-16
 */
public interface IGameMapManagement {

    /**
     * Gets the hex at a specified place
     *
     * @param place The hex's place (int)
     * @return The hex
     */
    IGameHex getHex(int place);

    /**
     * Places a settlement
     *
     * @param player   The number of the player
     *                 wanting to build the settlement (1-4)
     * @param position The position of the intersection
     * @return true if placement was successful; false if not
     */
    boolean placeSettlement(int player, int position);

    /**
     * Places a street
     *
     * @param player   The number of the player
     *                 wanting to build the street (1-4)
     * @param position The position of the road
     * @return true if placement was successful; false if not
     */
    boolean placeRoad(int player, int position);

    /**
     * Moves the robber
     *
     * @param newHex The hex the robber has moved to
     */
    void moveRobber(int newHex);

    /**
     * Gets the robber's position
     *
     * @return int Position of the robber
     */
    int getRobberPos();

    /**
     * Upgrades a settlement
     *
     * @param player   The number of the player
     *                 wanting to upgrade the settlement (1-4)
     * @param position The position of the intersection
     * @return true if placement was successful; false if not
     */
    boolean upgradeSettlement(int player, int position);

    /**
     * Checks if a settlement is placeable
     *
     * @param player   The number of the player
     *                 wanting to place the settlement (1-4)
     * @param position The position of the intersection
     * @return true if placement is possible; false if not
     */
    boolean settlementPlaceable(int player, int position);

    /**
     * Checks if a street is placeable
     *
     * @param player   The number of the player
     *                 wanting to place the street (1-4)
     * @param position The position of the road
     * @return true if placement is possible; false if not
     */
    boolean roadPlaceable(int player, int position);

    /**
     * Interface for an edge
     *
     * @author Mario
     * @since 2021-01-17
     */
    interface IEdge {

        /**
         * Gets the two surrounding intersections
         *
         * @return Array of two positions
         */
        int[] getNeiInt();

        /**
         * Gets neighbouring edges
         *
         * @return Array of positions
         */
        int[] getNeighbours();

        /**
         * Gets the edge's status
         *
         * @return 0 if undeveloped or 1-4 for road owner
         */
        int getState();

        /**
         * Sets the edge's status
         * <p>
         *
         * @param state 1-4 for road owner
         */
        void setState(int state);
    }

    /**
     * Interface for an intersection
     *
     * @author Mario
     * @since 2021-01-17
     */
    interface IIntersection {

        /**
         * Gets the surrounding hexes
         *
         * @return Array of three hex positions
         */
        int[] getHexes();

        /**
         * Gets neighbouring intersections
         *
         * @return Array of positions
         */
        int[] getNeighbours();

        /**
         * Gets the intersection's status
         *
         * @return "f" if free, "b" if blocked, or 1-4 for owner plus s (settlement) or c (city)
         */
        String getState();

        /**
         * Sets the intersection's status
         *
         * @param state "f" if free,
         *              "b" if blocked, or
         *              1-4 for owner plus s (settlement) or c (city)
         */
        void setState(String state);

    }

    /**
     * Interface for a hex
     *
     * @author Mario
     * @author Steven
     * @since 2021-01-16
     */
    interface IGameHex {

        enum type {
            Water, Desert, Resource, Harbor
        }

        type getType();

        /**
         * Interface for a land hex
         *
         * @author Mario
         * @author Steven
         * @since 2021-01-16
         */
        interface ILandHex extends IGameHex {
        }

        /**
         * Interface for a resource hex
         *
         * @author Mario
         * @author Steven
         * @since 2021-01-16
         */
        interface IResourceHex extends ILandHex {

            /**
             * Enum for all five resource giving hex types
             */
            enum resource {
                Hills, Forest, Mountains, Fields, Pasture
            }

            /**
             * Gets the number of the hex's token
             *
             * @return int Token number
             */
            int getToken();

            /**
             * Gets the hex's resource
             *
             * @return Resource
             */
            IResourceHex.resource getResource();

        }

        /**
         * Interface for a water hex
         *
         * @author Mario
         * @author Steven
         * @since 2021-01-16
         */
        interface IWaterHex extends IGameHex {

        }

        /**
         * Interface for a harbor hex
         *
         * @author Mario
         * @author Steven
         * @since 2021-01-16
         */
        interface IHarborHex extends IWaterHex {

            /**
             * Enum for the five resources a harbor can trade
             * and 'any' if every resource is tradeable
             */
            enum resource {
                Brick, Lumber, Ore, Grain, Wool, Any
            }

            /**
             * Gets the harbor's resource
             *
             * @return Resource
             */
            IHarborHex.resource getResource();

        }

    }
}
