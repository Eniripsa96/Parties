package com.sucy.party.inject;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Parties © 2017
 * com.sucy.party.inject.InjectInterface
 *
 * Default calls to static methods. See
 */
public class InjectInterface {

    public boolean isOnline(String playerName) {
        return getPlayer(playerName) != null;
    }

    public Player getPlayer(String playerName) {
        return Bukkit.getPlayer(playerName);
    }

    public PlayerData getPlayerData(Player player) {
        return SkillAPI.getPlayerData(player);
    }
}
