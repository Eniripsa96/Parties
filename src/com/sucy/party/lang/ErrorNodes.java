package com.sucy.party.lang;

/**
 * Configuration keys for the language config,
 * specifically the Errors section of it
 */
public class ErrorNodes {

    public static final String

    /************************
      Base Configuration Key
     ************************/

    BASE = "Errors.",

    /***********************
      Individual Value Keys
     ***********************/

    NO_INVITE_SELF = BASE + "no-invite-self",
    NO_INVITES = BASE + "no-invites",
    NO_PARTY = BASE + "no-party",
    NOT_ONLINE = BASE + "not-online",
    NOT_LEADER = BASE + "not-leader",
    IN_OTHER_PARTY = BASE + "in-other-party",
    PARTY_FULL = BASE + "party-full";
}
