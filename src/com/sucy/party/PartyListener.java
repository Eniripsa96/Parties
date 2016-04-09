package com.sucy.party;

import com.sucy.party.hook.Hooks;
import com.sucy.party.hook.InstancesHook;
import com.sucy.party.mccore.PartyBoardManager;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.event.PlayerExperienceGainEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

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
            if (attacker != null)
            {
                IParty targetParty = Hooks.getParty(target);
                IParty attackerParty = Hooks.getParty(attacker);

                // Cancel damage when in the same party
                if (targetParty != null && targetParty == attackerParty) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Handles party chat toggles
     *
     * @param event event details
     */
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.isToggled(event.getPlayer().getName())) {
            IParty party = Hooks.getParty(event.getPlayer());
            if (party == null || party.isEmpty()) {
                plugin.toggle(event.getPlayer().getName());
                return;
            }
            event.setCancelled(true);
            party.sendMessage(event.getPlayer(), event.getMessage());
        }
    }

    /**
     * Share experience between members
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExpGain(PlayerExperienceGainEvent event) {
        if (event.getSource() == ExpSource.COMMAND) return;
        if (plugin.isDebug()) plugin.getLogger().info("Exp already being shared with " + event.getPlayerData().getPlayerName());
        if (shared) return;
        IParty party = Hooks.getParty(event.getPlayerData().getPlayer());
        if (plugin.isDebug()) plugin.getLogger().info(event.getPlayerData().getPlayerName() + " has a party? " + (party != null));
        if (party != null) {
            event.setCancelled(true);
            shared = true;
            party.giveExp(event.getPlayerData().getPlayer(), event.getExp(), event.getSource());
            shared = false;
            if (plugin.isDebug()) plugin.getLogger().info("Exp was shared!");
        }
    }

    /**
     * Sets up scoreboards for players when they join
     *
     * @param event event details
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Party party = plugin.getParty(event.getPlayer());
        if (plugin.isUsingScoreboard() && party != null && !party.isEmpty()) {
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

    /**
     * Handles item distribution to a party
     *
     * @param event event details
     */
    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        IParty party = Hooks.getParty(event.getPlayer());
        if (party != null)
        {
            ItemStack item = event.getItem().getItemStack();

            String mode = plugin.getShareMode().toLowerCase();
            if (mode.equals("sequential")) {
                int count = item.getAmount();
                item.setAmount(1);
                for (int i = 0; i < count; i++) {
                    party.getSequentialPlayer().getInventory().addItem(item);
                }
            }
            else if (mode.equals("random")) {
                int count = item.getAmount();
                item.setAmount(1);
                for (int i = 0; i < count; i++) {
                    party.getRandomPlayer().getInventory().addItem(item);
                }
            }
            else if (mode.equals("sequential-stack")) {
                party.getSequentialPlayer().getInventory().addItem(item);
            }
            else if (mode.equals("random-stack")) {
                party.getRandomPlayer().getInventory().addItem(item);
            }
            else return;

            event.setCancelled(true);
            event.getItem().remove();
        }
    }
}
