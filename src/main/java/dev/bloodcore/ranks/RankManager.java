package dev.bloodcore.ranks;

import com.mongodb.Block;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankManager {
    private final Map<String, Rank> ranks = new HashMap<>();

    public RankManager() {
        Rank defaultRank = new Rank("Default", "", "", 0, new ArrayList<>());
        ranks.put("default", defaultRank);
        for (Document data : Core.i().getMongoManager().getRanksCollections().find()) {
            updateRank(data);
        }

        Bukkit.getScheduler().runTaskAsynchronously(Core.i(), ()-> Core.i().getMongoManager().getRanksCollections().watch().forEach((Block<? super ChangeStreamDocument<Document>>) bson -> {
            if (bson.getOperationType() == OperationType.INSERT || bson.getOperationType() == OperationType.UPDATE) {
                Document data = bson.getFullDocument();
                if (data != null) {
                    updateRank(data);
                }
            }
        }));
        Bukkit.getScheduler().runTaskAsynchronously(Core.i(), ()-> Core.i().getMongoManager().getUsersCollection().watch().forEach((Block<? super ChangeStreamDocument<Document>>) bson -> {
            if (bson.getOperationType() == OperationType.UPDATE) {
                Document data = bson.getFullDocument();
                if (data != null) {
                    User user = Core.i().getUser(data.getString("name"));
                    if (user != null) {
                        user.refreshPermissions(data);
                    }
                }
            }
        }));
    }

    private void updateRank(Document data) {
        String name = data.getString("_id");
        String prefix = data.getString("prefix");
        String color = data.getString("color");
        int weight = data.getInteger("weight");
        List<String> permissions = data.getList("permissions", String.class) == null ? new ArrayList<>() : data.getList("permissions", String.class);
        Rank rank = getRank(name);
        if (rank != null) {
            rank.setPrefix(prefix);
            rank.setColor(color);
            rank.setWeight(weight);
            rank.setPermissions(permissions);
        } else {
            rank = new Rank(name, prefix, color, weight, permissions);
            ranks.put(name.toLowerCase(), rank);
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
