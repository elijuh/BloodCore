package dev.bloodcore.listeners;

import com.google.common.collect.ImmutableMap;
import dev.bloodcore.Core;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.ConfigPlaceholder;
import dev.bloodcore.etc.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

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

    @EventHandler
    public void on(AsyncPlayerChatEvent e) {
        User user = Core.i().getUser(e.getPlayer().getName());
        if (user == null) {
            e.setCancelled(true);
        } else {
            e.setFormat(Config.GLOBAL_CHAT_FORMAT.getString(ImmutableMap.of(
                    ConfigPlaceholder.PREFIX, user.getRank().getPrefix(),
                    ConfigPlaceholder.PLAYER, "%1$s",
                    ConfigPlaceholder.GLOBAL_CHAT_MESSAGE, "%2$s")
            ));
        }
    }
}
