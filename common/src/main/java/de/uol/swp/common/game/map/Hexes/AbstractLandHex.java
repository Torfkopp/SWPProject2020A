package de.uol.swp.common.game.map.Hexes;

public abstract class AbstractLandHex implements ILandHex{
    private boolean robberOnField;

    @Override
    public void setRobberOnField( boolean robberOnField){
        this.robberOnField = robberOnField;
    }

    @Override
    public boolean isRobberOnField() {return robberOnField;}
}
