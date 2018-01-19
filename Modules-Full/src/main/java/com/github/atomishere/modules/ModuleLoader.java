package com.github.atomishere.modules;

import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.github.atomishere.modules.api.CommandModule;
import com.github.atomishere.modules.api.EventModule;
import com.github.atomishere.modules.api.Module;
import com.github.atomishere.modules.api.PacketModule;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Logger;

public class ModuleLoader {
    private final File modulesFile;
    private final Logger logger;
    private final ModulesPlugin plugin;
    private final List<Module> modules = Lists.newArrayList();

    public ModuleLoader(ModulesPlugin plugin, Logger logger, File modulesFile) {
        this.logger = logger;
        this.modulesFile = modulesFile;
        this.plugin = plugin;
    }

    public ModuleLoader(ModulesPlugin plugin, File modulesFile) {
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
                registerCommand((CommandModule) module);
                logger.warning("Loading command module ");
            } else if(module instanceof EventModule) {
                Bukkit.getServer().getPluginManager().registerEvents((EventModule) module, plugin);
            } else if(plugin.hasProtocolLib() && module instanceof PacketModule) {
                registerPacketModule((PacketModule) module);
            } else if(!plugin.hasProtocolLib() && module instanceof PacketModule) {
                logger.warning("Tried to load a Packet Module while ProtocolLib is not installed!");
            }
        }
    }

    private void registerPacketModule(final PacketModule module) {
        if(module.getServerPacketType() != null) {
            plugin.getManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, module.getServerPacketType()) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    module.onPacketSend(event);
                }
            });
        }
        if(module.getClientPacketType() != null) {
            plugin.getManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, module.getClientPacketType()) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    module.onPacketRecive(event);
                }
            });
        }
    }

    private void registerCommand(CommandModule module) {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register(module.getName(), module);
        } catch(IllegalAccessException ex) {
            ex.printStackTrace();
        } catch(NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public void disableModules() {
        modules.clear();
    }
}
