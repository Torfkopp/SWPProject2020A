package de.uol.swp.server.game;

public class harborHex implements iHarborHex{

    private int belongingHex;
    private resource resource;

    public harborHex(int belongingHex, resource resource){
        this.belongingHex = belongingHex;
        this.resource = resource;
    }

    @Override
    public resource getResource() {
        return resource;
    }
}
