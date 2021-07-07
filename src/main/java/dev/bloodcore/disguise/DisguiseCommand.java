package dev.bloodcore.disguise;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DisguiseCommand extends Command {

    public DisguiseCommand() {
        super("disguise", ImmutableList.of("nick"), "blood.world.disguise");

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {

        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {


        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage("/disguise [player name | clear]");
            return;
        }

        String playerName = args[0];
        if (playerName.equalsIgnoreCase("clear")) {
            Core.i().getDisguiseManager().deleteDisguise(player);
            player.sendMessage(ChatColor.GOLD + "Undisguised.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "Disguising...");
        Core.i().getDisguiseManager().loadDisguiseInfo(playerName, ((texture, signature) -> {
            if (texture == null || signature == null) {
                player.sendMessage(ChatColor.RED + "Failed to find \"" + playerName + "\"'s skin.");
                return;
            }

            Core.i().getDisguiseManager().applyDisguise(player, playerName, texture, signature);
            player.sendMessage(ChatColor.GOLD + "Disguised as \"" + playerName + "\"!");
        }));

        return;
    }

}

