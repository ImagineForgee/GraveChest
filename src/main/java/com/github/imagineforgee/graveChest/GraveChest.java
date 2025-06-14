package com.github.imagineforgee.graveChest;

import com.github.imagineforgee.graveChest.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class GraveChest extends JavaPlugin {

    public static final String GRAVE_COMPASS_NAME = "§6Grave Compass";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PlayerDeathListener deathListener = new PlayerDeathListener(this);
        getServer().getPluginManager().registerEvents(deathListener, this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this, deathListener.getPlayerGraves()), this);
        getServer().getPluginManager().registerEvents(new GraveChestBreakListener(this,
                deathListener.getGraveOwners(), deathListener.getPlayerGraves()), this);
        getServer().getPluginManager().registerEvents(new ChestEmptyListener(this), this);
        getServer().getPluginManager().registerEvents(new GraveCompassMoveBlockListener(), this);
        getServer().getPluginManager().registerEvents(new GraveCompassProtectionListener(), this);

        getLogger().info("GraveChest enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GraveChest disabled.");
    }
}
