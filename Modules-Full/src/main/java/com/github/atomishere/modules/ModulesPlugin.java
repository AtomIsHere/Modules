package com.github.atomishere.modules;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ModulesPlugin extends JavaPlugin {
    private ModuleLoader loader;
    private Logger logger = getLogger();

    @Override
    public void onLoad() {
        loader = new ModuleLoader(this, getDataFolder());
        loader.loadModules();
    }

    @Override
    public void onEnable() {
        //Load modules later modules so modules can use other plugin's API.
        Bukkit.getServer().getScheduler().runTaskLater(this, new Runnable() {
            public void run() {
                loader.registerModules();
            }
        }, 100L);
    }

    @Override
    public void  onDisable() {
        loader.disableModules();
        loader = null;
        logger = null;
    }
}
