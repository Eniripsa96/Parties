package com.sucy.party.command;

import com.sucy.party.Parties;
import com.sucy.skill.command.CommandHandler;

/**
 * Command handler for party commands
 */
public class PartyCommander extends CommandHandler {

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public PartyCommander(Parties plugin) {
        super(plugin, "Parties - Made by Eniripsa96", "pt");
    }

    /**
     * Registers commands for the plugin
     */
    @Override
    protected void registerCommands() {
        registerCommand("accept", new CmdAccept());
        registerCommand("decline", new CmdDecline());
        registerCommand("invite", new CmdInvite());
        registerCommand("leave", new CmdLeave());
    }
}
