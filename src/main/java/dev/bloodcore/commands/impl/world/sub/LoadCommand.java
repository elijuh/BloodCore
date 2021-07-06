package dev.bloodcore.commands.impl.world.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.List;

public class LoadCommand extends SubCommand {
    public LoadCommand() {
        super("load", ImmutableList.of(), "blood.admin", "/world load <name>");

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(ChatUtil.color("&cUsage: /world load <worldname>"));
            return;
        }
        String worldName = args[1];
        if (Core.i().getServer().getWorld(worldName) != null) {
            sender.sendMessage(ChatUtil.color("&cIt looks like that world is already loaded!"));
            return;
        }

        sender.sendMessage(ChatUtil.color(Messages.CORE_PREFIX.getString() + "&eCreating " + worldName + "..."));

        File[] container = Core.i().getServer().getWorldContainer().listFiles();
        if (container == null) {
            return;
        }

        boolean worldExists = false;
        for (File file : container) {
            if (file.getName().equalsIgnoreCase(worldName)) {
                worldExists = true;
                break;
            }
        }
        if (worldExists) {
            ChunkGenerator generator = Core.i().getWorldManager().getGenerator(worldName);
            WorldCreator worldCreator = new WorldCreator(worldName);
            if (generator != null) {
                worldCreator.generator(generator);
            }
            Core.i().getServer().createWorld(worldCreator);
            sender.sendMessage(ChatUtil.color(Messages.CORE_PREFIX.getString() + "&aLoaded " + worldName));
            return;
        }
        sender.sendMessage(ChatUtil.color(Messages.CORE_PREFIX.getString() + "&cCouldn't find " + worldName));

    }
}
