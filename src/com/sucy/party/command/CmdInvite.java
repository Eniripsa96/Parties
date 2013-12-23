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
 * Command to invite other players to a party
 */
public class CmdInvite implements ICommand {

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

        // Requires at least one argument
        if (args.length == 0) {
            handler.displayUsage(sender);
            return;
        }

        // Validate the player
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.DARK_RED + "That player is not online");
            return;
        }

        // Check the sender's party status
        Party party = parties.getParty(player);
        if (party != null) {

            // Party is full
            if (party.isFull()) {
                player.sendMessage(ChatColor.DARK_RED + "Your party is currently full");
                return;
            }

            // Doesn't have permission
            if (((Parties) plugin).isLeaderInviteOnly() && !party.isLeader(player)) {
                player.sendMessage(ChatColor.DARK_RED + "You aren't the leader of the party");
                return;
            }
        }

        // Check the target's party status
        Party targetParty = parties.getParty(target);
        if (targetParty != null && !targetParty.isEmpty()) {
            player.sendMessage(ChatColor.DARK_RED + "That player is already in another party");
            return;
        }

        // Clear the target's party
        if (targetParty != null) {
            parties.removeParty(targetParty);
        }

        // Initialize a new party if it doesn't exist
        if (party == null) {
            party = new Party(parties, player);
            parties.addParty(party);
        }

        // Invite the target
        party.invite(target);
        party.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.DARK_GREEN + " has been invited to join the party");
        target.sendMessage(ChatColor.DARK_GREEN + "You have been invited to join " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.DARK_GREEN + "party");
        target.sendMessage(ChatColor.DARK_GREEN + "Type " + ChatColor.GOLD + "/pt accept" + ChatColor.DARK_GREEN + " to join or " + ChatColor.GOLD + "/pt decline " + ChatColor.DARK_GREEN + "to decline");
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
        return "<player>";
    }

    /**
     * @param plugin plugin reference
     * @return       a description for the command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return "Invites a player to a party";
    }

    /**
     * @return type of sender required by the command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
