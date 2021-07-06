package dev.bloodcore.punishments;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import dev.bloodcore.Core;
import dev.bloodcore.db.MongoManager;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;

import java.util.function.Consumer;

public class PunishmentManager {
    private final MongoManager manager = Core.i().getMongoManager();

    public PunishmentManager() {
        new Thread(() -> Core.i().getMongoManager().getPunishmentsCollection().watch().forEach((Consumer<? super ChangeStreamDocument<Document>>) bson -> {
            if (bson.getDocumentKey() == null) {
                return;
            }
            Document data = Core.i().getMongoManager().getPunishmentsCollection().find(bson.getDocumentKey()).first();
            if (data == null) return;
            if (data.get("_id") instanceof String) {
                return;
            }
            if (bson.getOperationType() == OperationType.INSERT) {
                boolean silent = data.getBoolean("silent");
                PType type = PType.valueOf(data.getString("type"));
                Document target = Core.i().getMongoManager().getUserFromUUID(data.getString("uuid"));
                Document executor = data.getString("executor") == null ? null : Core.i().getMongoManager().getUserFromUUID(data.getString("executor"));
                String message;
                switch (type) {
                    case MUTE: message = Messages.MUTE_MESSAGE.getString();
                    break;
                    case BAN: message = Messages.BAN_MESSAGE.getString();
                    break;
                    case IPBAN: message = Messages.IPBAN_MESSAGE.getString();
                    break;
                    case KICK: message = Messages.KICK_MESSAGE.getString();
                    break;
                    default: return;
                }
                message = message
                        .replace("%silent%", silent ? Messages.SILENT_PREFIX.getString() : "")
                        .replace("%target%", target.getString("display"))
                        .replace("%executor%", executor == null ? "&4Console" : executor.getString("display"));
                TextComponent component = new TextComponent(ChatUtil.color(message));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatUtil.color("&7Reason: &f" + data.getString("reason")))));
                for (User user : Core.i().getUsers()) {
                    if (!silent || user.getPlayer().hasPermission("blood.silent")) {
                        user.getPlayer().spigot().sendMessage(component);
                    }
                    if (user.uuid().equals(data.getString("uuid"))) {
                        String kick = Messages.BAN_SCREEN.getString()
                                .replace("%bantype%", type == PType.BAN ? "banned" : "IP-banned")
                                .replace("%length%", data.getLong("length") == -1 ? "Permanent" : formatMillis(data.getLong("time") + data.getLong("length") - System.currentTimeMillis()))
                                .replace("%reason%", data.getString("reason"))
                                .replace("%appeal%", Messages.APPEAL.getString());
                        Bukkit.getScheduler().runTask(Core.i(), ()-> user.getPlayer().kickPlayer(ChatUtil.color(kick)));
                    }
                }
                Bukkit.getConsoleSender().sendMessage(component.getText());
            } else if (bson.getOperationType() == OperationType.UPDATE) {
                PType type = PType.valueOf(data.getString("type"));
                Document target = Core.i().getMongoManager().getUserFromUUID(data.getString("uuid"));
                Document removed = data.get("removed", Document.class);
                if (removed == null) {
                    return;
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
                    default: return;
                }
                message = message
                        .replace("%silent%", silent ? Messages.SILENT_PREFIX.getString() : "")
                        .replace("%target%", target.getString("display"))
                        .replace("%executor%", executor == null ? Messages.CONSOLE_NAME.getString() : executor.getString("display"));
                TextComponent component = new TextComponent(ChatUtil.color(message));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatUtil.color("&7Reason: &f" + removed.getString("reason")))));
                for (User user : Core.i().getUsers()) {
                    if (!silent || user.getPlayer().hasPermission("blood.silent")) {
                        user.getPlayer().spigot().sendMessage(component);
                    }
                }
                Bukkit.getConsoleSender().sendMessage(component.getText());
            }
        })).start();
    }

    public boolean isIPBanned(String ip) {
        if (ip == null) {
            return false;
        }

        for (Document data : manager.getPunishmentsCollection().find(new Document("ip", ip))) {
            if (!data.containsKey("removed")) {
                return true;
            }
        }
        return false;
    }

    public Document getActivePunishment(Document user, PType type) {
        Bson filter = Filters.and(Filters.eq("uuid", user.getString("uuid")), Filters.eq("type", type.name()), Filters.not(Filters.exists("removed")));
        for (Document data : manager.getPunishmentsCollection().find(filter)) {
            long time = data.getLong("time");
            long length = data.getLong("length");

            if (length == -1 || time + length > System.currentTimeMillis()) {
                return data;
            }
        }
        return null;
    }

    public void remove(Document user, PType type, Document info) {
        Document update = new Document("removed", info);
        Bson filter = Filters.and(new Document("uuid", user.getString("uuid")).append("type", type.name()), Filters.not(Filters.exists("removed")));
        manager.getPunishmentsCollection().updateMany(filter, new Document("$set", update));
    }

    public MongoCursor<Document> getPunishments(String uuid, PType type) {
        return manager.getPunishmentsCollection().find(new Document("uuid", uuid).append("type", type.name())).iterator();

    }

    public Document getActiveBan(Document user) {
        if (isIPBanned(user.getString("ip"))) {
            Bson filter = Filters.and(Filters.eq("ip", user.getString("ip")), Filters.not(Filters.exists("removed")));
            return manager.getPunishmentsCollection().find(filter).first();
        } else if (getActivePunishment(user, PType.BAN) != null || getActivePunishment(user, PType.IPBAN) != null) {
            Bson filter = Filters.and(Filters.eq("uuid", user.getString("uuid")), Filters.not(Filters.exists("removed")));
            for (Document data : manager.getPunishmentsCollection().find(filter)) {
                long time = data.getLong("time");
                long length = data.getLong("length");
                if (length == -1 || time - length > System.currentTimeMillis()) {
                    return data;
                }
            }
        }
        return null;
    }

    public MongoCursor<Document> getAccounts(String ip) {
        return manager.getUsersCollection().find(new Document("ip", ip)).iterator();
    }

    public void deletePunishment(int id) {
        manager.getPunishmentsCollection().deleteOne(Filters.eq("_id", id));
    }

    public int nextId() {
        manager.getPunishmentsCollection().updateOne(Filters.eq("_id", "last_id"), new Document("$inc", new Document("value", 1)));
        Document data = manager.getPunishmentsCollection().find(Filters.eq("_id", "last_id")).first();
        if (data != null) {
            return data.getInteger("value");
        }
        return -1;
    }

    public String formatMillis(long millis) {
        long seconds = millis / 1000;
        long days = 0, hours = 0, minutes = 0;

        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }

        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }

        while (hours >= 24) {
            hours -= 24;
            days++;
        }

        String format = pluralize(days, "Day", ", ")
                + pluralize(hours, "Hour", ", ")
                + pluralize(minutes, "Minute", ", ")
                + pluralize(seconds, "Second", ", ");
        return format.isEmpty() ? "" : format.substring(0, format.length() - 2);
    }

    private String pluralize(long amount, String name, String... extra) {
        StringBuilder format = new StringBuilder();
        if (amount == 1) {
            format.append(amount).append(" ").append(name);
        } else {
            format.append(amount > 0 ? amount + " " + name + "s" : "");
        }
        if (!format.toString().isEmpty()) {
            for (String s : extra) {
                format.append(s);
            }
        }
        return format.toString();
    }
}
