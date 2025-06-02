package com.github.imagineforgee.graveChest.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener {

    private static final int MAX_HEIGHT_CHECK = 5;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location baseLoc = player.getLocation();
        World world = baseLoc.getWorld();
        if (world == null) return;

        ItemStack[] drops = event.getDrops().toArray(new ItemStack[0]);
        event.getDrops().clear();

        boolean placed = false;

        for (int yOffset = 0; yOffset <= MAX_HEIGHT_CHECK; yOffset++) {
            Location loc = baseLoc.clone().add(0, yOffset, 0);
            Block mainBlock = loc.getBlock();
            Block rightBlock = mainBlock.getRelative(BlockFace.EAST);

            boolean mainClear = mainBlock.getType().isAir();
            boolean rightClear = rightBlock.getType().isAir();

            if (drops.length > 27) {
                if (mainClear && rightClear) {
                    placeDoubleChest(mainBlock, rightBlock, drops);
                    placed = true;
                    break;
                }
            } else {
                if (mainClear) {
                    placeSingleChest(mainBlock, drops);
                    placed = true;
                    break;
                }
            }
        }

        if (!placed) {
            Block fallbackBlock = baseLoc.getBlock();
            if (!fallbackBlock.getType().isAir()) {
                fallbackBlock = baseLoc.add(0, 1, 0).getBlock();
            }
            placeSingleChest(fallbackBlock, drops);
        }
    }

    private void placeSingleChest(Block block, ItemStack[] drops) {
        block.setType(Material.CHEST);
        Inventory inv = ((org.bukkit.block.Chest) block.getState()).getBlockInventory();
        for (ItemStack item : drops) {
            if (item != null) inv.addItem(item);
        }
    }

    private void placeDoubleChest(Block leftBlock, Block rightBlock, ItemStack[] drops) {
        leftBlock.setType(Material.CHEST);
        rightBlock.setType(Material.CHEST);

        Chest leftData = (Chest) Bukkit.createBlockData(Material.CHEST);
        Chest rightData = (Chest) Bukkit.createBlockData(Material.CHEST);

        leftData.setFacing(BlockFace.NORTH);
        rightData.setFacing(BlockFace.NORTH);

        leftData.setType(Chest.Type.LEFT);
        rightData.setType(Chest.Type.RIGHT);

        leftBlock.setBlockData(leftData, false);
        rightBlock.setBlockData(rightData, false);

        Inventory inv = ((org.bukkit.block.Chest) leftBlock.getState()).getInventory();
        for (ItemStack item : drops) {
            if (item != null) inv.addItem(item);
        }
    }
}
