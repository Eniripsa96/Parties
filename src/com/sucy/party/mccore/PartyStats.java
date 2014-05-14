package com.sucy.party.mccore;

import com.rit.sucy.scoreboard.StatHolder;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.party.Parties;
import com.sucy.party.Party;
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

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param player player name
     */
    public PartyStats(Parties plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
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
                    Player m = new VersionPlayer(member).getPlayer();
                    if (m != null) {
                        stats.add(m);
                    }
                    else stats.add(player);
                }
            }
        }
        return stats;
    }

    @Override
    public ArrayList<Integer> getValues() {
        ArrayList<Integer> stats = new ArrayList<Integer>();
        if (player.isOnline()) {
            Party pt = plugin.getParty(player);
            if (pt != null && !pt.isEmpty()) {
                for (String member : pt.getMembers()) {
                    stats.add(plugin.getSkillAPI().getPlayer(new VersionPlayer(member)).getLevel());
                }
            }
        }
        return stats;
    }
}
