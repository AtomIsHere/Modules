package com.github.atomishere.modules;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ModulesPlugin extends JavaPlugin {
    private ModuleLoader loader;

    private Logger logger = getLogger();

    private boolean protocolLib = false;
    @Getter
    private ProtocolManager manager = null;

    @Override
    public void onLoad() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        loader = new ModuleLoader(this, getDataFolder());
        logger.info("Loading modules");
        loader.loadModules();
        logger.info("Loaded modules");
    }

    @Override
    public void onEnable() {
        if(getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            manager = ProtocolLibrary.getProtocolManager();
            protocolLib = true;
        }

        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
        //Load modules later modules so modules can use other plugin's API.
        getServer().getScheduler().runTaskLater(this, new Runnable() {
            public void run() {
                logger.info("Registering modules");
                loader.registerModules();
                logger.info("Registered modules");
            }
        }, 100L);
    }

    @Override
    public void  onDisable() {
        loader.disableModules();
        loader = null;
        logger = null;
    }

    public boolean hasProtocolLib() {
        return protocolLib;
    }
}
