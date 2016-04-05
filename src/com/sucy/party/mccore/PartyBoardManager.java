package com.sucy.party.mccore;

import com.rit.sucy.scoreboard.BoardManager;
import com.rit.sucy.scoreboard.PlayerBoards;
import com.rit.sucy.scoreboard.StatBoard;
import com.sucy.party.Parties;
import com.sucy.party.lang.PartyNodes;
import org.bukkit.entity.Player;

/**
 * Manages giving and removing scoreboards to players through MCCore
 */
public class PartyBoardManager {

    /**
     * Applies a scoreboard for the player
     *
     * @param plugin plugin reference
     * @param player player to apply to
     */
    public static void applyBoard(Parties plugin, Player player) {

        String title = plugin.getMessage(PartyNodes.SCOREBOARD, false).get(0);

        StatBoard board = new StatBoard(title, plugin.getName());
        board.addStats(new PartyStats(plugin, player, plugin.isLevelScoreboard()));
        PlayerBoards boards = BoardManager.getPlayerBoards(player.getName());
        boards.removeBoards(plugin.getName());
        boards.addBoard(board);
    }

    /**
     * Removes boards for this plugin on empty party
     *
     * @param plugin plugin reference
     * @param player player to remove for
     */
    public static void clearBoard(Parties plugin, Player player) {
        BoardManager.getPlayerBoards(player.getName()).removeBoards(plugin.getName());
    }

    /**
     * Clears all of the scoreboards for the plugin
     *
     * @param plugin plugin reference
     */
    public static void clearBoards(Parties plugin) {
        BoardManager.clearPluginBoards(plugin.getName());
    }
}
