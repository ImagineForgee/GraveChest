package com.github.imagineforgee.graveChest.listeners;

import com.github.imagineforgee.graveChest.GraveChest;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.InventoryView;

public class GraveCompassProtectionListener implements Listener {


    private boolean isGraveCompass(ItemStack item) {
        if (item == null || item.getType() != Material.COMPASS) return false;
        if (!item.hasItemMeta()) return false;
        if (!(item.getItemMeta() instanceof CompassMeta meta)) return false;
        return GraveChest.GRAVE_COMPASS_NAME.equals(meta.getDisplayName());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack dropped = event.getItemDrop().getItemStack();
        if (isGraveCompass(dropped)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        ItemStack current = event.getCurrentItem();
        ItemStack cursor  = event.getCursor();

        if ((action == InventoryAction.DROP_ONE_SLOT || action == InventoryAction.DROP_ALL_SLOT)
                && isGraveCompass(current)) {
            event.setCancelled(true);
            return;
        }

        if ((action == InventoryAction.DROP_ONE_CURSOR || action == InventoryAction.DROP_ALL_CURSOR)
                && isGraveCompass(cursor)) {
            event.setCancelled(true);
            return;
        }

        InventoryView view = event.getView();
        int topSize = view.getTopInventory().getSize();
        int rawSlot = event.getRawSlot();

        if (rawSlot < topSize && isGraveCompass(cursor)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        ItemStack dragged = event.getOldCursor();
        InventoryView view = event.getView();
        int topSize = view.getTopInventory().getSize();

        if (!isGraveCompass(dragged)) return;
        for (Integer rawSlot : event.getRawSlots()) {
            if (rawSlot < topSize) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
