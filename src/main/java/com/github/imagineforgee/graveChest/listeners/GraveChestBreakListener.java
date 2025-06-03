package com.github.imagineforgee.graveChest.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import com.github.imagineforgee.graveChest.GraveChest;
import org.bukkit.inventory.meta.CompassMeta;

public class GraveChestBreakListener implements Listener {

    private final GraveChest plugin;
    private final Map<Location, UUID> graveOwners;
    private final Map<UUID, Location> playerGraves;

    public GraveChestBreakListener(GraveChest plugin, Map<Location, UUID> graveOwners, Map<UUID, Location> playerGraves) {
        this.plugin = plugin;
        this.graveOwners = graveOwners;
        this.playerGraves = playerGraves;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() != Material.CHEST) return;

        UUID owner = graveOwners.remove(block.getLocation());
        if (owner == null) return;

        playerGraves.remove(owner);

        Player ownerPlayer = plugin.getServer().getPlayer(owner);
        if (ownerPlayer != null && ownerPlayer.isOnline()) {
            removeGraveCompass(ownerPlayer);
        }
    }

    private void removeGraveCompass(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.COMPASS &&
                    item.getItemMeta() instanceof CompassMeta meta &&
                    "§6Grave Compass".equals(meta.getDisplayName())) {

                player.getInventory().setItem(i, null);
                break;
            }
        }
    }

}
