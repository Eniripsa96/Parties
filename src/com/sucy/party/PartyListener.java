package com.sucy.party;

import com.sucy.party.mccore.PartyBoardManager;
import com.sucy.skill.api.event.PlayerExperienceGainEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener for party mechanics
 */
public class PartyListener implements Listener {

    private Parties plugin;
    private boolean shared = false;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public PartyListener(Parties plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Prevent party members from damaging one another
     *
     * @param event event details
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        // Make sure the defender is a player
        if (event.getEntity() instanceof Player) {
            Player target = (Player)event.getEntity();

            // Get the attacker
            Player attacker = null;
            if (event.getDamager() instanceof Player) attacker = (Player)event.getDamager();
            else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile)event.getDamager();
                if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
                    attacker = (Player)projectile.getShooter();
                }
            }

            // Make sure the attacker is a player
            if (attacker != null) {

                Party targetParty = plugin.getParty(target);
                Party attackerParty = plugin.getParty(attacker);

                // Cancel damage when in the same party
                if (targetParty != null && targetParty == attackerParty && targetParty.isMember(target) && attackerParty.isMember(attacker)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Share experience between members
     *
     * @param event event details
     */
    @EventHandler
    public void onExpGain(PlayerExperienceGainEvent event) {
        if (shared) return;
        Party party = plugin.getParty(event.getPlayerData().getPlayer());
        if (party != null) {
            event.setCancelled(true);
            shared = true;
            party.giveExp(event.getPlayerData().getPlayer(), event.getExp());
            shared = false;
        }
    }

    /**
     * Sets up scoreboards for players when they join
     *
     * @param event event details
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.isUsingScoreboard()) {
            PartyBoardManager.applyBoard(plugin, event.getPlayer());
        }
    }

    /**
     * Removes members, changes leaders, or disbands parties upon disconnect
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        // The player must be in a party
        Party party = plugin.getParty(event.getPlayer());
        if (party != null) {

            // Decline invitations on quit
            if (party.isInvited(event.getPlayer())) {
                party.decline(event.getPlayer());
            }

            // Removing players on disconnect
            else if (plugin.isRemoveOnDc()) {
                party.removeMember(event.getPlayer());
            }

            // Changing leader on disconnect
            else if (plugin.isNewLeaderOnDc()) {
                party.changeLeader();
            }

            // Removes a party when it's online size reaches 0
            if (party.getOnlinePartySize() == 0) {
                plugin.removeParty(party);
            }
        }
    }
}
