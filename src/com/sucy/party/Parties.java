package com.sucy.party;

import com.sucy.party.command.PartyCommander;
import com.sucy.party.mccore.PartyBoardManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.mccore.CoreChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * Add-on plugin for SkillAPI allowing parties with shared experience
 */
public class Parties extends JavaPlugin {

    private ArrayList<Party> parties = new ArrayList<Party>();
    private SkillAPI skillAPI;
    private UpdateTask task;
    private boolean removeOnDc;
    private boolean newLeaderOnDc;
    private boolean displayMessage;
    private boolean leaderInviteOnly;
    private boolean useScoreboard;
    private double memberModifier;
    private double levelModifier;
    private long inviteTimeout;
    private int maxSize;

    /**
     * Loads settings and sets up the listeners
     */
    @Override
    public void onEnable() {
        skillAPI = (SkillAPI)getServer().getPluginManager().getPlugin("SkillAPI");
        task = new UpdateTask(this);

        saveDefaultConfig();
        removeOnDc = getConfig().getBoolean("remove-on-dc");
        newLeaderOnDc = getConfig().getBoolean("new-leader-on-dc");
        displayMessage = getConfig().getBoolean("display-message");
        leaderInviteOnly = getConfig().getBoolean("only-leader-invites");
        useScoreboard = getConfig().getBoolean("use-scoreboard");
        memberModifier = getConfig().getDouble("exp-modifications.members");
        levelModifier = getConfig().getDouble("exp-modifications.level");
        inviteTimeout = getConfig().getInt("invite-timeout") * 1000l;
        maxSize = getConfig().getInt("max-size");

        new PartyListener(this);
        new PartyCommander(this);
    }

    /**
     * Clears plugin data
     */
    @Override
    public void onDisable() {
        task.cancel();
        if (isUsingScoreboard()) {
            PartyBoardManager.clearBoards(this);
        }
        HandlerList.unregisterAll(this);
        parties.clear();
        skillAPI = null;
    }

    /**
     * @return SkillAPI reference
     */
    public SkillAPI getSkillAPI() {
        return skillAPI;
    }

    /**
     * @return whether or not party members are removed upon disconnect
     */
    public boolean isRemoveOnDc() {
        return removeOnDc;
    }

    /**
     * @return whether or not a new party leader is chosen upon disconnect
     */
    public boolean isNewLeaderOnDc() {
        return newLeaderOnDc;
    }

    /**
     * @return whether or not to display a message when experience is gained
     */
    public boolean isDisplayingMessages() {
        return displayMessage;
    }

    /**
     * @return whether or not only the leader can invite new party members
     */
    public boolean isLeaderInviteOnly() {
        return leaderInviteOnly;
    }

    /**
     * @return whether or not scoreboards are being used
     */
    public boolean isUsingScoreboard() {
        return useScoreboard && CoreChecker.isCoreActive();
    }

    /**
     * @return how long in milliseconds a party invitation lasts before expiring
     */
    public long getInviteTimeout() {
        return inviteTimeout;
    }

    /**
     * @return max size of the party
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @return the value for the member experience modifier
     */
    public double getMemberModifier() {
        return memberModifier;
    }

    /**
     * @return the value for the level experience modifier
     */
    public double getLevelModifier() {
        return levelModifier;
    }

    /**
     * Retrieves the party that the player is in
     *
     * @param player player to check
     * @return       party the player is in or null if not found
     */
    public Party getParty(Player player) {
        for (Party party : parties) {
            if (party.isMember(player) || party.isInvited(player)) {
                return party;
            }
        }
        return null;
    }

    /**
     * Adds a new party to the list
     *
     * @param party party to add
     */
    public void addParty(Party party) {
        parties.add(party);
    }

    /**
     * Removes a party from the list
     *
     * @param party party to remove
     */
    public void removeParty(Party party) {
        parties.remove(party);
    }

    /**
     * Updates invitations for parties
     */
    public void update() {
        for (Party party : parties) {
            party.checkInvitations();
        }
    }
}
