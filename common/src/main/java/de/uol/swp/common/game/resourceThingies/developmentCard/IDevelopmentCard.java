package de.uol.swp.common.game.resourceThingies.developmentCard;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public interface IDevelopmentCard extends Serializable {

    static Map<String, Object> getTableViewFormat(IDevelopmentCard developmentCard) {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("card", developmentCard.getType().toString());
        returnMap.put("amount", developmentCard.getAmount());
        return returnMap;
    }

    void decrease(int amount);

    void decrease();

    int getAmount();

    void setAmount(int amount);

    DevelopmentCardType getType();

    void increase(int amount);

    void increase();
}
