package com.github.atomishere.modules.api;

import com.comphenix.protocol.events.PacketEvent;

import static com.comphenix.protocol.PacketType.Play.Client;
import static com.comphenix.protocol.PacketType.Play.Server;

public abstract class PacketModule implements Module {
    private final ModuleData data;

    public PacketModule(ModuleData data) {
        this.data = data;
    }

    public final ModuleData getData() {
        return data;
    }

    public Client getClientPacketType() {
        return null;
    }
    public Server getServerPacketType() {
        return null;
    }

    public abstract void onPacketSend(PacketEvent event);

    public abstract void onPacketReceive(PacketEvent event);

}
