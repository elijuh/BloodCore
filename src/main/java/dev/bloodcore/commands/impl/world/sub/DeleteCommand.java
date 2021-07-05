package dev.bloodcore.commands.impl.world.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.etc.Config;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;

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
        World targetWorld = Core.i().getServer().getWorld(worldName);
        if (targetWorld == null) {
            sender.sendMessage(ChatUtil.color("&cWorld doesn't exist!"));
            return;
        }
        World defaultWorld = Core.i().getServer().getWorld("world");
        targetWorld.getPlayers().forEach(player -> {
            player.teleport(defaultWorld.getSpawnLocation());
            player.sendMessage(ChatUtil.color(Config.CORE_PREFIX + "&eTeleported you, world is being deleted!"));
        });
        sender.sendMessage(ChatUtil.color(Config.CORE_PREFIX.getString() + "&eDeleting " + worldName + "..."));
        new BukkitRunnable() {
            @Override
            public void run() {
                Core.i().getServer().unloadWorld(targetWorld, false);
                deleteFolder(targetWorld.getWorldFolder());
                Core.i().getWorldConfig().set(worldName, null);
                Core.i().getWorldConfig().save();
                sender.sendMessage(ChatUtil.color(Config.CORE_PREFIX.getString() + "&aDeleted " + worldName));
            }
        }.runTaskLater(Core.i(), 5L);



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
