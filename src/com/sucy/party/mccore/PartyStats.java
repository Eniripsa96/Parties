package com.sucy.party.mccore;

import com.rit.sucy.scoreboard.StatHolder;
import com.rit.sucy.version.VersionManager;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.party.Parties;
import com.sucy.party.Party;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sun.net.www.content.text.plain;

import java.util.ArrayList;

/**
 * Stat holder for a party scoreboard
 */
public class PartyStats implements StatHolder {

    private final Parties plugin;
    private final Player  player;
    private final boolean level;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param player player name
     * @param level  whether or not to display level. False makes it display health
     */
    public PartyStats(Parties plugin, Player player, boolean level) {
        this.plugin = plugin;
        this.player = player;
        this.level = level;
    }

    /**
     * @return stats map for a MCCore StatsScoreboard
     */
    @Override
    public ArrayList<OfflinePlayer> getStats() {
        ArrayList<OfflinePlayer> stats = new ArrayList<OfflinePlayer>();
        if (player.isOnline()) {
            Party pt = plugin.getParty(player);
            if (pt != null && !pt.isEmpty()) {
                for (String member : pt.getMembers()) {
                    Player m = VersionManager.getPlayer(member);
                    if (m != null) {
                        stats.add(m);
                    }
                    else stats.add(player);
                }
            }
        }
        return stats;
    }

    /**
     * @return the current values for the party members
     */
    @Override
    public ArrayList<Integer> getValues() {
        ArrayList<Integer> stats = new ArrayList<Integer>();
        if (player.isOnline()) {
            Party pt = plugin.getParty(player);
            if (pt != null && !pt.isEmpty()) {
                for (String member : pt.getMembers()) {
                    if (level)
                    {
                        PlayerClass main = SkillAPI.getPlayerData(VersionManager.getPlayer(member)).getMainClass();
                        stats.add(main == null ? 0 : main.getLevel());
                    }
                    else
                    {
                        stats.add((int)Math.ceil(VersionManager.getPlayer(member).getHealth()));
                    }
                }
            }
        }
        return stats;
    }
}
