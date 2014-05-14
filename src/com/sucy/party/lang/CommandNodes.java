package com.sucy.party.lang;

/**
 * Configuration keys for the language config,
 * specifically the Command section of it
 */
public class CommandNodes {

    public static final String

    /************************
      Base Configuration Key
     ************************/

    BASE = "Command.",

    /******************
      Command Root Key
     ******************/

    ROOT = BASE + "root",

    /*************************
      Command Subsection Keys
     *************************/

    NAME = BASE + "Names.",
    DESCRIPTION = BASE + "Descriptions.",
    ARGUMENTS = BASE + "Arguments.",

    /***********************
      Specific Command Keys
     ***********************/

    ACCEPT = "accept",
    DECLINE = "decline",
    INVITE = "invite",
    LEAVE = "leave",
    MESSAGE = "message",
    TOGGLE = "toggle-chat";
}
