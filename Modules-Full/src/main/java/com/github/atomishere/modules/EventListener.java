package com.github.atomishere.modules;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/*
    This classes sole purpose is to boost my Ego,

    You can delete it if you please.
 */
public class EventListener implements Listener {
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.GOLD + "The creator of the plugin Modules has joined your server\nPlease say thanks for making such a Great plugin!");
    }
}
