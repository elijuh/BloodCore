package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.commands.Command;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TpCommand extends Command {
    public TpCommand() {
        super("tp", ImmutableList.of("teleport"), "blood.command.tp");

        new Command("tphere", ImmutableList.of("s"), "blood.command.tp") {

            @Override
            public List<String> onTabComplete(CommandSender sender, String[] args) {
                return null;
            }

            @Override
            public void onExecute(CommandSender sender, String[] args) {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(ChatUtil.color("&cConsole cannot use /tphere."));
                } else if (args.length > 0) {
                    Bukkit.dispatchCommand(sender, "tp " + args[0] + " " + sender.getName());
                } else {
                    sender.sendMessage(ChatUtil.color("&cUsage: /tphere <player>"));
                }
            }
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 || args.length == 2) {
            return sender instanceof Player ? PlayerUtil.getVisiblePlayers((Player) sender, args[args.length - 1]) : PlayerUtil.getAllPlayers(args[args.length - 1]);
        }
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Player target1 = sender instanceof Player ? (Player) sender : null;
        Player target2;

        if (args.length > 0) {
            target2 = Bukkit.getPlayer(args[0]);
            if (target2 == null) {
                sender.sendMessage(ChatUtil.color("&cThat player is not online!"));
                return;
            }
            if (args.length > 1) {
                target1 = Bukkit.getPlayer(args[0]);
                target2 = Bukkit.getPlayer(args[1]);

                if (target1 == null || target2 == null) {
                    sender.sendMessage(ChatUtil.color("&cThat player is not online!"));
                    return;
                }
            } else if (!(sender instanceof Player)) {
                sender.sendMessage(ChatUtil.color("&cPlease provide 2 targets since you cannot teleport!"));
                sender.sendMessage(ChatUtil.color("&c/tp <player> <player>"));
                return;
            }

            target1.teleport(target2);
            sender.sendMessage(ChatUtil.color("&eYou have teleported " +
                    (target1.equals(sender) ? "" : "&6" + target1.getName() + " ") + "&eto &6" + target2.getName() + "&e."));
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /tp <player> [player]"));
        }
    }
}
