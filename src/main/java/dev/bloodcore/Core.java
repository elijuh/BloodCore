package dev.bloodcore;

import dev.bloodcore.chat.ChatManager;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.impl.essential.FeedCommand;
import dev.bloodcore.commands.impl.essential.FlyCommand;
import dev.bloodcore.commands.impl.essential.HealCommand;
import dev.bloodcore.commands.impl.essential.ListCommand;
import dev.bloodcore.commands.impl.main.BloodCommand;
import dev.bloodcore.commands.impl.punishments.BanCommand;
import dev.bloodcore.commands.impl.punishments.UnbanCommand;
import dev.bloodcore.commands.impl.rank.RankCommand;
import dev.bloodcore.commands.impl.world.WorldCommand;
import dev.bloodcore.db.MongoManager;
import dev.bloodcore.disguise.DisguiseCommand;
import dev.bloodcore.disguise.DisguiseListener;
import dev.bloodcore.disguise.DisguiseManager;
import dev.bloodcore.disguise.util.HTTPUtility;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import dev.bloodcore.etc.YamlStorage;
import dev.bloodcore.listeners.BukkitListener;
import dev.bloodcore.listeners.PunishmentListener;
import dev.bloodcore.punishments.PunishmentManager;
import dev.bloodcore.ranks.RankManager;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.ReflectionUtil;
import dev.bloodcore.world.WorldListener;
import dev.bloodcore.world.WorldManager;
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
    private final YamlStorage worldConfig = new YamlStorage(new File(getDataFolder(), "worlds.yml"));

    private WorldManager worldManager;
    private MongoManager mongoManager;
    private ChatManager chatManager;
    private RankManager rankManager;
    private DisguiseManager disguiseManager;
    private PunishmentManager punishmentManager;

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
        for (Config value : Config.values()) {
            getConfig().addDefault(value.getPath(), value.getDef());
        }
        saveConfig();

        messages.copyDefaults();
        for (Messages value : Messages.values()) {
            messages.addDefault(value.getPath(), value.getDef());
        }
        messages.save();

        worldManager = new WorldManager();

        worldManager.loadWorlds();

        mongoManager = new MongoManager();
        chatManager = new ChatManager();
        rankManager = new RankManager();
        punishmentManager = new PunishmentManager();
        disguiseManager = new DisguiseManager(this, new HTTPUtility(this));

        new BloodCommand();
        new RankCommand();
        new ListCommand();
        new FlyCommand();
        new WorldCommand();
        new FeedCommand();
        new HealCommand();

        new BanCommand();
        new UnbanCommand();
        new DisguiseCommand();

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PunishmentListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DisguiseListener(disguiseManager), this);


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
        reloadConfig();
        getConfig().options().copyDefaults(true);
        for (Config value : Config.values()) {
            getConfig().addDefault(value.getPath(), value.getDef());
        }
        saveConfig();

        messages.reload();
        messages.copyDefaults();
        for (Messages value : Messages.values()) {
            messages.addDefault(value.getPath(), value.getDef());
        }
        messages.save();

        worldConfig.reload();
        chatManager.reload();
    }
}
