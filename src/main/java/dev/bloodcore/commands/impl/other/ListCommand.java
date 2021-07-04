package dev.bloodcore.commands.impl.other;

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

public class ListCommand extends Command {
    public ListCommand() {
        super("list", ImmutableList.of("online"), null);
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
        long players = Core.i().getUsers().stream()
                .filter(user -> !user.getData().containsKey("vanished") || !(boolean) user.get("vanished")).count();

        builder.append("\n&f(").append(players).append("/").append(Bukkit.getMaxPlayers()).append("&f): ");
        for (User user : Core.i().getUsers()) {
            if (!user.getData().containsKey("vanished") || !(boolean) user.get("vanished")) {
                builder.append(user.getRank().getColor()).append(user.name());
            }
        }
        sender.sendMessage(ChatUtil.color("&7&m-----------------------------"));
        sender.sendMessage(ChatUtil.color(builder.toString()));
        sender.sendMessage(ChatUtil.color("&7&m-----------------------------"));

    }
}
