package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.YamlStorage;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
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
        Location spawn = null;
        YamlStorage worldConfig = Core.i().getWorldConfig();
        for (String key : worldConfig.getKeys(false)) {
            if (worldConfig.get(key + ".spawn") != null) {
                spawn = (Location) worldConfig.get(key + ".spawn");
                break;
            }
        }
        if (spawn == null) {
            sender.sendMessage(ChatUtil.color("&cSpawn hasn't been configured yet."));
            return;
        }
        ((Player)sender).teleport(spawn);
        sender.sendMessage(ChatUtil.color("&aYou've been teleported to spawn!"));

    }
}
