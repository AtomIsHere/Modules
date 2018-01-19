package com.github.atomishere.modules.api;

import org.bukkit.command.defaults.BukkitCommand;

import java.util.List;

public abstract class CommandModule extends BukkitCommand implements Module {
    private final ModuleData data;

    public CommandModule(String name, String description, String usageMessage, List<String> aliases, ModuleData data) {
        super(name, description, usageMessage, aliases);
        this.data = data;
    }

    public final ModuleData getData() {
        return data;
    }
}
