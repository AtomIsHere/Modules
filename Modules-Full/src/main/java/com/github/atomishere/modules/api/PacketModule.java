package com.github.atomishere.modules.api;

import com.comphenix.protocol.events.PacketEvent;
import static com.comphenix.protocol.PacketType.Play.Client;
import static com.comphenix.protocol.PacketType.Play.Server;

public abstract class PacketModule implements Module {
    public abstract void onPacketSend(PacketEvent event);

    public abstract void onPacketReceive(PacketEvent event);

    public abstract Client getClientPacketType();
    public abstract Server getServerPacketType();
}
