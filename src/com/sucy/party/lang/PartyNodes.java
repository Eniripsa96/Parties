package com.sucy.party.lang;

/**
 * Configuration keys for the language config,
 * specifically the Party section of it
 */
public class PartyNodes {

    public static final String

    /************************
      Base Configuration Key
     ************************/

    BASE = "Party.",

    /***********************
      Individual Value Keys
     ***********************/

    PLAYER_JOINED = BASE + "player-joined",
    PLAYER_DECLINED = BASE + "player-declined",
    PLAYER_INVITED = BASE + "player-invited",
    PLAYER_LEFT = BASE + "player-left",
    NO_RESPONSE = BASE + "no-response",
    CHAT_MESSAGE = BASE + "chat-message",
    NEW_LEADER = BASE + "new-leader",
    SCOREBOARD = BASE + "scoreboard-title";
}
