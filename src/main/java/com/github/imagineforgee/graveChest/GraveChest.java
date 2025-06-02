package com.github.imagineforgee.graveChest;

import com.github.imagineforgee.graveChest.listeners.PlayerDeathListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class GraveChest extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getLogger().info("GraveChest enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GraveChest disabled.");
    }
}
