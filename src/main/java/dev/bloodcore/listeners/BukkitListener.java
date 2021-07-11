package dev.bloodcore.listeners;

import dev.bloodcore.Core;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.Bukkit;
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
        } else {
            if (!Config.JOIN_MESSAGE.getString().isEmpty()) {
                e.setJoinMessage(ChatUtil.color(Config.JOIN_MESSAGE.getString()
                        .replace("%color%", user.getRank().getColor())
                        .replace("%prefix%", user.getRank().getPrefix())
                        .replace("%player%", user.name())
                ));
            }
        }
        Core.i().getUsers().add(user);
        Core.i().getMongoManager().updateUser(user);

        if (Config.SPAWN_ON_JOIN.getBoolean()) {
            if (Core.i().getConfig().contains("spawn")) {
                String[] info = Core.i().getConfig().getString("spawn").split(";");
                Location spawn = new Location(
                        Bukkit.getWorld(info[0]),
                        Double.parseDouble(info[1]),
                        Double.parseDouble(info[2]),
                        Double.parseDouble(info[3]),
                        Float.parseFloat(info[4]),
                        Float.parseFloat(info[5])
                );
                e.getPlayer().teleport(spawn);
            }
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        User user = Core.i().getUser(e.getPlayer());
        if (user != null) {
            if (user.isVanished()) {
                e.setQuitMessage(null);
            } else {
                if (!Config.JOIN_MESSAGE.getString().isEmpty()) {
                    e.setQuitMessage(ChatUtil.color(Config.QUIT_MESSAGE.getString()
                            .replace("%color%", user.getRank().getColor())
                            .replace("%prefix%", user.getRank().getPrefix())
                            .replace("%player%", user.name())
                    ));
                }
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
            } else {
                if (!Config.JOIN_MESSAGE.getString().isEmpty()) {
                    e.setLeaveMessage(ChatUtil.color(Config.QUIT_MESSAGE.getString()
                            .replace("%color%", user.getRank().getColor())
                            .replace("%prefix%", user.getRank().getPrefix())
                            .replace("%player%", user.name())
                    ));
                }
            }
            user.unload();
            Core.i().getUsers().remove(user);
        }
    }
}
