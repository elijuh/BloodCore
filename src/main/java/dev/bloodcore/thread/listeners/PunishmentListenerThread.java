package dev.bloodcore.thread.listeners;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import dev.bloodcore.Core;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import dev.bloodcore.punishments.PType;
import dev.bloodcore.thread.DisablingThread;
import dev.bloodcore.utils.ChatUtil;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Bukkit;

public class PunishmentListenerThread extends DisablingThread {

    @Override
    public void run() {
        MongoCursor<ChangeStreamDocument<Document>> iterator = Core.i().getMongoManager().getPunishmentsCollection().watch().iterator();
        while (iterator.hasNext() && isEnabled()) {
            ChangeStreamDocument<Document> bson = iterator.next();
            if (!isEnabled()) {
                break;
            }
            if (bson.getDocumentKey() == null) {
                continue;
            }
            Document data = Core.i().getMongoManager().getPunishmentsCollection().find(bson.getDocumentKey()).first();
            if (data == null) continue;
            if (data.get("_id") instanceof String) {
                continue;
            }
            if (bson.getOperationType() == OperationType.INSERT) {
                String duration = data.getLong("length") == -1 ? "Permanent" : Core.i().getPunishmentManager().formatMillis(data.getLong("length"));
                String reason = data.getString("reason");
                boolean silent = data.getBoolean("silent");
                PType type = PType.valueOf(data.getString("type"));
                Document target = Core.i().getMongoManager().getUserFromUUID(data.getString("uuid"));
                Document executor = data.getString("executor") == null ? null : Core.i().getMongoManager().getUserFromUUID(data.getString("executor"));
                String message;
                switch (type) {
                    case MUTE: message = (data.getLong("length") == -1 ? Messages.MUTE_MESSAGE : Messages.TEMPMUTE_MESSAGE).getString();
                        break;
                    case BAN: message = (data.getLong("length") == -1 ? Messages.BAN_MESSAGE : Messages.TEMPBAN_MESSAGE).getString();
                        break;
                    case IPBAN: message = Messages.IPBAN_MESSAGE.getString();
                        break;
                    case KICK: message = Messages.KICK_MESSAGE.getString();
                        break;
                    case WARN: message = Messages.WARN_MESSAGE.getString();
                        break;
                    default: continue;
                }
                message = message
                        .replace("%silent%", silent ? Messages.SILENT_PREFIX.getString() : "")
                        .replace("%target%", target.getString("display"))
                        .replace("%executor%", executor == null ? "&4Console" : executor.getString("display"));
                TextComponent component = new TextComponent(ChatUtil.color(message));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                        ChatUtil.color((type == PType.WARN || type == PType.KICK ? Messages.KICK_WARN_HOVER_INFO : Messages.PUNISH_HOVER_INFO).getString())
                        .replace("%reason%", reason)
                        .replace("%duration%", duration)
                )));
                for (User user : Core.i().getUsers()) {
                    if (!silent || user.getPlayer().hasPermission("blood.silent")) {
                        user.getPlayer().spigot().sendMessage(component);
                    }
                    if (user.uuid().equals(data.getString("uuid"))) {
                        if (type == PType.BAN || type == PType.IPBAN) {
                            String kick = Messages.BAN_SCREEN.getString()
                                    .replace("%bantype%", type == PType.BAN ? "banned" : "IP-banned")
                                    .replace("%duration%", duration)
                                    .replace("%reason%", reason)
                                    .replace("%appeal%", Messages.APPEAL.getString());
                            Bukkit.getScheduler().runTask(Core.i(), () -> user.getPlayer().kickPlayer(ChatUtil.color(kick)));
                        } else if (type == PType.KICK) {
                            String kick = Messages.KICK_SCREEN.getString()
                                    .replace("%reason%", reason);
                            Bukkit.getScheduler().runTask(Core.i(), () -> user.getPlayer().kickPlayer(ChatUtil.color(kick)));
                        } else {
                            String notify = type == PType.MUTE ? Messages.MUTED_NOTIFY.getString()
                                    .replace("%reason%", reason)
                                    .replace("%duration%", duration)
                                    .replace("%appeal%", Messages.APPEAL.getString()) :
                                    Messages.WARN_NOTIFY.getString()
                                    .replace("%reason%", reason);
                            user.msg(notify);
                        }
                    }
                }
                Bukkit.getConsoleSender().sendMessage(component.getText());

            } else if (bson.getOperationType() == OperationType.UPDATE) {
                PType type = PType.valueOf(data.getString("type"));
                Document target = Core.i().getMongoManager().getUserFromUUID(data.getString("uuid"));
                Document removed = data.get("removed", Document.class);
                if (removed == null) {
                    continue;
                }
                Document executor = removed.getString("by") == null ? null : Core.i().getMongoManager().getUserFromUUID(removed.getString("by"));
                boolean silent = removed.getBoolean("silent");
                String message;
                switch (type) {
                    case MUTE: message = Messages.UNMUTE_MESSAGE.getString();
                        break;
                    case BAN: message = Messages.UNBAN_MESSAGE.getString();
                        break;
                    case IPBAN: message = Messages.UNIPBAN_MESSAGE.getString();
                        break;
                    default: continue;
                }
                message = message
                        .replace("%silent%", silent ? Messages.SILENT_PREFIX.getString() : "")
                        .replace("%target%", target.getString("display"))
                        .replace("%executor%", executor == null ? Messages.CONSOLE_NAME.getString() : executor.getString("display"));
                TextComponent component = new TextComponent(ChatUtil.color(message));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                        ChatUtil.color(Messages.REMOVAL_HOVER_INFO.getString()
                        .replace("%reason%", removed.getString("reason"))
                ))));
                for (User user : Core.i().getUsers()) {
                    if (!silent || user.getPlayer().hasPermission("blood.silent")) {
                        user.getPlayer().spigot().sendMessage(component);
                    }
                }
                Bukkit.getConsoleSender().sendMessage(component.getText());
            }
        }
        System.out.println("breaking from PunishmentListenerThread #" + getId());
    }
}
