package dev.bloodcore.commands.impl.world.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WorldTeleportCommand extends SubCommand {
    public WorldTeleportCommand(){
        super("tp", ImmutableList.of("teleport", "goto"), "blood.admin", "/world tp <name>");

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if(sender instanceof ConsoleCommandSender) return;
        if(args.length == 1){
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
            return;
        }
        String worldName = args[1];
        World world = Core.i().getServer().getWorld(worldName);
        if(world == null){
            sender.sendMessage(ChatUtil.color("&cWorld not found!"));
            return;
        }
        ((Player)sender).teleport(world.getSpawnLocation());
        sender.sendMessage(ChatUtil.color("&aTeleported you to " + world.getName()));

    }
}
