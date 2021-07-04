package dev.bloodcore.commands.impl.rank.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.commands.impl.rank.RankCommand;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RankListCommand extends SubCommand {

    public RankListCommand() {
        super("list", ImmutableList.of("l"), "blood.rank.list", "/rank list");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        StringBuilder builder = new StringBuilder("&6&lAll Ranks &7»");
        List<Rank> ranks = Core.i().getRankManager().getRanks().stream().sorted(Comparator.comparingInt(Rank::getPriority)).collect(Collectors.toList());
        for (int i = ranks.size() - 1; i > -1; i--) {
            Rank rank = ranks.get(i);
            builder.append("\n&e").append(rank.getId()).append(" &8[&6&l").append(rank.getPriority()).append("&8]");
        }
        sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
        sender.sendMessage(ChatUtil.color(builder.toString()));
        sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
    }
}
