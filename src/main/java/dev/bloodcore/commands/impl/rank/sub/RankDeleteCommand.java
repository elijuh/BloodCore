package dev.bloodcore.commands.impl.rank.sub;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.List;
import java.util.stream.Collectors;

public class RankDeleteCommand extends SubCommand {
    public RankDeleteCommand() {
        super("delete", ImmutableList.of("remove"), "blood.rank.delete", "/rank delete <rank>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Core.i().getRankManager().getRanks().stream().map(Rank::getId)
                    .filter(s -> StringUtil.startsWithIgnoreCase(s, args[1]))
                    .collect(Collectors.toList());
        }
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Rank rank = Core.i().getRankManager().getRank(args[1]);
            if (rank == null) {
                sender.sendMessage(ChatUtil.color("&7That rank doesn't exist."));
            } else if (rank.getId().equalsIgnoreCase("default")) {
                sender.sendMessage(ChatUtil.color("&7You cannot delete the default rank."));
            } else {
                Core.i().getMongoManager().getRanksCollections().deleteOne(Filters.eq("_id", rank.getId()));
            }
        } else {
            sender.sendMessage(ChatUtil.color("&7Usage: &7/rank delete <rank>"));
        }
    }
}
