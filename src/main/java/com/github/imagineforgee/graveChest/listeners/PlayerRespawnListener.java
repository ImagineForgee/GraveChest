package com.github.imagineforgee.graveChest.listeners;

import com.github.imagineforgee.graveChest.GraveChest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.Map;
import java.util.UUID;

public class PlayerRespawnListener implements Listener {

    private final GraveChest plugin;
    private final Map<UUID, Location> graveLocations;

    public PlayerRespawnListener(GraveChest plugin, Map<UUID, Location> graveLocations) {
        this.plugin = plugin;
        this.graveLocations = graveLocations;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location grave = graveLocations.remove(player.getUniqueId());
        if (grave == null) return;

        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) compass.getItemMeta();
        if (meta != null) {
            meta.setLodestone(grave);
            meta.setLodestoneTracked(false);
            meta.setDisplayName(GraveChest.GRAVE_COMPASS_NAME);
            compass.setItemMeta(meta);
        }

        player.getInventory().addItem(compass);
    }
}
