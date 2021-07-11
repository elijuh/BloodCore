package dev.bloodcore.listeners;

import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import dev.bloodcore.etc.YamlStorage;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerJoinEvent e) {
        User user = new User(e.getPlayer());
        if (user.isVanished()) {
            e.setJoinMessage(null);
        }
        Core.i().getUsers().add(user);
        Core.i().getMongoManager().updateUser(user);

        YamlStorage worldConfig = Core.i().getWorldConfig();
        for (String key : worldConfig.getKeys(false)) {
            if (worldConfig.get(key + ".spawn") != null) {
                Location loc = (Location) worldConfig.get(key + ".spawn");
                e.getPlayer().teleport(loc);
                return;
            }
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        User user = Core.i().getUser(e.getPlayer());
        if (user != null) {
            if (user.isVanished()) {
                e.setQuitMessage(null);
            }
            user.unload();
            Core.i().getUsers().remove(user);
        }
    }

    @EventHandler
    public void on(PlayerKickEvent e) {
        User user = Core.i().getUser(e.getPlayer());
        if (user != null) {
            if (user.isVanished()) {
                e.setLeaveMessage(null);
            }
            user.unload();
            Core.i().getUsers().remove(user);
        }
    }
}
