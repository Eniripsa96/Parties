package com.sucy.party.inject;

import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Player;

/**
 * Parties © 2017
 * com.sucy.party.inject.Server
 *
 * Wrapper around static methods than can have alternate implementations
 * injected for testing purposes.
 */
public class Server {

    public static boolean isOnline(String playerName) {
        return context.isOnline(playerName);
    }

    public static Player getPlayer(String playerName) {
        return context.getPlayer(playerName);
    }

    public static PlayerData getPlayerData(Player player) {
        return context.getPlayerData(player);
    }

    public static PlayerClass getClass(Player player) {
        return getPlayerData(player).getMainClass();
    }

    public static int getLevel(String name) {
        if (isOnline(name)) {
            PlayerClass playerClass = getClass(getPlayer(name));
            if (playerClass != null) {
                return playerClass.getLevel();
            }
        }
        return 0;
    }

    public static boolean hasClass(Player player) {
        return getPlayerData(player).hasClass();
    }

    private static InjectInterface context = new InjectInterface();

    static void setContext(InjectInterface injectedContext) {
        context = injectedContext;
    }
}
