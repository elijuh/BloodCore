package dev.bloodcore.commands.impl.world.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class UnloadCommand extends SubCommand {
    public UnloadCommand(){
        super("unload", ImmutableList.of(), "blood.admin", "/world unload <name>");

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
            player.sendMessage(ChatUtil.color(Messages.CORE_PREFIX + "&eTeleported you, world is being unloaded!"));
        });
        sender.sendMessage(ChatUtil.color(Messages.CORE_PREFIX.getString() + "&eUnloading " + worldName + "..."));
        new BukkitRunnable() {
            @Override
            public void run() {
                Core.i().getServer().unloadWorld(targetWorld, false);
                sender.sendMessage(ChatUtil.color(Messages.CORE_PREFIX.getString() + "&aUnloaded " + worldName));
            }
        }.runTaskLater(Core.i(), 5L);



    }
}
