package dev.bloodcore.listeners;

import dev.bloodcore.Core;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.DecimalFormat;
import java.util.HashMap;

public class ChatListener implements Listener {
    private final HashMap<Player, Long> cooldowns = new HashMap<>();
    private final DecimalFormat df = new DecimalFormat("#.#");

    @EventHandler
    public void on(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        User user = Core.i().getUser(e.getPlayer());
        if (user == null) {
            e.setCancelled(true);
        } else {
            long remainingTime = (Config.GLOBAL_CHAT_TIMER.getInt() * 1000L) - (System.currentTimeMillis() - cooldowns.getOrDefault(p, 0L));
            if (remainingTime > 0) {
                p.sendMessage(ChatUtil.color(Config.GLOBAL_CHAT_COOLDOWN_MESSAGE.getString().replace("%delay%", "" + df.format((double) remainingTime / 1000D))));
                e.setCancelled(true);
                return;
            }
            e.setFormat(ChatUtil.color(Config.GLOBAL_CHAT_FORMAT.getString()
                    .replace("%rank_prefix%", user.getRank().getPrefix())
                    .replace("%player%", "%1$s")
                    .replace("%message%", "%2$s")
            ));
            cooldowns.put(p, System.currentTimeMillis());
        }
    }
}
