package com.sucy.party.mccore;

import com.rit.sucy.scoreboard.StatHolder;
import com.sucy.party.Parties;
import com.sucy.party.Party;
import com.sucy.party.inject.Server;
import org.bukkit.entity.Player;

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
    public ArrayList<String> getNames() {
        ArrayList<String> stats = new ArrayList<String>();
        if (player.isOnline()) {
            Party pt = plugin.getParty(player);
            if (pt != null && !pt.isEmpty()) {
                for (String member : pt.getMembers()) {
                    if (Server.isOnline(member)) {
                        stats.add(member);
                    }
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
                        Server.getLevel(member);
                    }
                    else
                    {
                        stats.add((int)Math.ceil(Server.getPlayer(member).getHealth()));
                    }
                }
            }
        }
        return stats;
    }
}
