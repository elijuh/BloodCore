package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCommand extends Command {
    public GamemodeCommand() {
        super("gamemode", ImmutableList.of("gm"), "blood.command.gamemode");
        String[][] shortcuts = new String[][]{
                new String[]{"gms", "survival"},
                new String[]{"gmc", "creative"},
                new String[]{"gma", "adventure"},
                new String[]{"gmsp", "spectator"}
        };
        for (String[] shortcut : shortcuts) {
            new Command(shortcut[0], ImmutableList.of(), null) {
                @Override
                public List<String> onTabComplete(CommandSender sender, String[] args) {
                    return null;
                }

                @Override
                public void onExecute(CommandSender sender, String[] args) {
                    Bukkit.dispatchCommand(sender, "gamemode " + shortcut[1] + (args.length > 0 ? (" " + args[0]) : ""));
                }
            };
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completion = new ArrayList<>();
            for (GameMode gameMode : GameMode.values()) {
                if (StringUtil.startsWithIgnoreCase(gameMode.name(), args[0])) {
                    completion.add(gameMode.name().toLowerCase());
                }
            }
            return completion;
        } else if (args.length == 2) {
            return sender instanceof Player ? PlayerUtil.getVisiblePlayers(((Player) sender)) : PlayerUtil.getAllPlayers();
        }

        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            User target = args.length > 1 ? Core.i().getUser(args[1]) : Core.i().getUser(sender.getName());
            if (args.length == 1 && sender instanceof ConsoleCommandSender) {
                sender.sendMessage(ChatUtil.color("&cPlease provide a player."));
                return;
            } else if (target == null) {
                sender.sendMessage(ChatUtil.color("&cThat player is not online."));
                return;
            }
            GameMode gameMode;
            try {
                gameMode = GameMode.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException bruh) {
                try {
                    gameMode = GameMode.getByValue(Integer.parseInt(args[0]));
                } catch (NumberFormatException cmon) {
                    sender.sendMessage(ChatUtil.color("&cCould not find gamemode: " + args[0]));
                    return;
                }
            }

            target.getPlayer().setGameMode(gameMode);

            if (target.getPlayer() != sender) {
                sender.sendMessage(ChatUtil.color(Messages.GAMEMODE_OTHER.getString()
                        .replace("%gamemode%", gameMode.name().toLowerCase())
                        .replace("%target%", target.name())
                        .replace("%target_prefix%", target.getRank().getPrefix())));

                String senderPrefix = sender instanceof Player ? (Core.i().getUser(sender.getName()) != null ? Core.i().getUser(sender.getName()).getRank().getPrefix() : "&7") : "&4";
                target.msg(Messages.GAMEMODE_OTHER_RECEIVER.getString()
                        .replace("%gamemode%", gameMode.name().toLowerCase())
                        .replace("%sender%", sender.getName())
                        .replace("%sender_prefix%", senderPrefix));
            } else {
                target.msg(Messages.GAMEMODE_SELF.getString()
                        .replace("%gamemode%", gameMode.name().toLowerCase()));
            }
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /gamemode <gamemode> [player]"));
        }
    }
}
