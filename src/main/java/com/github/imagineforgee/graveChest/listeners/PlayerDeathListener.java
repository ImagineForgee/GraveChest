package com.github.imagineforgee.graveChest.listeners;

import com.github.imagineforgee.graveChest.GraveChest;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private static final int MAX_HEIGHT_CHECK = 5;
    private final GraveChest plugin;
    private final Map<UUID, Location> graveLocations = new HashMap<>();
    private final Map<UUID, Location> playerGraves = new HashMap<>();
    private final Map<Location, UUID> graveOwners = new HashMap<>();

    public PlayerDeathListener(GraveChest plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (plugin.getConfig().getBoolean("require-permission", true) &&
                !player.hasPermission("gravechest.use")) {
            return;
        }

        Location baseLoc = player.getLocation();
        World world = baseLoc.getWorld();
        if (world == null) return;

        String worldName = baseLoc.getWorld().getName();
        int x = baseLoc.getBlockX();
        int y = baseLoc.getBlockY();
        int z = baseLoc.getBlockZ();

        event.getDrops().removeIf(this::isGraveCompass);

        player.sendMessage("");
        player.sendMessage("§cYou died! Your grave has been placed at:");
        player.sendMessage(" §eWorld: " + worldName);
        player.sendMessage(String.format(" §eX: %d  Y: %d  Z: %d", x, y, z));
        player.sendMessage("");

        ItemStack[] drops = event.getDrops().toArray(new ItemStack[0]);
        event.getDrops().clear();

        String chestTypeConfig = plugin.getConfig().getString("chest-type", "auto").toLowerCase();
        boolean forceSingle = chestTypeConfig.equals("single");
        boolean forceDouble = chestTypeConfig.equals("double");

        boolean placed = false;
        for (int yOffset = 0; yOffset <= MAX_HEIGHT_CHECK; yOffset++) {
            Location loc = baseLoc.clone().add(0, yOffset, 0);
            Block mainBlock = loc.getBlock();
            Block rightBlock = mainBlock.getRelative(BlockFace.EAST);

            boolean mainClear = mainBlock.getType().isAir();
            boolean rightClear = rightBlock.getType().isAir();

            if ((forceDouble || (!forceSingle && drops.length > 27)) && mainClear && rightClear) {
                placeDoubleChest(player, mainBlock, rightBlock, drops);
                giveCompass(player, mainBlock.getLocation());
                placed = true;
                break;
            } else if (mainClear) {
                placeSingleChest(player, mainBlock, drops);
                giveCompass(player, mainBlock.getLocation());
                placed = true;
                break;
            }
        }

        if (!placed) {
            Block fallbackBlock = baseLoc.getBlock();
            if (!fallbackBlock.getType().isAir()) {
                fallbackBlock = baseLoc.add(0, 1, 0).getBlock();
            }
            placeSingleChest(player, fallbackBlock, drops);
            giveCompass(player, fallbackBlock.getLocation());
        }
    }

    private void placeSingleChest(Player player, Block block, ItemStack[] drops) {
        block.setType(Material.CHEST);
        BlockState state = block.getState();
        graveLocations.put(player.getUniqueId(), block.getLocation());
        playerGraves.put(player.getUniqueId(), block.getLocation());
        graveOwners.put(block.getLocation(), player.getUniqueId());
        if (state instanceof org.bukkit.block.Chest chest) {
            if (plugin.getConfig().getBoolean("use-custom-name", true)) {
                chest.setCustomName(player.getName() + "'s Grave");
            }
            chest.update();
            Inventory inv = chest.getBlockInventory();
            boolean dropOverflow = plugin.getConfig().getBoolean("drop-overflow-items", true);
            for (ItemStack item : drops) {
                if (item != null) {
                    Map<Integer, ItemStack> overflow = inv.addItem(item);
                    if (dropOverflow) {
                        overflow.values().forEach(i -> block.getWorld().dropItemNaturally(block.getLocation(), i));
                    }
                }
            }
        }

        scheduleGraveRemoval(block, null);
    }

    private void placeDoubleChest(Player player, Block leftBlock, Block rightBlock, ItemStack[] drops) {
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
        graveLocations.put(player.getUniqueId(), leftBlock.getLocation());
        playerGraves.put(player.getUniqueId(), leftBlock.getLocation());
        graveOwners.put(leftBlock.getLocation(), player.getUniqueId());

        BlockState state = leftBlock.getState();
        if (state instanceof org.bukkit.block.Chest chest) {
            if (plugin.getConfig().getBoolean("use-custom-name", true)) {
                chest.setCustomName(player.getName() + "'s Grave");
            }
            chest.update();
            Inventory inv = chest.getInventory();
            boolean dropOverflow = plugin.getConfig().getBoolean("drop-overflow-items", true);
            for (ItemStack item : drops) {
                if (item != null) {
                    Map<Integer, ItemStack> overflow = inv.addItem(item);
                    if (dropOverflow) {
                        overflow.values().forEach(i -> leftBlock.getWorld().dropItemNaturally(leftBlock.getLocation(), i));
                    }
                }
            }
        }

        scheduleGraveRemoval(leftBlock, rightBlock);
    }

    private void scheduleGraveRemoval(Block main, Block secondary) {
        int minutes = plugin.getConfig().getInt("expire-time-minutes", 0);
        if (minutes <= 0) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (main.getType() == Material.CHEST) {
                main.setType(Material.AIR);
            }
            if (secondary != null && secondary.getType() == Material.CHEST) {
                secondary.setType(Material.AIR);
            }
        }, 20L * 60 * minutes);
    }

    private void giveCompass(Player player, Location targetLocation) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) Bukkit.getItemFactory().getItemMeta(Material.COMPASS);
        if (meta != null) {
            meta.setLodestone(targetLocation);
            meta.setLodestoneTracked(false);
            meta.setDisplayName(ChatColor.GOLD + "Grave Compass");
            compass.setItemMeta(meta);
            player.getInventory().addItem(compass);
        }
    }

    private boolean isGraveCompass(ItemStack item) {
        if (item == null || item.getType() != Material.COMPASS) return false;
        if (!item.hasItemMeta()) return false;
        if (!(item.getItemMeta() instanceof CompassMeta meta)) return false;
        return GraveChest.GRAVE_COMPASS_NAME.equals(meta.getDisplayName());
    }

    public Map<UUID, Location> getGraveLocations() {
        return graveLocations;
    }

    public Map<UUID, Location> getPlayerGraves() {
        return playerGraves;
    }

    public Map<Location, UUID> getGraveOwners() {
        return graveOwners;
    }
}
