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
    private final List<String> userPermissions;
    private final Player player;
    private Rank rank;

    public User(Player player) {
        this.player = player;

        Document data = Core.i().getMongoManager().getUserFromUUID(player.getUniqueId().toString());
        if (data == null) {
            data = new Document("uuid", uuid())
                    .append("name", name())
                    .append("ip", "")
                    .append("display", "&7" + name())
                    .append("rank", "Default");

            Core.i().getMongoManager().getUsersCollection().insertOne(data);
        }

        rank = Core.i().getRankManager().getRank(data.getString("rank") == null ? "Default" : data.getString("rank"));
        if (rank == null) {
            rank = Core.i().getRankManager().getRank("Default");
            Core.i().getMongoManager().getUsersCollection().updateOne(Filters.eq("uuid", uuid()), new Document("$set", new Document("rank", rank.getId())));
        }

        userPermissions = data.getList("permissions", String.class) == null ? new ArrayList<>() : data.getList("permissions", String.class);

        ReflectionUtil.setPermissibleBase(player, new CustomPermissionBase(this));
    }

    public void unload() {
        PermissibleBase permissible = ReflectionUtil.getPermissibleBase(player);
        if (permissible instanceof CustomPermissionBase) {
            CustomPermissionBase permissionBase = (CustomPermissionBase) permissible;
            ReflectionUtil.setPermissibleBase(player, permissionBase.getPrevious());
        }
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
        userPermissions.clear();
        userPermissions.addAll(data.getList("permissions", String.class) == null ? new ArrayList<>() : data.getList("permissions", String.class));
        setRank(Core.i().getRankManager().getRank(data.getString("rank"), true));
    }
}
