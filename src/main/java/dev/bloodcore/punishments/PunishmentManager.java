package dev.bloodcore.punishments;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.db.MongoManager;
import dev.bloodcore.thread.DisablingThread;
import dev.bloodcore.thread.listeners.PunishmentListenerThread;
import org.bson.Document;
import org.bson.conversions.Bson;

public class PunishmentManager {
    private final MongoManager manager = Core.i().getMongoManager();

    public PunishmentManager() {
        DisablingThread t = new PunishmentListenerThread();
        Core.i().getThreads().add(t);
        t.start();
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
            Bson filter = Filters.and(Filters.eq("uuid", user.getString("uuid")), Filters.eq("ip", user.getString("ip")), Filters.not(Filters.exists("removed")));
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
