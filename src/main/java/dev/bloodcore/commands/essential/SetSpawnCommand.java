package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import dev.bloodcore.etc.YamlStorage;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSpawnCommand extends Command {
    public SetSpawnCommand() {
        super("setspawn", ImmutableList.of(), "blood.command.setspawn");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {

        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage(ChatUtil.color("&cThis command can't be used from console."));
            return;
        }
        YamlStorage worldConfig = Core.i().getWorldConfig();
        for (String key : worldConfig.getKeys(false)) {
            worldConfig.set(key + ".spawn", null);
        }
        String worldName = ((Player)sender).getWorld().getName();
        worldConfig.set(worldName + ".spawn", ((Player) sender).getLocation());
        sender.sendMessage(ChatUtil.color(Messages.CORE_PREFIX + " &eYou've set the server's spawn location!"));
    }
}
