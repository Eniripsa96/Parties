package com.sucy.party.command;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.config.CustomFilter;
import com.rit.sucy.config.Filter;
import com.rit.sucy.text.TextSizer;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.party.Parties;
import com.sucy.party.Party;
import com.sucy.party.lang.ErrorNodes;
import com.sucy.party.lang.IndividualNodes;
import com.sucy.party.lang.PartyNodes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to display party information
 */
public class CmdInfo implements IFunction {

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
            StringBuilder members = new StringBuilder();
            for (String member : party.getMembers()) {
                members.append(new VersionPlayer(member).getName());
                members.append(", ");
            }
            parties.sendMessage(
                    player,
                    IndividualNodes.INFO,
                    new CustomFilter("{leader}", party.getLeader().getName()),
                    new CustomFilter("{members}", members.substring(0, members.length() - 2)),
                    new CustomFilter("{size}", party.getPartySize() + ""),
                    new CustomFilter("{break}", TextSizer.createLine("", "-", ChatColor.DARK_GRAY))
            );
        }

        else parties.sendMessage(player, ErrorNodes.NO_PARTY);
    }
}
