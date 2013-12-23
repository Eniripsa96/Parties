package com.sucy.party;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Update task for parties
 */
public class UpdateTask extends BukkitRunnable {

    Parties plugin;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public UpdateTask(Parties plugin) {
        this.plugin = plugin;
        runTaskTimer(plugin, 20, 20);
    }

    /**
     * Update the parties
     */
    @Override
    public void run() {
        plugin.update();
    }
}
