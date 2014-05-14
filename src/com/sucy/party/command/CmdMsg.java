package com.sucy.party.command;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.config.Config;
import com.sucy.party.Parties;
import com.sucy.party.Party;
import com.sucy.party.PermissionNode;
import com.sucy.party.lang.CommandNodes;
import com.sucy.party.lang.ErrorNodes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to invite other players to a party
 */
public class CmdMsg implements IFunction {

    /**
     * Executes the command
     *
     * @param command handler for the commands
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments provided
     */
    @Override
    public void execute(ConfigurableCommand command, Plugin plugin, CommandSender sender, String[] args) {

        Parties parties = (Parties)plugin;
        Player player = (Player)sender;

        // Requires at least one argument
        if (args.length == 0) {
            command.displayHelp(sender, 1);
            return;
        }

        // Check the sender's party status
        Party party = parties.getParty(player);
        if (party != null && !party.isEmpty()) {
            String text = args[0];
            for (int i = 1; i < args.length; i++) {
                text += " " + args[i];
            }
            party.sendMessage(player, text);
        }

        // Not in a party
        else parties.sendMessage(player, ErrorNodes.NO_PARTY);
    }
}
