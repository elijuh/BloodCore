package dev.bloodcore.commands.impl.world.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;

public class ListCommand extends SubCommand {
    public ListCommand() {
        super("list", ImmutableList.of(), "blood.admin", "/world list");

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
        HashSet<String> loadedWorlds = new HashSet<>();

        for (World world : Core.i().getServer().getWorlds()) {
            //todo fix later
            loadedWorlds.add(world.getName());
            sender.sendMessage(ChatUtil.color("&6» &e" + world.getName() + " &a(players: " + world.getPlayers().size() + ") &7(entities: " + world.getEntities().size() + ") &6(chunks: " + world.getLoadedChunks().length + ")"));
        }
        for (String key : Core.i().getWorldConfig().getKeys(false)) {
            if (loadedWorlds.contains(key)) {
                continue;
            }
            sender.sendMessage(ChatUtil.color("&6» &7" + key + " (unloaded)"));
        }
        sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));


    }

}
