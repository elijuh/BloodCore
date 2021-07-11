package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SudoCommand extends Command {
    public SudoCommand() {
        super("sudo", ImmutableList.of(), "blood.command.sudo");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return sender instanceof Player ? PlayerUtil.getVisiblePlayers((Player) sender, args[0]) : PlayerUtil.getAllPlayers(args[0]);
        }
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        User uSender = Core.i().getUser(sender.getName());
        User target = args.length > 0 ? Core.i().getUser(args[0]) : Core.i().getUser(sender.getName());
        if (args.length == 0 && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatUtil.color("&cPlease provide a player."));
            return;
        } else if (target == null) {
            sender.sendMessage(ChatUtil.color("&cThat player is not online."));
            return;
        }
        if (args.length == 1) {
            sender.sendMessage(ChatUtil.color("&cUsage: /sudo <player> <args>"));
            return;
        }
        if (target.getPlayer() != sender) {
            StringBuilder parts = new StringBuilder(args[1]);
            for (int i = 2; i < args.length; i++) {
                parts.append(" ").append(args[i]);
            }
            String command = parts.toString();
            sender.sendMessage(ChatUtil.color(Messages.SUDO_OTHER.getString()
                    .replace("%target%", target.name())
                    .replace("%target_prefix%", target.getRank().getPrefix())
                    .replace("%command%", command)));

            String staffMessage = ChatUtil.color(Messages.SUDO_STAFF.getString()
                    .replace("%target%", target.name())
                    .replace("%target_prefix%", target.getRank().getPrefix())
                    .replace("%command%", command)
                    .replace("%sender_prefix%", sender instanceof Player ? uSender.getRank().getPrefix() : "")
                    .replace("%sender%", sender instanceof Player ? sender.getName() : "&rConsole"));
            for (Player onlinePlayer : Core.i().getServer().getOnlinePlayers()) {
                if(onlinePlayer.hasPermission("blood.command.sudo")){
                    onlinePlayer.sendMessage(staffMessage);
                }
            }

            target.getPlayer().chat(command);

        } else {
            target.msg(ChatUtil.color(Messages.SUDO_YOURSELF.getString()));
        }
    }
}
