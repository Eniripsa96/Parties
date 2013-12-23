package com.sucy.party.mccore;

import com.rit.sucy.scoreboard.BoardManager;
import com.rit.sucy.scoreboard.PlayerBoards;
import com.rit.sucy.scoreboard.StatBoard;
import com.sucy.party.Parties;
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
        StatBoard board = new StatBoard("Party", plugin.getName());
        board.addStats(new PartyStats(plugin, player.getName()));
        PlayerBoards boards = BoardManager.getPlayerBoards(player.getName());
        boards.removeBoards(plugin.getName());
        boards.addBoard(board);
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
