package dev.bloodcore;

import dev.bloodcore.chat.ChatManager;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.impl.rank.RankCommand;
import dev.bloodcore.db.MongoManager;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.User;
import dev.bloodcore.listeners.BukkitListener;
import dev.bloodcore.ranks.RankManager;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.ReflectionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Core extends JavaPlugin {
    private final Set<User> users = new HashSet<>();
    private static Core instance;

    private MongoManager mongoManager;
    private ChatManager chatManager;
    private RankManager rankManager;

    public void onLoad() {
        instance = this;
    }

    public void onEnable() {
        getConfig().options().copyDefaults(true);
        getConfig().addDefault("mongo-db.connection-string", "");
        for (Config value : Config.values()) {
            getConfig().addDefault(value.getPath(), value.getDef());
        }
        saveConfig();

        mongoManager = new MongoManager();
        chatManager = new ChatManager();
        rankManager = new RankManager();

        new RankCommand();

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);

        for (Player p : Bukkit.getOnlinePlayers()) {
            users.add(new User(p));
        }
    }

    public void onDisable() {
        try {
            CommandMap map = (CommandMap) ReflectionUtil.getField(Bukkit.getServer().getClass(), "commandMap").get(Bukkit.getServer());
            ReflectionUtil.unregisterCommands(map, Command.getRegisteredCommands());
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (User user : users) {
            user.unload();
        }
        users.clear();
        Bukkit.shutdown();
    }

    public User getUser(String name) {
        for (User user : users) {
            if (user.name().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    public static Core i() {
        return instance;
    }

    public void rankLog(String log) {
        for (User user : users) {
            if (user.getPlayer().hasPermission("blood.command.rank")) {
                user.msg("&6&lRank Log &8» &e" + log);
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&6&lRank Log &8» &e" + log));
    }
}
