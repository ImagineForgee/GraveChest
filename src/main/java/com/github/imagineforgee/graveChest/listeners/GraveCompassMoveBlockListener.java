package com.github.imagineforgee.graveChest.listeners;

import com.github.imagineforgee.graveChest.GraveChest;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

public class GraveCompassMoveBlockListener implements Listener {

    private boolean isGraveCompass(ItemStack item) {
        if (item == null || item.getType() != Material.COMPASS) return false;
        if (!item.hasItemMeta()) return false;
        if (!(item.getItemMeta() instanceof CompassMeta meta)) return false;
        return GraveChest.GRAVE_COMPASS_NAME.equals(meta.getDisplayName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            ItemStack clicked = event.getCurrentItem();
            if (isGraveCompass(clicked)) {
                event.setCancelled(true);
                return;
            }
        }

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        InventoryView view = event.getView();
        int rawSlot = event.getRawSlot();

        if (rawSlot < view.getTopInventory().getSize() && isGraveCompass(cursor)) {
            event.setCancelled(true);
            return;
        }

        if (rawSlot < view.getTopInventory().getSize() && isGraveCompass(current)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryView view = event.getView();
        int topSize = view.getTopInventory().getSize();

        for (Integer rawSlot : event.getRawSlots()) {
            if (rawSlot < topSize) {
                ItemStack toBePlaced = event.getOldCursor();
                if (isGraveCompass(toBePlaced)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}