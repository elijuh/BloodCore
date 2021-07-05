package dev.bloodcore.commands.impl.world.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.etc.Config;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.io.File;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DeleteCommand extends SubCommand {
    public DeleteCommand() {
        super("delete", ImmutableList.of("remove"), "blood.admin", "/world delete");

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
        World defaultWorld = Core.i().getServer().getWorld("world");

        World targetWorld = Core.i().getServer().getWorld(worldName);
        if (targetWorld != null) {
            targetWorld.getPlayers().forEach(player -> {
                player.teleport(defaultWorld.getSpawnLocation());
                player.sendMessage(ChatUtil.color(Config.CORE_PREFIX + "&eTeleported you, world is being deleted!"));
                Core.i().getServer().unloadWorld(targetWorld, false);

            });
        } else if (!Core.i().getWorldConfig().getKeys(false).contains(worldName)) {
            sender.sendMessage(ChatUtil.color("&cCouldn't find that world"));
            return;
        }

        sender.sendMessage(ChatUtil.color(Config.CORE_PREFIX.getString() + "&eDeleting " + worldName + "..."));

        File worldFile = new File(Core.i().getServer().getWorldContainer(), worldName);
        deleteFolder(worldFile);
        Core.i().getWorldConfig().set(worldName, null);
        Core.i().getWorldConfig().save();
        sender.sendMessage(ChatUtil.color(Config.CORE_PREFIX.getString() + "&aDeleted " + worldName));


    }

    private void deleteFolder(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
            path.delete();
        }
    }
}
