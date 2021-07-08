package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.User;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends Command {
    public ListCommand() {
        super("list", ImmutableList.of("online"), "blood.command.list");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        StringBuilder builder = new StringBuilder();
        List<Rank> ranks = new ArrayList<>(Core.i().getRankManager().getRanks());
        if (ranks.size() > 0) {
            ranks.sort(Comparator.comparingInt(Rank::getPriority));
            Rank rank = ranks.get(ranks.size() - 1);
            builder.append(rank.getColor()).append(rank.getId());
            for (int i = ranks.size() - 2; i >= 0; i--) {
                rank = ranks.get(i);
                builder.append("&r, ").append(rank.getColor()).append(rank.getId());
            }
        }
        List<User> users = Core.i().getUsers().stream()
                .filter(user -> !user.isHidden())
                .sorted(Comparator.comparingInt(user -> user.getRank().getPriority()))
                .collect(Collectors.toList());

        builder.append("\n&f(").append(users.size()).append("/").append(Bukkit.getMaxPlayers()).append("&f): ");
        if (users.size() > 0) {
            User user = users.get(users.size() - 1);
            builder.append(user.getRank().getColor()).append(user.name());
            for (int i = users.size() - 2; i >= 0; i--) {
                user = users.get(i);
                builder.append("&r, ").append(user.getRank().getColor()).append(user.name());
            }
        }
        sender.sendMessage(ChatUtil.color("&7&m-----------------------------"));
        sender.sendMessage(ChatUtil.color(builder.toString()));
        sender.sendMessage(ChatUtil.color("&7&m-----------------------------"));

    }
}
