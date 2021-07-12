package dev.bloodcore.etc;

import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.ranks.permission.CustomPermissionBase;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.ReflectionUtil;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import java.util.*;

@Getter
@Setter
public class User {
    private final Map<String, Object> data = new HashMap<>();
    private final List<String> userPermissions, ignoreList;
    private final Document settings;
    private final Player player;
    private Rank rank;
    private String tag;
    private GUI currentGUI;
    private boolean vanished, staffMode;

    public User(Player player) {
        this.player = player;

        Document data = Core.i().getMongoManager().getUserFromUUID(player.getUniqueId().toString());
        if (data == null) {
            data = new Document("uuid", uuid())
                    .append("name", name())
                    .append("ip", "")
                    .append("display", "&7" + name())
                    .append("rank", "Default")
                    .append("permissions", new ArrayList<>())
                    .append("ignoreList", new ArrayList<>())
                    .append("settings", Core.i().getDefaultSettings())
                    .append("data", new Document("firstJoin", System.currentTimeMillis()).append("lastJoin", System.currentTimeMillis()));

            Core.i().getMongoManager().getUsersCollection().insertOne(data);
        }

        rank = Core.i().getRankManager().getRank(data.getString("rank") == null ? "Default" : data.getString("rank"));
        if (rank == null) {
            rank = Core.i().getRankManager().getRank("Default");
            Core.i().getMongoManager().getUsersCollection().updateOne(Filters.eq("uuid", uuid()), new Document("$set", new Document("rank", rank.getId())));
        }

        userPermissions = data.getList("permissions", String.class) == null ? new ArrayList<>() : data.getList("permissions", String.class);
        ignoreList = data.getList("ignoreList", String.class) == null ? new ArrayList<>() : data.getList("ignoreList", String.class);
        settings = data.get("settings", Document.class);

        ReflectionUtil.setPermissibleBase(player, new CustomPermissionBase(this));
    }

    public void unload() {
        PermissibleBase permissible = ReflectionUtil.getPermissibleBase(player);
        if (permissible instanceof CustomPermissionBase) {
            CustomPermissionBase permissionBase = (CustomPermissionBase) permissible;
            ReflectionUtil.setPermissibleBase(player, permissionBase.getPrevious());
        }

        Disguise disguise = get("disguise");
        if (disguise != null) {
            disguise.remove();
        }

        if (staffMode) {
            Core.i().getStaffManager().leaveStaffMode(this);
        }

        Document update = new Document("settings", settings);
        Core.i().getMongoManager().getUsersCollection().updateOne(Filters.eq("uuid", uuid()), new Document("$set", update));
    }

    public void msg(String s) {
        player.sendMessage(ChatUtil.color(s));
    }

    public void sound(Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void actionBar(String s) {
        ReflectionUtil.sendActionBar(player, ChatUtil.color(s));
    }

    public String uuid() {
        return player.getUniqueId().toString();
    }

    public String name() {
        return player.getName();
    }

    public String ip() {
        return player.getAddress().getAddress().getHostAddress();
    }

    public Set<String> getPermissions() {
        Set<String> permissions = new HashSet<>(this.userPermissions);
        if (rank != null) {
            permissions.addAll(rank.getPermissions());
            for (String parent : rank.gettAllParents()) {
                Rank parentRank = Core.i().getRankManager().getRank(parent);
                if (parentRank != null) {
                    permissions.addAll(parentRank.getPermissions());
                }
            }
        }
        permissions.addAll(Core.i().getRankManager().getRank("default").getPermissions());
        return permissions;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    public void refreshPermissions(Document data) {
        List<String> permissions = data.getList("permissions", String.class) == null ? new ArrayList<>() : data.getList("permissions", String.class);
        for (String perm : permissions) {
            if (!userPermissions.contains(perm)) {
                userPermissions.add(perm);
                Core.i().rankLog("&6" + name() + " &ehad permission added &6" + perm + "&e.");
            }
        }
        for (String perm : new ArrayList<>(userPermissions)) {
            if (!permissions.contains(perm)) {
                userPermissions.remove(perm);
                Core.i().rankLog("&6" + name() + " &ehad permission removed &6" + perm + "&e.");
            }
        }
        setRank(Core.i().getRankManager().getRank(data.getString("rank"), true));
    }
}
