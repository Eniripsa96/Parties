package com.sucy.party;

import com.sucy.skill.api.PlayerSkills;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data for a party
 */
public class Party {

    private ArrayList<String> members = new ArrayList<String>();
    private HashMap<String, Long> invitations = new HashMap<String, Long>();
    private Parties plugin;
    private String partyLeader;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param leader leader of the party
     */
    public Party(Parties plugin, Player leader) {
        this.plugin = plugin;
        this.partyLeader = leader.getName();
        members.add(partyLeader);
    }

    /**
     * @return true if full, false otherwise
     */
    public boolean isFull() {
        checkInvitations();
        return invitations.size() + members.size() >= plugin.getMaxSize();
    }

    /**
     * @return true if the party is empty, false otherwise
     */
    public boolean isEmpty() {
        checkInvitations();
        return invitations.size() + members.size() <= 1;
    }

    /**
     * Clears all expired invitations from the map
     */
    public void checkInvitations() {
        String[] names = invitations.keySet().toArray(new String[invitations.size()]);
        for (String name : names) {
            if (invitations.get(name) < System.currentTimeMillis()) {
                invitations.remove(name);
                sendMessage(ChatColor.GOLD + name + ChatColor.DARK_GREEN + " didn't respond to the party invitation");
                Player player = plugin.getServer().getPlayer(name);
                if (player != null) {
                    player.sendMessage(ChatColor.DARK_GREEN + "You didn't respond to the party invitation");
                }
            }
        }
    }

    /**
     * @return size of the party
     */
    public int getPartySize() {
        return members.size();
    }

    /**
     * @return list of names of the members in the party
     */
    public ArrayList<String> getMembers() {
        return members;
    }

    /**
     * @return number of online members in the party
     */
    public int getOnlinePartySize() {
        int counter = 0;
        for (String member : members) {
            if (plugin.getServer().getPlayer(member) != null) counter++;
        }
        return counter;
    }

    /**
     * Checks if the player is on the team
     *
     * @param player player to check
     * @return       true if on the team, false otherwise
     */
    public boolean isMember(Player player) {
        return members.contains(player.getName());
    }

    /**
     * Checks if the player has been invited to join the party
     *
     * @param player player to check
     * @return       true if invited to join the team, false otherwise
     */
    public boolean isInvited(Player player) {
        checkInvitations();
        return invitations.containsKey(player.getName());
    }

    /**
     * Checks if the player is the leader of the party
     *
     * @param player player to check
     * @return       true if they're the leader, false otherwise
     */
    public boolean isLeader(Player player) {
        return partyLeader.equals(player.getName());
    }

    /**
     * Adds a member to the party
     *
     * @param player player to add
     */
    public void invite(Player player) {
        if (!members.contains(player.getName()) && !invitations.containsKey(player.getName())) {
            invitations.put(player.getName(), System.currentTimeMillis() + plugin.getInviteTimeout());
        }
    }

    /**
     * Accepts a player into the party
     *
     * @param player player to accept
     */
    public void accept(Player player) {
        if (invitations.containsKey(player.getName())) {
            invitations.remove(player.getName());
            members.add(player.getName());
        }
    }

    /**
     * @param player player to decline
     */
    public void decline(Player player) {
        if (invitations.containsKey(player.getName())) {
            invitations.remove(player.getName());
        }
    }

    /**
     * Removes a member from the party
     *
     * @param player player to remove
     */
    public void removeMember(Player player) {
        if (members.contains(player.getName())) {
            members.remove(player.getName());
        }
        if (isLeader(player) && members.size() > 0) {
            partyLeader = members.get(0);
        }
    }

    /**
     * Changes the leader of the party
     */
    public void changeLeader() {
        for (String member : members) {
            if (plugin.getServer().getPlayer(member) != null) {
                partyLeader = member;
                plugin.getServer().getPlayer(member).sendMessage(ChatColor.DARK_GREEN + "You are now the leader of your party");
            }
        }
    }

    /**
     * Shares experience within the party
     *
     * @param source player who caused the experience gain
     * @param amount amount received
     */
    public void giveExp(Player source, int amount) {
        if (getOnlinePartySize() == 0) {
            return;
        }

        // Member modifier
        double baseAmount = amount / (1 + (getOnlinePartySize() - 1) * plugin.getMemberModifier());
        int level = plugin.getSkillAPI().getPlayer(source.getName()).getLevel();

        // Grant exp to all members
        for (String member : members) {

            // Player must be online
            Player player = plugin.getServer().getPlayer(member);
            if (player != null) {
                PlayerSkills data = plugin.getSkillAPI().getPlayer(member);
                int exp = (int)Math.ceil(baseAmount);

                // Level modifier
                if (plugin.getLevelModifier() > 0) {
                    int dl = data.getLevel() - level;
                    exp = (int)Math.ceil(baseAmount * Math.pow(2, -plugin.getLevelModifier() * dl * dl));
                }

                data.giveExp(exp);

                // Display message if enabled
                if (plugin.isDisplayingMessages()) {
                    player.sendMessage(ChatColor.DARK_GREEN + "You gained " + ChatColor.GOLD + exp + " experience");
                }
            }
        }
    }

    /**
     * Sends a message to all members in the party
     *
     * @param message message to send
     */
    public void sendMessage(String message) {
        for (String member : members) {
            Player player = plugin.getServer().getPlayer(member);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }
}
