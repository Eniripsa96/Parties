package com.sucy.party.mccore;

import com.rit.sucy.scoreboard.StatHolder;
import com.sucy.party.Parties;
import com.sucy.party.Party;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Stat holder for a party scoreboard
 */
public class PartyStats implements StatHolder {

    private final Parties plugin;
    private final String  player;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     * @param player player name
     */
    public PartyStats(Parties plugin, String player) {
        this.plugin = plugin;
        this.player = player;
    }

    /**
     * @return stats map for a MCCore StatsScoreboard
     */
    @Override
    public Map<String, Integer> getStats() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        Player p = plugin.getServer().getPlayer(player);
        if (p != null) {
            Party pt = plugin.getParty(p);
            if (pt != null && !pt.isEmpty()) {
                for (String member : pt.getMembers()) {
                    map.put(member, plugin.getSkillAPI().getPlayer(member).getLevel());
                }
            }
        }
        return map;
    }
}
