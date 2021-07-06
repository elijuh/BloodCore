package dev.bloodcore.commands.impl.rank.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;

public class RankCreateCommand extends SubCommand {
    public RankCreateCommand() {
        super("create", ImmutableList.of("c", "make", "new"), "blood.rank.create", "/rank create <name> <priority>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length > 2) {
            String name;
            int priority;
            if (Core.i().getRankManager().getRank(args[1]) != null) {
                sender.sendMessage(ChatUtil.color("&7That rank already exists."));
                return;
            }
            name = args[1];
            if (!isSafeName(name)) {
                sender.sendMessage(ChatUtil.color("&cName contains invalid characters: &7" + name));
                return;
            }
            try {
                priority = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatUtil.color("&cInvalid integer for priority at arg #2: &7" + args[2]));
                return;
            }
            Rank rank = new Rank(name, "", priority, new HashSet<>(), new HashSet<>());
            Document data = new Document("_id", rank.getId())
                    .append("prefix", "")
                    .append("priority", priority)
                    .append("permissions", new HashSet<>())
                    .append("parents", new HashSet<>());
            Core.i().getMongoManager().getRanksCollection().insertOne(data);
        } else {
            sender.sendMessage(ChatUtil.color("&eUsage: &7/rank create <rank> <priority>"));
        }
    }

    private boolean isSafeName(String name) {
        for (char c : name.toCharArray()) {
            if (Character.getType(c) != 1 && Character.getType(c) != 2) {
                return false;
            }
        }
        return true;
    }
}
