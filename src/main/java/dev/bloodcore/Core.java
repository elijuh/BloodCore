package dev.bloodcore;

import dev.bloodcore.chat.ChatManager;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.essential.*;
import dev.bloodcore.commands.main.BloodCommand;
import dev.bloodcore.commands.punishments.*;
import dev.bloodcore.commands.rank.RankCommand;
import dev.bloodcore.commands.user.UserCommand;
import dev.bloodcore.commands.world.WorldCommand;
import dev.bloodcore.db.MongoManager;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import dev.bloodcore.etc.YamlStorage;
import dev.bloodcore.listeners.BukkitListener;
import dev.bloodcore.listeners.ChatListener;
import dev.bloodcore.listeners.PunishmentListener;
import dev.bloodcore.listeners.WorldListener;
import dev.bloodcore.punishments.PunishmentManager;
import dev.bloodcore.ranks.RankManager;
import dev.bloodcore.thread.DisablingThread;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.HTTPUtil;
import dev.bloodcore.utils.ReflectionUtil;
import dev.bloodcore.world.WorldManager;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Core extends JavaPlugin {
    private final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm aa");
    private final Set<User> users = new HashSet<>();
    private final Set<DisablingThread> threads = new HashSet<>();
    private static Core instance;

    private final YamlStorage messages = new YamlStorage(new File(getDataFolder(), "messages.yml"));
    private final YamlStorage worldConfig = new YamlStorage(new File(getDataFolder(), "worlds.yml"));

    private BloodExpansion expansion;

    private WorldManager worldManager;
    private MongoManager mongoManager;
    private ChatManager chatManager;
    private RankManager rankManager;
    private PunishmentManager punishmentManager;

    private HTTPUtil httpUtility;

    public void onEnable() {
        httpUtility = new HTTPUtil();
        getConfig().options().copyDefaults(true);
        for (Config value : Config.values()) {
            getConfig().addDefault(value.getPath(), value.getDef());
        }
        saveConfig();
        String license = getConfig().getString("license");
        if (!httpUtility.validate(license)) {
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&cInvalid license! plugin disabling..."));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }else{
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aLicense is valid! Enabling core."));

        }
        instance = this;

        Bukkit.getScheduler().runTaskAsynchronously(this, ()-> {
            loadClasses();
            Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
            dateFormat.setTimeZone(TimeZone.getDefault());

            messages.copyDefaults();
            for (Messages value : Messages.values()) {
                messages.addDefault(value.getPath(), value.getDef());
            }
            messages.save();

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                expansion = new BloodExpansion();
                expansion.register();
            }

            worldManager = new WorldManager();

            worldManager.loadWorlds();

            mongoManager = new MongoManager();
            chatManager = new ChatManager();
            rankManager = new RankManager();
            punishmentManager = new PunishmentManager();

            new BloodCommand();
            new RankCommand();
            new ListCommand();
            new FlyCommand();
            new WorldCommand();
            new FeedCommand();
            new HealCommand();
            new PingCommand();
            new UserCommand();
            new DisguiseCommand();
            new GamemodeCommand();
            new RulesCommand();
            new MessageCommand();
            new SudoCommand();
            new BanCommand();
            new UnbanCommand();
            new TempbanCommand();
            new MuteCommand();
            new UnmuteCommand();
            new TempmuteCommand();
            new IPBanCommand();
            new IPUnbanCommand();
            new KickCommand();
            new WarnCommand();

            Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
            Bukkit.getPluginManager().registerEvents(new PunishmentListener(), this);
            Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
            Bukkit.getPluginManager().registerEvents(new ChatListener(), this);



            for (Player p : Bukkit.getOnlinePlayers()) {
                users.add(new User(p));
            }
        });
    }

    public void onDisable() {
        if (instance != null) {
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
            for (DisablingThread thread : threads) {
                thread.disable();
            }

            if (expansion != null && expansion.isRegistered()) {
                expansion.unregister();
            }
        }
    }

    public User getUser(Player player) {
        for (User user : users) {
            if (user.getPlayer() == player) {
                return user;
            }
        }
        return null;
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

    public void loadClasses() {
        File dir = new File(getDataFolder(), "lib");
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            File file = new File(Core.i().getDataFolder() + "/lib");
            if (!file.exists()) {
                file.mkdir();
            }
            for (String lib : new String[]{"mongo.jar"}) {
                File library = new File(file.getPath() + "/" + lib);
                if(!library.exists()) {
                    System.out.println("Downloading " + lib + "...");
                    FileUtils.copyURLToFile(new URL("https://hardstyles.me/bloodcore/" + lib), library);
                    System.out.println("Download successful: " + lib);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        File[] files = dir.listFiles();
        if (files == null) return;
        URLClassLoader loader = (URLClassLoader) getClassLoader();
        Method addUrl;
        try {
            addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrl.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        for (File file : files) {
            try {
                URL url = file.toURI().toURL();
                addUrl.invoke(loader, url);
                System.out.println("Loaded library: " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public Document getDefaultSettings() {
        return new Document("messageSounds", true)
                .append("messageToggle", true);
    }
}
