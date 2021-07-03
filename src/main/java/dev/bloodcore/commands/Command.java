package dev.bloodcore.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.ReflectionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Command extends org.bukkit.command.Command {
    private static final List<String> registeredCommands = new ArrayList<>();
    String name, permission;
    List<String> aliases;

    public Command(String name) {
        this(name, Lists.newArrayList(), null);
    }

    public Command(String name, List<String> aliases, String permission) {
        super(name);
        setAliases(aliases);

        this.name = name;
        this.aliases = aliases;
        this.permission = permission;

        try {
            CommandMap map = (CommandMap) ReflectionUtil.getField(Bukkit.getServer().getClass(), "commandMap").get(Bukkit.getServer());
            ReflectionUtil.unregisterCommands(map, getName());
            ReflectionUtil.unregisterCommands(map, getAliases());
            map.register(getName(), "bloodcore", this);
            registeredCommands.add(name);
            registeredCommands.addAll(aliases);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            onConsole(sender, args);
            return true;
        }

        Player p = (Player) sender;
        if (permission != null && !p.hasPermission(permission)) {
            p.sendMessage(ChatUtil.color("&cNo permission."));
            return true;
        }
        User user = Core.i().getUser(p.getName());
        if (user == null) {
            p.sendMessage(ChatUtil.color("&cProfile not loaded, please relog and try again."));
            return true;
        }

        try {
            onExecute(user, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (sender instanceof Player) {
            if (getPermission() != null) {
                if (!sender.hasPermission(getPermission())) {
                    return ImmutableList.of();
                }
            }

            Player p = (Player) sender;
            User user = Core.i().getUser(p.getName());
            if (user == null) {
                return ImmutableList.of();
            }

            List<String> tabCompletion = onTabComplete(user, args);
            if (tabCompletion == null) {
                List<String> list = Lists.newArrayList();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (StringUtil.startsWithIgnoreCase(player.getName(), args[0]) && p.canSee(player)) {
                        list.add(player.getName());
                    }
                }
                return list;
            }
            return tabCompletion;

        } else {
            return ImmutableList.of();
        }
    }

    public void onConsole(CommandSender sender, String[] args) {
        sender.sendMessage(ChatUtil.color("&cYou must be a player to execute this command."));
    }

    public static List<String> getRegisteredCommands() {
        return registeredCommands;
    }

    public abstract List<String> onTabComplete(User user, String[] args);

    public abstract void onExecute(User user, String[] args);

}
