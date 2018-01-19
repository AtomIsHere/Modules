package com.github.atomishere.modules.api;

import org.bukkit.event.Listener;

public abstract class EventModule implements Module, Listener {
    private final ModuleData data;

    public EventModule(ModuleData data) {
        this.data = data;
    }

    public final ModuleData getData() {
        return data;
    }
}
