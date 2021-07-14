package dev.bloodcore.ranks;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import dev.bloodcore.Core;
import dev.bloodcore.thread.DisablingThread;
import dev.bloodcore.thread.listeners.RankListenerThread;
import dev.bloodcore.thread.listeners.UserListenerThread;
import dev.bloodcore.utils.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RankManager {
    private final Set<Rank> ranks = new HashSet<>();

    public RankManager() {
        Rank defaultRank = new Rank("Default", "&7", 0, new HashSet<>(), new HashSet<>());
        ranks.add(defaultRank);

        for (Document data : Core.i().getMongoManager().getRanksCollection().find()) {
            updateRank(data, false);
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aLoaded rank: &f" + data.getString("_id")));
        }

        DisablingThread t1 = new RankListenerThread(this);
        DisablingThread t2 = new UserListenerThread();

        Core.i().getThreads().add(t1);
        Core.i().getThreads().add(t2);
        t1.start();
        t2.start();

        if (Core.i().getMongoManager().getRanksCollection().find(new Document("_id", "Default")).first() == null) {
            Core.i().getMongoManager().getRanksCollection().insertOne(new Document("_id", "Default").append("prefix", "&7").append("color", "&7")
                    .append("priority", 0).append("permissions", ImmutableList.of()).append("parents", ImmutableList.of()));
        }
    }

    public Document getRankData(String name) {
        return Core.i().getMongoManager().getRanksCollection().find(new Document("_id", name)).collation(Collation.builder().locale("en").collationStrength(CollationStrength.PRIMARY).build()).first();
    }

    public void updateRank(Document data, boolean log) {
        String prefix = data.getString("prefix");
        int priority = data.getInteger("priority");
        List<String> permissions = data.getList("permissions", String.class) == null ? new ArrayList<>() : data.getList("permissions", String.class);
        List<String> parents = data.getList("parents", String.class) == null ? new ArrayList<>() : data.getList("parents", String.class);
        Rank rank = getRank(data.getString("_id"));
        if (rank != null) {
            List<String> updates = new ArrayList<>();
            if (!rank.getPrefix().equals(prefix)) {
                rank.setPrefix(prefix);
                updates.add("had prefix set to &6" + prefix + "Player&e.");
            }
            if (rank.getPriority() != priority) {
                rank.setPriority(priority);
                updates.add("had priority set to &6" + priority + "&e.");
            }
            for (String perm : permissions) {
                if (!rank.getPermissions().contains(perm)) {
                    rank.getPermissions().add(perm);
                    updates.add("added permission &6" + perm + "&e.");
                }
            }
            for (String perm : new HashSet<>(rank.getPermissions())) {
                if (!permissions.contains(perm)) {
                    rank.getPermissions().remove(perm);
                    updates.add("removed permission &6" + perm + "&e.");
                }
            }
            for (String parent : parents) {
                if (!rank.getParents().contains(parent)) {
                    rank.getParents().add(parent);
                    updates.add("added parent &6" + parent + "&e.");
                }
            }
            for (String parent : new HashSet<>(rank.getParents())) {
                if (!parents.contains(parent)) {
                    rank.getParents().remove(parent);
                    updates.add("removed parent &6" + parent + "&e.");
                }
            }
            if (log) {
                for (String update : updates) {
                    Core.i().rankLog("rank &6" + rank.getId() + " &e" + update);
                }
            }
        } else {
            rank = new Rank(data.getString("_id"), prefix, priority, new HashSet<>(permissions), new HashSet<>(parents));
            Rank current = getRank(rank.getId());
            if (current != null) {
                ranks.remove(current);
            }
            ranks.add(rank);
            if (log) {
                Core.i().rankLog("created new rank &6" + rank.getId());
            }
        }
    }

    public Rank getRank(String name) {
        return getRank(name, false);
    }

    public Rank getRank(String name, boolean orDefault) {
        Rank rank = ranks.stream().filter(r -> r.getId().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (name.equals("default")) {
            return rank;
        } else if (rank == null && orDefault) {
            rank = getRank("default");
        }
        return rank;
    }
}
