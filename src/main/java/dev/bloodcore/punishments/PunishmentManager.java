package dev.bloodcore.punishments;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.db.MongoManager;
import dev.bloodcore.thread.DisablingThread;
import dev.bloodcore.thread.listeners.PunishmentListenerThread;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;

public class PunishmentManager {
    private final MongoManager manager = Core.i().getMongoManager();

    public PunishmentManager() {
        DisablingThread t = new PunishmentListenerThread();
        Core.i().getThreads().add(t);
        t.start();
    }

    public Document getIPBan(String ip) {
        Bson filter = Filters.and(Filters.eq("ip", ip), Filters.eq("type", PType.IPBAN.name()), Filters.not(Filters.exists("removed")));
        return manager.getPunishmentsCollection().find(filter).first();
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

    public FindIterable<Document> getPunishments(String uuid, PType type) {
        return manager.getPunishmentsCollection().find(new Document("uuid", uuid).append("type", type.name()));
    }

    public FindIterable<Document> getAccounts(String ip) {
        return manager.getUsersCollection().find(new Document("ip", ip));
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

    public long parseDate(String input) throws NumberFormatException {
        if (input.length() < 2) {
            throw new NumberFormatException();
        }
        long amount = Integer.parseInt(input.substring(0, input.length() - 1));
        switch (input.toCharArray()[input.length() - 1]) {
            case 'y': {
                return amount * 31536000000L;
            }
            case 'M': {
                return amount * 2592000000L;
            }
            case 'w': {
                return amount * 604800000L;
            }
            case 'd': {
                return amount * 86400000L;
            }
            case 'h': {
                return amount * 3600000L;
            }
            case 'm': {
                return amount * 60000L;
            }
            case 's': {
                return amount * 1000L;
            }
            default: {
                throw new NumberFormatException();
            }
        }
    }
}
