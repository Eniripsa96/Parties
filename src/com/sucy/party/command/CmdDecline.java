package com.sucy.party.command;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.config.Filter;
import com.sucy.party.Parties;
import com.sucy.party.Party;
import com.sucy.party.lang.ErrorNodes;
import com.sucy.party.lang.IndividualNodes;
import com.sucy.party.lang.PartyNodes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to accept a party invitation
 */
public class CmdDecline implements IFunction {

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
        if (party != null && party.isInvited(player)) {
            party.decline(player);
            party.sendMessages(parties.getMessage(PartyNodes.PLAYER_DECLINED, true, Filter.PLAYER.setReplacement(player.getName())));
            parties.sendMessage(player, IndividualNodes.DECLINED);
        }

        else parties.sendMessage(player, ErrorNodes.NO_INVITES);
    }
}
