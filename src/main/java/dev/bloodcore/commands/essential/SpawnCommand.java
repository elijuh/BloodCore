package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnCommand extends Command {
    public SpawnCommand() {
        super("spawn", ImmutableList.of(), "blood.command.spawn");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatUtil.color("&cPlayer-only command."));
        } else if (!Core.i().getConfig().contains("spawn")) {
            sender.sendMessage(ChatUtil.color("&cSpawn has not been set yet!"));
        } else {
            String[] info = Core.i().getConfig().getString("spawn").split(";");
            Location spawn = new Location(
                    Bukkit.getWorld(info[0]),
                    Double.parseDouble(info[1]),
                    Double.parseDouble(info[2]),
                    Double.parseDouble(info[3]),
                    Float.parseFloat(info[4]),
                    Float.parseFloat(info[5])
            );
            ((Player) sender).teleport(spawn);
            sender.sendMessage(ChatUtil.color("&aYou have teleported to spawn."));
        }
    }
}
