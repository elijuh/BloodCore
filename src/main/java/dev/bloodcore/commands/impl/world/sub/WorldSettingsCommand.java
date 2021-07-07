package dev.bloodcore.commands.impl.world.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WorldSettingsCommand extends SubCommand {
    public WorldSettingsCommand() {
        super("setting", ImmutableList.of("settings"), "blood.admin", "/world settings <setting> [value]");

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof ConsoleCommandSender) return;
        String worldName = ((Player) sender).getWorld().getName();

        if (args.length == 1) {
            sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
            for (String key : Core.i().getWorldConfig().getValues(worldName).getKeys(false)) {
                sender.sendMessage(ChatUtil.color("&6» &e" + key + "&f: &a" + Core.i().getWorldConfig().get(worldName + "." + key)));
            }
            sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
            return;
        }

        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("weather")) {
                boolean current = Core.i().getWorldConfig().getBoolean(worldName + ".disableWeather");
                Core.i().getWorldConfig().set(worldName + ".disableWeather", !current);
                Core.i().getWorldConfig().save();
                sender.sendMessage(ChatUtil.color(Messages.CORE_PREFIX.getString() + "&aUpdated setting: &6" + worldName + "&a will now have " + (!current ? "clear weather" : "all weather")));

            }
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
        }

    }
}
