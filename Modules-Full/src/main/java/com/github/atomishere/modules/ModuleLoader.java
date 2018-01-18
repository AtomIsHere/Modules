package com.github.atomishere.modules;

import com.github.atomishere.modules.api.CommandModule;
import com.github.atomishere.modules.api.EventModule;
import com.github.atomishere.modules.api.Module;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Logger;

public class ModuleLoader {
    private final File modulesFile;
    private final Logger logger;
    private final JavaPlugin plugin;
    private final List<Module> modules = Lists.newArrayList();

    public ModuleLoader(JavaPlugin plugin, Logger logger, File modulesFile) {
        this.logger = logger;
        this.modulesFile = modulesFile;
        this.plugin = plugin;
    }

    public ModuleLoader(JavaPlugin plugin, File modulesFile) {
        this(plugin, plugin.getLogger(), modulesFile);
    }

    public void loadModules() {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("Module_") && name.endsWith(".jar");
            }
        };

        try {
            for (File file : modulesFile.listFiles(filter)) {
                logger.info("Loading module " + file.getName());

                ModuleClassLoader loader;
                try {
                    loader = new ModuleClassLoader(getClass().getClassLoader(), file);
                } catch (MalformedURLException ex) {
                    for (StackTraceElement element : ex.getStackTrace()) {
                        Bukkit.getServer().getLogger().severe(element.toString());
                    }
                    continue;
                } catch (Exception ex) {
                    for (StackTraceElement element : ex.getStackTrace()) {
                        logger.info(element.toString());
                    }
                    continue;
                }

                modules.add(loader.getModule());
            }
        } catch(NullPointerException ignored) {
        }
    }

    public void registerModules() {
        for(Module module : modules) {
            if(module instanceof CommandModule) {
                CommandModule commandModule = (CommandModule) module;
                try {
                    final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

                    bukkitCommandMap.setAccessible(true);
                    CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

                    commandMap.register(commandModule.getName(), commandModule);
                } catch(IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch(NoSuchFieldException ex) {
                    ex.printStackTrace();
                }
            } else if(module instanceof EventModule) {
                Bukkit.getServer().getPluginManager().registerEvents((EventModule) module, plugin);
            }
        }
    }

    public void disableModules() {
        modules.clear();
    }
}
