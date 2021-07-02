package dev.bloodcore.etc;

import dev.bloodcore.Core;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.ranks.permission.CustomPermissionBase;
import dev.bloodcore.utils.ReflectionUtil;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.entity.Player;

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
                    .append("ip", ip())
                    .append("rank", "default");

            Core.i().getMongoManager().getUsersCollection().insertOne(data);
        }

        rank = Core.i().getRankManager().getRank(data.getString("rank") == null ? "default" : data.getString("rank"));

        userPermissions = data.getList("permissions", String.class) == null ? new ArrayList<>() : data.getList("permissions", String.class);

        CustomPermissionBase permissionBase = new CustomPermissionBase(this);
        Class<?> craftHumanEntityClass = ReflectionUtil.getCBClass("CraftHumanEntity");
        Object craftHumanEntity = craftHumanEntityClass.cast(player);
        try {
            ReflectionUtil.getField(craftHumanEntityClass, "perm").set(craftHumanEntity, permissionBase);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void unload() {

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

    public List<String> getUserPermissions() {
        List<String> permissions = new ArrayList<>(this.userPermissions);
        permissions.addAll(rank.getPermissions());
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
    }
}
