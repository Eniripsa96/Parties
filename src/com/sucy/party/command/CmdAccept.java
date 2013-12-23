package com.sucy.party.command;

import com.sucy.party.Parties;
import com.sucy.party.Party;
import com.sucy.party.PermissionNode;
import com.sucy.skill.command.CommandHandler;
import com.sucy.skill.command.ICommand;
import com.sucy.skill.command.SenderType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to accept a party invitation
 */
public class CmdAccept implements ICommand {

    /**
     * Executes the command
     *
     * @param handler handler for the commands
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments provided
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        Parties parties = (Parties)plugin;
        Player player = (Player)sender;

        // Check the sender's party status
        Party party = parties.getParty(player);
        if (party != null && party.isInvited(player)) {
            party.accept(player);
            party.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.DARK_GREEN + " has joined the party");
        }

        else player.sendMessage(ChatColor.DARK_RED + "You have no pending invitations to accept");
    }

    /**
     * @return permission required to use the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNode.GENERAL;
    }

    /**
     * @param plugin plugin reference
     * @return       arguments used by the command
     */
    @Override
    public String getArgsString(Plugin plugin) {
        return "";
    }

    /**
     * @param plugin plugin reference
     * @return       a description for the command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return "Accepts a party request";
    }

    /**
     * @return type of sender required by the command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
