package dev.bloodcore.listeners;

import dev.bloodcore.Core;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.punishments.PType;
import dev.bloodcore.punishments.PunishmentManager;
import dev.bloodcore.utils.ChatUtil;
import org.bson.Document;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PunishmentListener implements Listener {
    private final PunishmentManager manager = Core.i().getPunishmentManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(AsyncPlayerPreLoginEvent e) {
        Document data = Core.i().getMongoManager().getUserFromUUID(e.getUniqueId().toString());
        if (data == null) {
            return;
        }
        Document ban = manager.getActiveBan(data);
        if (ban == null) return;
        PType type = PType.valueOf(ban.getString("type"));

        if (type == PType.IPBAN) {
            String message = Messages.BAN_SCREEN.getString()
                    .replace("%bantype%", "IP-banned")
                    .replace("%length%", "Permanent")
                    .replace("%reason%", ban.getString("reason"))
                    .replace("%appeal%", Messages.APPEAL.getString());

            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            e.setKickMessage(ChatUtil.color(message));
        } else if (type == PType.BAN) {
            String message = Messages.BAN_SCREEN.getString()
                    .replace("%bantype%", "banned")
                    .replace("%length%", ban.getLong("length") == -1 ? "Permanent" : manager.formatMillis(ban.getLong("time") + ban.getLong("length") - System.currentTimeMillis()))
                    .replace("%reason%", ban.getString("reason"))
                    .replace("%appeal%", Messages.APPEAL.getString());

            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            e.setKickMessage(ChatUtil.color(message));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(AsyncPlayerChatEvent e) {
        Document data = Core.i().getMongoManager().getUserFromUUID(e.getPlayer().getUniqueId().toString());
        if (data != null) {
            Document mute = manager.getActivePunishment(data, PType.MUTE);
            if (mute != null) {
                e.setCancelled(true);
                String message = Messages.CHAT_DENY_MUTED.getString()
                        .replace("%reason%", mute.getString("reason"))
                        .replace("%duration%", mute.getLong("length") == -1 ? "Permanent" : manager.formatMillis(mute.getLong("time") + mute.getLong("length") - System.currentTimeMillis()))
                        .replace("%appeal%", Messages.APPEAL.getString());
                e.getPlayer().sendMessage(ChatUtil.color(message));
            }
        }
    }
}
