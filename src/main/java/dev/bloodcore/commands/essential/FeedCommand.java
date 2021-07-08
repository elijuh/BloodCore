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
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FeedCommand extends Command {
    public FeedCommand() {
        super("feed", ImmutableList.of("food"), "blood.command.feed");
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
        User target = args.length > 0 ? Core.i().getUser(args[0]) : Core.i().getUser(sender.getName());
        if (args.length == 0 && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatUtil.color("&cPlease provide a player."));
            return;
        } else if (target == null) {
            sender.sendMessage(ChatUtil.color("&cThat player is not online."));
            return;
        }

        target.getPlayer().setFoodLevel(40);
        target.getPlayer().setSaturation(40);
        if (target.getPlayer() != sender) {
            sender.sendMessage(ChatUtil.color(Messages.FEED_OTHER.getString()
                    .replace("%target%", target.name())
                    .replace("%target_prefix%", target.getRank().getPrefix())));
            if (target.getPlayer() != sender) {
                String senderPrefix = sender instanceof Player ? (Core.i().getUser(sender.getName()) != null ? Core.i().getUser(sender.getName()).getRank().getPrefix() : "&7") : "&4";
                target.msg(Messages.FEED_OTHER_RECEIVER.getString()
                        .replace("%sender%", sender.getName())
                        .replace("%sender_prefix%", senderPrefix));
            }
        } else {
            target.msg(Messages.FEED_SELF.getString());
        }
    }
}
