package dev.bloodcore.commands.impl.rank.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
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
                List<String> permissions = new ArrayList<>(rank.getPermissions());
                if (!permissions.isEmpty()) {
                    String permission = permissions.get(0);
                    builder.append(permission);
                    for (int i = 1; i < permissions.size(); i++) {
                        permission = permissions.get(i);
                        builder.append("&7, &a").append(permission);
                    }
                } else {
                    builder.append("&8(&7None&8)");
                }
                builder.append("\n&6Parents:\n&8» &a");
                List<String> parents = new ArrayList<>(rank.getParents());
                if (!parents.isEmpty()) {
                    String parent = parents.get(0);
                    builder.append(parent);
                    for (int i = 1; i < parents.size(); i++) {
                        parent = parents.get(i);
                        builder.append("&7, &a").append(parent);
                    }
                } else {
                    builder.append("&8(&7None&8)");
                }
                sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
                sender.sendMessage(ChatUtil.color(builder.toString()));
                sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
            } else {
                sender.sendMessage(ChatUtil.color("&7That rank doesn't exist."));
            }
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
        }
    }
}
