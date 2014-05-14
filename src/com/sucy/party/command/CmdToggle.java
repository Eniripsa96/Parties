package com.sucy.party.command;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.party.Parties;
import com.sucy.party.Party;
import com.sucy.party.PermissionNode;
import com.sucy.party.lang.CommandNodes;
import com.sucy.party.lang.ErrorNodes;
import com.sucy.party.lang.IndividualNodes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to toggle party chat
 */
public class CmdToggle implements IFunction {

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
        if (party != null && !party.isEmpty()) {

            parties.toggle(player.getName());
            if (parties.isToggled(player.getName())) {
                parties.sendMessage(player, IndividualNodes.CHAT_ON);
            }
            else parties.sendMessage(player, IndividualNodes.CHAT_OFF);
        }

        // Not in a party
        else parties.sendMessage(player, ErrorNodes.NO_PARTY);
    }
}
