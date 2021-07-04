package dev.bloodcore;

import dev.bloodcore.chat.ChatManager;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.impl.essential.FlyCommand;
import dev.bloodcore.commands.impl.essential.ListCommand;
import dev.bloodcore.commands.impl.main.BloodCommand;
import dev.bloodcore.commands.impl.rank.RankCommand;
import dev.bloodcore.db.MongoManager;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.User;
import dev.bloodcore.etc.YamlStorage;
import dev.bloodcore.listeners.BukkitListener;
import dev.bloodcore.ranks.RankManager;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.ReflectionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Core extends JavaPlugin {
    private final Set<User> users = new HashSet<>();
    private static Core instance;

    private final YamlStorage messages = new YamlStorage(new File(getDataFolder(), "messages.yml"));

    private MongoManager mongoManager;
    private ChatManager chatManager;
    private RankManager rankManager;

    public void onLoad() {
        instance = this;
    }

    public void onEnable() {
        if (Bukkit.getServicesManager().getRegistration(Bukkit.getPluginManager().getPlugin("BloodLib").getClass()) != null) {
            Bukkit.getLogger().log(Level.SEVERE, "Plugin cannot be reloaded! shutting down..");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);

        getConfig().options().copyDefaults(true);
        getConfig().addDefault("mongo-db.connection-string", "");
        saveConfig();

        messages.copyDefaults();
        for (Config value : Config.values()) {
            messages.addDefault(value.getPath(), value.getDef());
        }
        messages.save();

        mongoManager = new MongoManager();
        chatManager = new ChatManager();
        rankManager = new RankManager();

        new BloodCommand();
        new RankCommand();
        new ListCommand();
        new FlyCommand();

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);

        for (Player p : Bukkit.getOnlinePlayers()) {
            users.add(new User(p));
        }
    }

    public void onDisable() {
        if (Bukkit.getServicesManager().getRegistration(Bukkit.getPluginManager().getPlugin("BloodLib").getClass()) == null) {
            Bukkit.getServicesManager().register(Bukkit.getPluginManager().getPlugin("BloodLib").getClass(), null, Bukkit.getPluginManager().getPlugin("BloodLib"), ServicePriority.Normal);
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
        }
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

    public void reload() {
        messages.reload();
        reloadConfig();
    }
}
