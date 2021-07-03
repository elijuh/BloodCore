package dev.bloodcore.listeners;

import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerLoginEvent e) {
        User user = new User(e.getPlayer());
        Core.i().getUsers().add(user);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerJoinEvent e) {
        User user = Core.i().getUser(e.getPlayer().getName());
        if (user == null) {
            user = new User(e.getPlayer());
            Core.i().getUsers().add(user);
        }
        Core.i().getMongoManager().updateUser(user);
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        User user = Core.i().getUser(e.getPlayer().getName());
        if (user != null) {
            user.unload();
            Core.i().getUsers().remove(user);
        }
    }

    @EventHandler
    public void on(PlayerKickEvent e) {
        User user = Core.i().getUser(e.getPlayer().getName());
        if (user != null) {
            user.unload();
            Core.i().getUsers().remove(user);
        }
    }
}
