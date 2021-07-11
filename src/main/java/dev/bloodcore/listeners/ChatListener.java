package dev.bloodcore.listeners;

import dev.bloodcore.Core;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void on(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        User user = Core.i().getUser(e.getPlayer());
        if (user == null) {
            e.setCancelled(true);
        } else {
            long remainingTime = (Config.GLOBAL_CHAT_TIMER.getInt() * 1000L) - (System.currentTimeMillis() - (long) user.getData().getOrDefault("lastChat", 0L));
            if (remainingTime > 0) {
                p.sendMessage(ChatUtil.color(Config.GLOBAL_CHAT_COOLDOWN_MESSAGE.getString()
                        .replace("%delay%", Double.toString(Math.round(remainingTime / 100.0) / 10.0))));
                e.setCancelled(true);
                return;
            }
            e.setFormat(ChatUtil.color(Config.GLOBAL_CHAT_FORMAT.getString()
                    .replace("%rank_prefix%", user.getRank().getPrefix())
                    .replace("%player%", "%1$s")
                    .replace("%message%", "%2$s")
            ));
            user.getData().put("lastChat", System.currentTimeMillis());
        }
    }
}
