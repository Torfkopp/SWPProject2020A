package de.uol.swp.common.chat.message;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
import de.uol.swp.common.message.AbstractServerMessage;

import java.util.Map;

public class SystemMessageForTradeWithBankMessage extends AbstractServerMessage {

    private final String lobbyName;
    private final String user;
    private final String developmentCard;
    //private final Map<I18nWrapper, Integer> respondingResourceMap;
    //private final Map<I18nWrapper, Integer> offeringResourceMap;

    public SystemMessageForTradeWithBankMessage(String user, String lobbyName,
                                                String developmentCard) {
        this.lobbyName = lobbyName;
        this.user = user;
        this.developmentCard = developmentCard;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    ///**
    // * Gets the SystemMessage object.
    // *
    // * @return The encapsulated SystemMessage
    // */
    //public SystemMessage getMsg() {
    //    return new SystemMessageDTO(
    //            //makeSingularI18nWrapper(this.user, this.offeringResourceMap,
    //            //                        this.respondingResourceMap));
    //}

    public SystemMessage getMsg(){
        return new SystemMessageDTO(makeSingularI18nWrapper(user, developmentCard));
    }

    /**
     * Check if the ChatMessage message is destined for a lobby chat
     *
     * @return True, if the SystemMessage message is meant for a lobby chat; False if not
     */
    public boolean isLobbyChatMessage() {
        return lobbyName != null;
    }

    private I18nWrapper makeSingularI18nWrapper(String user,
                                                String developmentCard) {
        StringBuilder offerString = new StringBuilder();
        //for (Map.Entry<I18nWrapper, Integer> entry : offeringResourceMap.entrySet()) {
        //    offerString.append(", ");
        //    if (entry.getValue() > 0) offerString.append(entry.getValue()).append(" ");
        //    offerString.append(entry.getKey().toString());
        //}
        StringBuilder demandString = new StringBuilder();
        //for (Map.Entry<I18nWrapper, Integer> entry : respondingResourceMap.entrySet()) {
        //    demandString.append(", ");
        //    if (entry.getValue() > 0) demandString.append(entry.getValue()).append(" ");
        //    demandString.append(entry.getKey().toString());
        //}
        return new I18nWrapper("lobby.trade.withbank.systemmessage", user,
                               developmentCard);
    }
}

