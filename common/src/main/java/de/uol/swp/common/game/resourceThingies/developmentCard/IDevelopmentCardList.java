package de.uol.swp.common.game.resourceThingies.developmentCard;

import java.io.Serializable;
import java.util.*;

public interface IDevelopmentCardList extends Iterable<DevelopmentCard>, Serializable {

    static List<Map<String, Object>> getTableViewFormat(IDevelopmentCardList developmentCardList){
        List<Map<String, Object>> returnMap = new LinkedList<>();
        for (IDevelopmentCard developmentCard : developmentCardList){
            returnMap.add(IDevelopmentCard.getTableViewFormat(developmentCard));
        }
        return returnMap;
    }

    @Override
    Iterator<DevelopmentCard> iterator();

    DevelopmentCardList create();

    DevelopmentCard get(DevelopmentCardType resource);

    void increase(DevelopmentCardType resource);

    void decrease(DevelopmentCardType resource);

    int getAmount(DevelopmentCardType resource);
}
