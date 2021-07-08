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

public class PingCommand extends Command {
    public PingCommand() {
        super("ping", ImmutableList.of(), "blood.command.ping");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return sender instanceof Player ? PlayerUtil.getVisiblePlayers((Player) sender) : PlayerUtil.getAllPlayers();
        }
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        User target = Core.i().getUser(args.length > 0 ? args[0] : sender.getName());
        if (sender instanceof ConsoleCommandSender && args.length == 0) {
            sender.sendMessage(ChatUtil.color("&cPlease provide a player."));
            return;
        } else if (target == null || target.isHidden()) {
            sender.sendMessage(ChatUtil.color("&cThat player is not online."));
            return;
        }

        sender.sendMessage(ChatUtil.color(Messages.PING.getString()
                .replace("%prefix%", target.getRank().getPrefix())
                .replace("%target%", target.name())
                .replace("%ping%", Integer.toString(PlayerUtil.getPing(target.getPlayer())))));
    }
}
