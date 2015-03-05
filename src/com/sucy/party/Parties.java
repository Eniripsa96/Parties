package com.sucy.party;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.SenderType;
import com.rit.sucy.config.Config;
import com.rit.sucy.config.CustomFilter;
import com.rit.sucy.config.FilterType;
import com.rit.sucy.config.LanguageConfig;
import com.rit.sucy.text.TextFormatter;
import com.sucy.party.command.*;
import com.sucy.party.mccore.PartyBoardManager;
import com.sucy.skill.SkillAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Add-on plugin for SkillAPI allowing parties with shared experience
 */
public class Parties extends JavaPlugin {

    private ArrayList<Party> parties = new ArrayList<Party>();
    private ArrayList<String> toggled = new ArrayList<String>();
    private LanguageConfig language;
    private UpdateTask task;
    private boolean removeOnDc;
    private boolean newLeaderOnDc;
    private boolean leaderInviteOnly;
    private boolean useScoreboard;
    private boolean levelScoreboard;
    private double memberModifier;
    private double levelModifier;
    private long inviteTimeout;
    private int maxSize;

    /**
     * Loads settings and sets up the listeners
     */
    @Override
    public void onEnable() {
        task = new UpdateTask(this);

        saveDefaultConfig();
        Config.trim(getConfig());
        Config.setDefaults(getConfig());
        saveConfig();
        language = new LanguageConfig(this, "language");

        removeOnDc = getConfig().getBoolean("remove-on-dc");
        newLeaderOnDc = getConfig().getBoolean("new-leader-on-dc");
        leaderInviteOnly = getConfig().getBoolean("only-leader-invites");
        useScoreboard = getConfig().getBoolean("use-scoreboard");
        levelScoreboard = getConfig().getBoolean("level-scoreboard");
        memberModifier = getConfig().getDouble("exp-modifications.members");
        levelModifier = getConfig().getDouble("exp-modifications.level");
        inviteTimeout = getConfig().getInt("invite-timeout") * 1000l;
        maxSize = getConfig().getInt("max-size");

        new PartyListener(this);

        // Set up commands
        ConfigurableCommand root = new ConfigurableCommand(this, "pt", SenderType.ANYONE);
        root.addSubCommands(
                new ConfigurableCommand(this, "accept", SenderType.PLAYER_ONLY, new CmdAccept(), "Accepts a party request", "", PermissionNode.GENERAL),
                new ConfigurableCommand(this, "decline", SenderType.PLAYER_ONLY, new CmdDecline(), "Declines a party request", "", PermissionNode.GENERAL),
                new ConfigurableCommand(this, "info", SenderType.PLAYER_ONLY, new CmdInfo(), "Views party information", "", PermissionNode.GENERAL),
                new ConfigurableCommand(this, "invite", SenderType.PLAYER_ONLY, new CmdInvite(), "Invites a player to a party", "<player>", PermissionNode.GENERAL),
                new ConfigurableCommand(this, "leave", SenderType.PLAYER_ONLY, new CmdLeave(), "Leaves your party", "", PermissionNode.GENERAL),
                new ConfigurableCommand(this, "message", SenderType.PLAYER_ONLY, new CmdMsg(), "Sends a message to your party", "<message>", PermissionNode.GENERAL),
                new ConfigurableCommand(this, "toggle", SenderType.PLAYER_ONLY, new CmdToggle(), "Toggles party chat on/off", "", PermissionNode.GENERAL)
        );
        CommandManager.registerCommand(root);
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
     * @return whether or not only the leader can invite new party members
     */
    public boolean isLeaderInviteOnly() {
        return leaderInviteOnly;
    }

    /**
     * @return whether or not scoreboards are being used
     */
    public boolean isUsingScoreboard() {
        return useScoreboard;
    }

    /**
     * @return whether or not levels are shown in the scoreboard over health
     */
    public boolean isLevelScoreboard()
    {
        return levelScoreboard;
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

    /**
     * Checks if the player is toggled
     *
     * @param playerName name of the player to check
     * @return           true if toggled, false otherwise
     */
    public boolean isToggled(String playerName) {
        return toggled.contains(playerName.toLowerCase());
    }

    /**
     * Toggles the player's party chat
     *
     * @param playerName name of player to toggle
     */
    public void toggle(String playerName) {
        if (isToggled(playerName)) {
            toggled.remove(playerName.toLowerCase());
        }
        else toggled.add(playerName.toLowerCase());
    }

    /**
     * <p>Gets the message at the given key in the language configuration.</p>
     *
     * <p>Colors are applied and the message returned contains a list of all
     * lines from the configuration. If it was just a single value, there will
     * be only one element. If it was a string list, it will contain each line.</p>
     *
     * @param key     language key
     * @param player  whether or not the message is for a player
     * @param filters filters to apply
     * @return        list of message lines
     */
    public List<String> getMessage(String key, boolean player, CustomFilter ... filters) {
        return language.getMessage(key, player, FilterType.COLOR, filters);
    }

    /**
     * <p>Sends a message to the target based on the message at the given
     * language key.</p>
     *
     * @param target  recipient of the message
     * @param key     message key
     * @param filters filters to use
     */
    public void sendMessage(Player target, String key, CustomFilter ... filters) {
        language.sendMessage(key, target, FilterType.COLOR, filters);
    }
}
