package dev.bloodcore.listeners;

import dev.bloodcore.Core;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerJoinEvent e) {
        User user = new User(e.getPlayer());

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

        if (e.getPlayer().hasPermission("blood.command.staffmode") && Core.i().getStaffManager().getStaffConfig().getBoolean("staffmode.enable-on-join")) {
            Core.i().getStaffManager().enterStaffMode(user);
        }

        if (e.getPlayer().hasPermission("blood.command.vanish") && Core.i().getStaffManager().getStaffConfig().getBoolean("vanish.enable-on-join")) {
            Core.i().getStaffManager().vanish(user);
        }

        if (e.getPlayer().hasPermission("blood.staff.alerts")) {
            String message = "&6[Staff] " + user.getRank().getColor() + user.name() + " &ehas connected to &6" + Config.SERVER_NAME + "&e.";
            String json = "{\"permission\": \"blood.staff.alerts\", \"message\": \"%s\"}";
            Core.i().getRedisManager().getPubJedis().publish("MESSAGING", String.format(json, message));
        }

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

    @EventHandler
    public void on(InventoryClickEvent e) {
        User user = Core.i().getUser((Player) e.getView().getPlayer());
        if (user != null) {
            if (user.getCurrentGUI() != null) {
                user.getCurrentGUI().handle(e);
            }
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent e) {
        User user = Core.i().getUser((Player) e.getView().getPlayer());
        if (user != null) {
            if (user.getCurrentGUI() != null) {
                user.getCurrentGUI().handle(e);
                user.setCurrentGUI(null);
            }
        }
    }
}
