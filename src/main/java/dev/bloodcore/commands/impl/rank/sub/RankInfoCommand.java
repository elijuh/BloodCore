package dev.bloodcore.commands.impl.rank.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RankInfoCommand extends SubCommand {

    public RankInfoCommand() {
        super("info", ImmutableList.of("i"), "blood.rank.info", "/rank info <rank>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Rank rank = Core.i().getRankManager().getRank(args[1]);
            if (rank != null) {
                StringBuilder builder = new StringBuilder("&6&lRank Info &7»")
                        .append("\n&eID: &f").append(rank.getId())
                        .append("\n&ePriority: &f").append(rank.getPriority())
                        .append("\n&ePrefix: &7(&r").append(rank.getPrefix()).append("Player&7)")
                        .append("\n&6Permissions:\n&8» &a");
                for (String permission : rank.getPermissions()) {
                    builder.append(permission).append(", ");
                }
                if (builder.toString().endsWith(", ")) {
                    builder.delete(builder.length() - 2, builder.length());
                }
                builder.append("\n&6Parents:\n&8» &a");
                for (String parent : rank.getParents()) {
                    builder.append(parent).append(", ");
                }
                if (builder.toString().endsWith(", ")) {
                    builder.delete(builder.length() - 2, builder.length());
                }
                sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
                sender.sendMessage(ChatUtil.color(builder.toString()));
                sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
            } else {
                sender.sendMessage(ChatUtil.color("&7That rank doesn't exist."));
            }
        } else {
            sender.sendMessage(ChatUtil.color("&eUsage: &7" + getUsage()));
        }
    }
}
