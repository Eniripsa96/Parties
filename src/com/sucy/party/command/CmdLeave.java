package com.sucy.party.command;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.config.Filter;
import com.sucy.party.Parties;
import com.sucy.party.Party;
import com.sucy.party.PermissionNode;
import com.sucy.party.lang.CommandNodes;
import com.sucy.party.lang.ErrorNodes;
import com.sucy.party.lang.PartyNodes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to accept a party invitation
 */
public class CmdLeave implements IFunction {

    /**
     * Executes the command
     *
     * @param command owning command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments provided
     */
    @Override
    public void execute(ConfigurableCommand command, Plugin plugin, CommandSender sender, String[] args) {

        Parties parties = (Parties)plugin;
        Player player = (Player)sender;

        // Check the sender's party status
        Party party = parties.getParty(player);
        if (party != null && party.isMember(player)) {
            party.sendMessages(parties.getMessage(PartyNodes.PLAYER_LEFT, true, Filter.PLAYER.setReplacement(player.getName())));
            party.removeMember(player);
            if (party.isEmpty()) {
                parties.removeParty(party);
            }
        }

        else parties.sendMessage(player, ErrorNodes.NO_PARTY);
    }
}
