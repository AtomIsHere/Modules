package com.github.atomishere.modules;

import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.github.atomishere.modules.api.*;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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

                JarFile moduleJar;
                try {
                    moduleJar = new JarFile(file);
                } catch(IOException ex) {
                    logger.warning(file.getName() + " is not a jar file.");
                    continue;
                }

                JarEntry entry = moduleJar.getJarEntry("module.properties");
                InputStream stream;
                try {
                    stream = moduleJar.getInputStream(entry);
                } catch(IOException ex) {
                    logger.warning(file.getName() + " does not contain module.properties");
                    ex.printStackTrace();
                    continue;
                }

                ModuleData data;
                try {
                    data = new ModuleData(stream);
                } catch(IOException ex) {
                    logger.warning(file.getName() + " does not contain module.properties");
                    ex.printStackTrace();
                    continue;
                } catch(InvalidModuleException ex) {
                    logger.warning(file.getName() + " has an invalid module.properties");
                    ex.printStackTrace();
                    continue;
                }

                ModuleClassLoader loader;
                try {
                    loader = new ModuleClassLoader(getClass().getClassLoader(), file, data);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    continue;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }

                logger.info("Loaded module " + data.getName());
                modules.add(loader.getModule());
            }
        } catch(NullPointerException ignored) {
        }
    }

    public void registerModules() {
        for(Module module : modules) {
            if(module instanceof CommandModule) {
                logger.info("Registering command module " + module.getData());
                registerCommand((CommandModule) module);
                logger.info("Registered command module " + module.getData());
            } else if(module instanceof EventModule) {
                logger.info("Registering event module " + module.getData());
                Bukkit.getServer().getPluginManager().registerEvents((EventModule) module, plugin);
                logger.info("Registered event module " + module.getData());
            } else if(plugin.hasProtocolLib() && module instanceof PacketModule) {
                logger.info("Registering packet module " + module.getData());
                registerPacketModule((PacketModule) module);
                logger.info("Registered packet module " + module.getData());
            } else if(!plugin.hasProtocolLib() && module instanceof PacketModule) {
                logger.severe("Tried to register a Packet Module while ProtocolLib is not installed!");
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
                    module.onPacketReceive(event);
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
