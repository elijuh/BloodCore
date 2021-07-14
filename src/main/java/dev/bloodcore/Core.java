package dev.bloodcore;

import com.google.gson.Gson;
import com.sk89q.wepif.PermissionsProvider;
import dev.bloodcore.chat.ChatManager;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.essential.*;
import dev.bloodcore.commands.main.BloodCommand;
import dev.bloodcore.commands.punishments.*;
import dev.bloodcore.commands.rank.RankCommand;
import dev.bloodcore.commands.staff.*;
import dev.bloodcore.commands.user.UserCommand;
import dev.bloodcore.commands.world.WorldCommand;
import dev.bloodcore.db.MongoManager;
import dev.bloodcore.db.RedisManager;
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
import dev.bloodcore.staff.StaffListener;
import dev.bloodcore.staff.StaffManager;
import dev.bloodcore.thread.DisablingThread;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.HTTPUtil;
import dev.bloodcore.utils.ReflectionUtil;
import dev.bloodcore.world.WorldManager;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Core extends JavaPlugin implements PermissionsProvider {
    private final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm aa");
    private final Gson gson = new Gson();
    private final Set<User> users = new HashSet<>();
    private final Set<DisablingThread> threads = new HashSet<>();
    private static Core instance;

    private YamlStorage messages;
    private YamlStorage worldConfig;
    private YamlStorage staffConfig;

    private BloodExpansion expansion;

    private WorldManager worldManager;
    private MongoManager mongoManager;
    private RedisManager redisManager;
    private ChatManager chatManager;
    private RankManager rankManager;
    private PunishmentManager punishmentManager;
    private StaffManager staffManager;

    private HTTPUtil httpUtility;

    public void onEnable() {
        httpUtility = new HTTPUtil();
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&7&m------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&e&lLoading Configuration"));
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
        getConfig().options().copyDefaults(true);
        for (Config value : Config.values()) {
            getConfig().addDefault(value.getPath(), value.getDef());
        }
        saveConfig();
        Bukkit.getLogger().info("config.yml successfully loaded.");
        messages = new YamlStorage(new File(getDataFolder(), "messages.yml"));
        worldConfig = new YamlStorage(new File(getDataFolder(), "worlds.yml"));
        staffConfig = new YamlStorage(new File(getDataFolder(), "staffconfig.yml"));
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&7&m------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&6&lChecking License"));
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
        String license = getConfig().getString("license");
        if (!httpUtility.validate(license)) {
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&cInvalid license! plugin disabling..."));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&7&m------------------------------------"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aLicense is valid! Enabling core."));
        }
        instance = this;
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&7&m------------------------------------"));

        Bukkit.getScheduler().runTaskAsynchronously(this, ()-> {
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&d&lLoading Libraries"));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            loadClasses();
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aLibraries loaded successfully."));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
            dateFormat.setTimeZone(TimeZone.getDefault());

            messages.copyDefaults();
            for (Messages value : Messages.values()) {
                messages.addDefault(value.getPath(), value.getDef());
            }
            messages.save();

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&d&lLoading PlaceholderAPI Expansion"));
                Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
                expansion = new BloodExpansion();
                expansion.register();
                Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
                Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aExpansion loaded successfully."));
            }

            worldManager = new WorldManager();
            worldManager.loadWorlds();
            mongoManager = new MongoManager();
            redisManager = new RedisManager();
            chatManager = new ChatManager();
            punishmentManager = new PunishmentManager();
            staffManager = new StaffManager();

            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&d&lLoading Ranks"));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            rankManager = new RankManager();
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aRanks loaded successfully."));

            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&d&lLoading Commands"));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
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
            new StaffModeCommand();
            new VanishCommand();
            new TpaCommand();
            new SpawnCommand();
            new InvSeeCommand();
            new TpCommand();
            new SetSpawnCommand();
            new AltsCommand();
            new HistoryCommand();
            new StaffListCommand();
            new FreezeCommand();
            new ChatManagerCommand();
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aLoaded commands successfully."));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&d&lLoading Listeners"));
            Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
            Bukkit.getPluginManager().registerEvents(new PunishmentListener(), this);
            Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
            Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
            Bukkit.getPluginManager().registerEvents(new StaffListener(), this);
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aLoaded listeners successfully."));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&7&m------------------------------------"));

            for (Player p : Bukkit.getOnlinePlayers()) {
                users.add(new User(p));
            }

            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&7&m------------------------------------"));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&6&lBloodCore&r &7v" + getDescription().getVersion()));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&fPlugin has successfully been &a&lEnabled&f!"));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&7&m------------------------------------"));
        });
    }

    public void onDisable() {
        if (instance != null) {
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&7&m------------------------------------"));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&d&lUnloading Commands"));
            try {
                CommandMap map = (CommandMap) ReflectionUtil.getField(Bukkit.getServer().getClass(), "commandMap").get(Bukkit.getServer());
                ReflectionUtil.unregisterCommands(map, Command.getRegisteredCommands());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aCommands unloaded successfully."));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&d&lSaving Users"));
            for (User user : users) {
                user.unload();
            }
            users.clear();
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aUsers unloaded successfully."));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&d&lDisabling Listener Threads"));
            for (DisablingThread thread : threads) {
                thread.disable();
            }
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aThreads unloaded successfully."));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&d&lClosing Connections"));
            redisManager.shutdown();
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aConnections closed successfully."));
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));

            if (expansion != null && expansion.isRegistered()) {
                expansion.unregister();
            }
            Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&7&m------------------------------------"));
        }
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&7&m------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&6&lBloodCore&r &7v" + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color(" "));
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&fPlugin has successfully been &c&lDisabled&f!"));
        Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&7&m------------------------------------"));
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
            File file = new File(Core.i().getDataFolder(), "lib");
            if (!file.exists()) {
                file.mkdir();
            }
            for (String lib : new String[]{"mongo.jar", "jedis.jar", "slf4j-api.jar"}) {
                File library = new File(file.getPath(), lib);
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
        staffManager.reload();
    }

    public Document getDefaultSettings() {
        return new Document("messageSounds", true)
                .append("messageToggle", true);
    }

    @Override
    public boolean hasPermission(String name, String permission) {
        Player p = Bukkit.getPlayerExact(name);
        if (p != null) {
            return p.hasPermission(permission);
        }
        return false;
    }

    @Override
    public boolean hasPermission(String s, String name, String permission) {
        return hasPermission(name, permission);
    }

    @Override
    public boolean inGroup(String name, String group) {
        User user = getUser(name);
        if (user != null) {
            return user.getRank().getId().toLowerCase().equals(group) || user.getRank().getParents().stream().anyMatch(parent -> parent.equalsIgnoreCase(group));
        }
        return false;
    }

    @Override
    public String[] getGroups(String name) {
        User user = getUser(name);
        Set<String> groups = new HashSet<>();
        if (user != null) {
            groups.add(user.getRank().getId());
            groups.addAll(user.getRank().getParents());
        }
        return groups.toArray(new String[0]);
    }

    @Override
    public boolean hasPermission(OfflinePlayer offlinePlayer, String permission) {
        return hasPermission(offlinePlayer.getName(), permission);
    }

    @Override
    public boolean hasPermission(String s, OfflinePlayer offlinePlayer, String permission) {
        return hasPermission(offlinePlayer.getName(), permission);
    }

    @Override
    public boolean inGroup(OfflinePlayer offlinePlayer, String group) {
        return inGroup(offlinePlayer.getName(), group);
    }

    @Override
    public String[] getGroups(OfflinePlayer offlinePlayer) {
        return getGroups(offlinePlayer.getName());
    }
}
