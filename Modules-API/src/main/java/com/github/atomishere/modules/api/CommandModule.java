package com.github.atomishere.modules.api;

import org.bukkit.command.defaults.BukkitCommand;

import java.util.List;

public abstract class CommandModule extends BukkitCommand implements Module {
    protected CommandModule(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }
}
