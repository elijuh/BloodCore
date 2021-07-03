package dev.bloodcore.etc;

import dev.bloodcore.Core;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.ranks.permission.CustomPermissionBase;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.ReflectionUtil;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class User {
    private final Map<String, Object> data = new HashMap<>();
    private final List<String> userPermissions;
    private final Player player;
    private Rank rank;

    public User(Player player) {
        this.player = player;

        Document data = Core.i().getMongoManager().getData(player.getUniqueId());
        if (data == null) {
            data = new Document("uuid", uuid())
                    .append("name", name())
                    .append("ip", "")
                    .append("rank", "default");

            Core.i().getMongoManager().getUsersCollection().insertOne(data);
        }

        rank = Core.i().getRankManager().getRank(data.getString("rank") == null ? "default" : data.getString("rank"));

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

    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>(this.userPermissions);
        if (rank != null) {
            permissions.addAll(rank.getPermissions());
        }
        return permissions;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    public void refreshPermissions(Document data) {
        userPermissions.clear();
        userPermissions.addAll(data.getList("permissions", String.class) == null ? new ArrayList<>() : data.getList("permissions", String.class));
        rank = Core.i().getRankManager().getRank(data.getString("rank"), true);
        Bukkit.getLogger().info("set " + name() + "'s rank to " + rank.getDisplay());
    }
}
