package com.sucy.party;

import com.rit.sucy.config.Filter;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.party.lang.IndividualNodes;
import com.sucy.party.lang.PartyNodes;
import com.sucy.party.mccore.PartyBoardManager;
import com.sucy.skill.api.PlayerSkills;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.persistence.Version;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Data for a party
 */
public class Party {

    private ArrayList<String> members = new ArrayList<String>();
    private HashMap<String, Long> invitations = new HashMap<String, Long>();
    private Parties plugin;
    private VersionPlayer partyLeader;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param leader leader of the party
     */
    public Party(Parties plugin, Player leader) {
        this.plugin = plugin;
        this.partyLeader = new VersionPlayer(leader);
        members.add(partyLeader.getIdString());
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
     * Retrieves the leader of the party
     *
     * @return party leader
     */
    public VersionPlayer getLeader() {
        return partyLeader;
    }

    /**
     * Clears all expired invitations from the map
     */
    public void checkInvitations() {
        String[] members = invitations.keySet().toArray(new String[invitations.size()]);
        for (String member : members) {
            if (invitations.get(member) < System.currentTimeMillis()) {
                invitations.remove(member);
                Player player = new VersionPlayer(member).getPlayer();
                if (player != null) {
                    sendMessages(plugin.getMessage(PartyNodes.NO_RESPONSE, true, Filter.PLAYER.setReplacement(player.getName())));
                    plugin.sendMessage(player, IndividualNodes.NO_RESPONSE);
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
            if (new VersionPlayer(member).getPlayer() != null) counter++;
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
        return members.contains(new VersionPlayer(player).getIdString());
    }

    /**
     * Checks if the player has been invited to join the party
     *
     * @param player player to check
     * @return       true if invited to join the team, false otherwise
     */
    public boolean isInvited(Player player) {
        checkInvitations();
        return invitations.containsKey(new VersionPlayer(player).getIdString());
    }

    /**
     * Checks if the player is the leader of the party
     *
     * @param player player to check
     * @return       true if they're the leader, false otherwise
     */
    public boolean isLeader(Player player) {
        return partyLeader.equals(new VersionPlayer(player));
    }

    /**
     * Adds a member to the party
     *
     * @param player player to add
     */
    public void invite(Player player) {
        VersionPlayer vp = new VersionPlayer(player);
        if (!members.contains(vp.getIdString()) && !invitations.containsKey(vp.getIdString())) {
            invitations.put(vp.getIdString(), System.currentTimeMillis() + plugin.getInviteTimeout());
        }
    }

    /**
     * Accepts a player into the party
     *
     * @param player player to accept
     */
    public void accept(Player player) {
        VersionPlayer vp = new VersionPlayer(player);
        if (invitations.containsKey(vp.getIdString())) {
            invitations.remove(vp.getIdString());
            members.add(vp.getIdString());
        }
    }

    /**
     * @param player player to decline
     */
    public void decline(Player player) {
        VersionPlayer vp = new VersionPlayer(player);
        if (invitations.containsKey(vp.getIdString())) {
            invitations.remove(vp.getIdString());
        }
    }

    /**
     * Removes a member from the party
     *
     * @param player player to remove
     */
    public void removeMember(Player player) {
        VersionPlayer vp = new VersionPlayer(player);
        if (members.contains(vp.getIdString())) {
            members.remove(vp.getIdString());
        }
        if (isLeader(player) && members.size() > 0) {
            partyLeader = new VersionPlayer(members.get(0));
        }
    }

    /**
     * Changes the leader of the party
     */
    public void changeLeader() {
        for (String member : members) {
            if (new VersionPlayer(member).getPlayer() != null) {
                partyLeader = new VersionPlayer(member);
                sendMessages(plugin.getMessage(PartyNodes.NEW_LEADER, true, Filter.PLAYER.setReplacement(partyLeader.getName())));
            }
        }
    }

    /**
     * Removes scoreboards for the party
     */
    public void removeBoards() {
        for (String member : members) {
            Player player = new VersionPlayer(member).getPlayer();
            if (player != null) {
                PartyBoardManager.clearBoard(plugin, player);
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
            Player player = new VersionPlayer(member).getPlayer();
            if (player != null) {
                PlayerSkills data = plugin.getSkillAPI().getPlayer(member);
                int exp = (int)Math.ceil(baseAmount);

                // Level modifier
                if (plugin.getLevelModifier() > 0) {
                    int dl = data.getLevel() - level;
                    exp = (int)Math.ceil(baseAmount * Math.pow(2, -plugin.getLevelModifier() * dl * dl));
                }

                data.giveExp(exp);
            }
        }
    }

    /**
     * Sends a message to the party
     *
     * @param message message to send
     */
    public void sendMessage(String message) {
        for (String member : members) {
            Player player = new VersionPlayer(member).getPlayer();
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Sends a list of messages to the party
     *
     * @param messages messages to send
     */
    public void sendMessages(List<String> messages) {
        for (String member : members) {
            Player player = new VersionPlayer(member).getPlayer();
            if (player != null) {
                for (String message : messages) {
                    player.sendMessage(message);
                }
            }
        }
    }

    /**
     * Sends a message to all members in the party
     *
     * @param sender  the player who sent a party message
     * @param message message the player typed
     */
    public void sendMessage(Player sender, String message) {
        List<String> messages = plugin.getMessage(PartyNodes.CHAT_MESSAGE, true, Filter.PLAYER.setReplacement(sender.getName()), Filter.MESSAGE.setReplacement(message));
        for (String member : members) {
            Player player = new VersionPlayer(member).getPlayer();
            if (player != null) {
                for (String line : messages) {
                    player.sendMessage(line);
                }
            }
        }
    }

    /**
     * Clears the party scoreboard for the player
     *
     * @param player player to clear for
     */
    public void clearBoard(Player player) {
        PartyBoardManager.clearBoard(plugin, player);
        if (isEmpty()) {
            removeBoards();
        }
    }

    public void updateBoards() {
        removeBoards();
        for (String member : members) {
            PartyBoardManager.applyBoard(plugin, new VersionPlayer(member).getPlayer());
        }
    }
}
