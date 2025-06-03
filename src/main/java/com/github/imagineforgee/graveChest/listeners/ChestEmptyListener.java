package com.github.imagineforgee.graveChest.listeners;

import com.github.imagineforgee.graveChest.GraveChest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

public class ChestEmptyListener implements Listener {

    private final GraveChest plugin;

    public ChestEmptyListener(GraveChest plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        checkInventoryEmpty(event.getInventory(), event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            checkInventoryEmpty(event.getInventory(), event.getWhoClicked());
        }, 1L);
    }

    private void checkInventoryEmpty(Inventory inventory, HumanEntity viewer) {
        BlockState state = inventory.getLocation() != null
                ? inventory.getLocation().getBlock().getState()
                : null;

        if (!(state instanceof Chest chest)) return;

        boolean isEmpty = true;
        for (ItemStack item : chest.getBlockInventory().getContents()) {
            if (item != null && !item.getType().isAir()) {
                isEmpty = false;
                break;
            }
        }

        if (isEmpty && viewer instanceof Player player) {
            removeGraveCompass(player);
        }
    }

    private void removeGraveCompass(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.COMPASS &&
                    item.getItemMeta() instanceof CompassMeta meta &&
                    GraveChest.GRAVE_COMPASS_NAME.equals(meta.getDisplayName())) {

                player.getInventory().setItem(i, null);
                break;
            }
        }
    }
}

