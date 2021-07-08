package dev.bloodcore.commands.world.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.world.GeneratorType;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

public class WorldCreateCommand extends SubCommand {
    public WorldCreateCommand() {
        super("create", ImmutableList.of("new"), "blood.admin", "/world create <name> [generator]");

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof ConsoleCommandSender) return;
        if (args.length == 1) {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
            return;
        }
        String worldName = args[1];


        if (Core.i().getServer().getWorld(worldName) != null) {
            sender.sendMessage(ChatUtil.color("&cIt looks like that world already exists!"));
            return;
        }

        GeneratorType generatorType = GeneratorType.NORMAL;
        if (args.length > 2) {
            try {
                generatorType = GeneratorType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatUtil.color("&cThat generator type does not exist! &7(" + args[2] + ")"));
            }
        }
        sender.sendMessage(ChatUtil.color(Messages.CORE_PREFIX.getString() + "&eCreating " + worldName + "..."));
        Core.i().getWorldConfig().set(worldName + ".generator", generatorType.name());
        Core.i().getWorldConfig().set(worldName + ".autoload", true);
        Core.i().getWorldConfig().save();

        Core.i().getServer().createWorld(new WorldCreator(worldName).generator(Core.i().getWorldManager().getGenerator(worldName)));
        sender.sendMessage(ChatUtil.color(Messages.CORE_PREFIX.getString() + "&aCreated " + worldName + " &7Generator: " + generatorType.getName()));


    }
}
