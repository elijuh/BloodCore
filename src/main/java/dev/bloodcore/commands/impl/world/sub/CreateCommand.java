package dev.bloodcore.commands.impl.world.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.etc.Config;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.world.generators.VoidGenerator;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

public class CreateCommand extends SubCommand {
    public CreateCommand() {
        super("create", ImmutableList.of("new"), "blood.admin", "/world create <name>");

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if(sender instanceof ConsoleCommandSender) return;
        if (args.length == 1) {
            sender.sendMessage(ChatUtil.color("&cUsage: /world create <worldname>"));
            return;
        }
        String worldName = args[1];
        if (Core.i().getServer().getWorld(worldName) != null) {
            sender.sendMessage(ChatUtil.color("&cIt looks like that world already exists!"));
            return;
        }

        sender.sendMessage(ChatUtil.color(Config.CORE_PREFIX.getString() + "&eCreating " + worldName + "..."));
        Core.i().getWorldConfig().set(worldName + ".generator", "void");
        Core.i().getWorldConfig().set(worldName + ".autoload", true);
        Core.i().getWorldConfig().save();
        Core.i().getServer().createWorld(new WorldCreator(worldName).generator(new VoidGenerator()));
        sender.sendMessage(ChatUtil.color(Config.CORE_PREFIX.getString() + "&aCreated " + worldName));


    }
}
