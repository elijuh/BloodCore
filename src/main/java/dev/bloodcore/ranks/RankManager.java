package dev.bloodcore.ranks;

import com.google.common.collect.ImmutableList;
import com.mongodb.Block;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;

@Getter
public class RankManager {
    private final Map<String, Rank> ranks = new HashMap<>();

    public RankManager() {
        Rank defaultRank = new Rank("default", "Default", "&7", "&7", 0, ImmutableList.of(), ImmutableList.of());
        ranks.put(defaultRank.getId(), defaultRank);

        for (Document data : Core.i().getMongoManager().getRanksCollections().find()) {
            updateRank(data);
        }

        Bukkit.getScheduler().runTaskAsynchronously(Core.i(), () -> Core.i().getMongoManager().getRanksCollections().watch().forEach((Block<? super ChangeStreamDocument<Document>>) bson -> {
            try {
                if (bson.getDocumentKey() != null) {
                    if (bson.getOperationType() == OperationType.INSERT || bson.getOperationType() == OperationType.UPDATE || bson.getOperationType() == OperationType.REPLACE) {
                        Document data = Core.i().getMongoManager().getRanksCollections().find(bson.getDocumentKey()).first();
                        if (data != null) {
                            updateRank(data);
                        }
                    } else if (bson.getOperationType() == OperationType.DELETE) {
                        Rank rank = ranks.remove(bson.getDocumentKey().getString("_id").getValue());
                        if (rank != null) {
                            for (User user : Core.i().getUsers()) {
                                user.setRank(getRank("default"));
                            }
                            Core.i().rankLog("rank &c" + rank.getDisplay() + " &7was removed.");
                        }
                    }
                }
            } catch (LinkageError error) {
                //yes
            }
        }));
        Bukkit.getScheduler().runTaskAsynchronously(Core.i(), () -> Core.i().getMongoManager().getUsersCollection().watch().forEach((Block<? super ChangeStreamDocument<Document>>) bson -> {
            try {
                if (bson.getOperationType() == OperationType.UPDATE && bson.getDocumentKey() != null) {
                    Document data = Core.i().getMongoManager().getUsersCollection().find(bson.getDocumentKey()).first();
                    if (data != null) {
                        User user = Core.i().getUser(data.getString("name"));
                        if (user != null) {
                            user.refreshPermissions(data);
                        }
                    }
                }
            } catch (LinkageError error) {
                //yes
            }
        }));

        if (Core.i().getMongoManager().getRanksCollections().find(new Document("_id", "default")).first() == null) {
            Core.i().getMongoManager().getRanksCollections().insertOne(new Document("_id", "default").append("display", "Default").append("prefix", "&7").append("color", "&7")
                    .append("weight", 0).append("permissions", ImmutableList.of()).append("parents", ImmutableList.of()));
        }
    }

    private void updateRank(Document data) {
        String display = data.getString("display");
        String prefix = data.getString("prefix");
        String color = data.getString("color");
        int weight = data.getInteger("weight");
        List<String> permissions = data.getList("permissions", String.class) == null ? new ArrayList<>() : data.getList("permissions", String.class);
        List<String> parents = data.getList("parents", String.class) == null ? new ArrayList<>() : data.getList("parents", String.class);
        Rank rank = getRank(data.getString("_id"));
        if (rank != null) {
            List<String> updates = new ArrayList<>();
            if (!rank.getDisplay().equals(display)) {
                rank.setDisplay(display);
                updates.add("had display set to &c" + display + "&7.");
            }
            if (!rank.getPrefix().equals(prefix)) {
                rank.setPrefix(prefix);
                updates.add("had prefix set to &c" + prefix + "&7.");
            }
            if (!rank.getColor().equals(color)) {
                rank.setColor(color);
                updates.add("had color set to &c" + color + "&7.");
            }
            if (rank.getWeight() != weight) {
                rank.setWeight(weight);
                updates.add("had weight set to &c" + weight + "&7.");
            }
            for (String perm : permissions) {
                if (!rank.getPermissions().contains(perm)) {
                    rank.getPermissions().add(perm);
                    updates.add("added permission &c" + perm + "&7.");
                }
            }
            for (String perm : new ArrayList<>(rank.getPermissions())) {
                if (!permissions.contains(perm)) {
                    rank.getPermissions().remove(perm);
                    updates.add("removed permission &c" + perm + "&7.");
                }
            }
            for (String parent : parents) {
                if (!rank.getParents().contains(parent)) {
                    rank.getParents().add(parent);
                    updates.add("added parent &c" + parent + "&7.");
                }
            }
            for (String parent : new ArrayList<>(rank.getParents())) {
                if (!parents.contains(parent)) {
                    rank.getParents().remove(parent);
                    updates.add("removed parent &c" + parent + "&7.");
                }
            }
            for (String update : updates) {
                Core.i().rankLog("rank &c" + rank.getId() + "&7 " + update);
            }
        } else {
            rank = new Rank(data.getString("_id"), display, prefix, color, weight, permissions, parents);
            ranks.put(rank.getId(), rank);
            Core.i().rankLog("created new rank &c" + rank.getId());
        }
    }

    public Rank getRank(String name) {
        return getRank(name, false);
    }

    public Rank getRank(String name, boolean orDefault) {
        Rank rank = ranks.get(name.toLowerCase());
        if (rank == null && orDefault) {
            rank = ranks.get("default");
        }
        return rank;
    }
}
