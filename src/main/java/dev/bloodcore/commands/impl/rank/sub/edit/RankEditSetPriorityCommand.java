package dev.bloodcore.commands.impl.rank.sub.edit;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.utils.ChatUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RankEditSetPriorityCommand extends SubCommand {
    public RankEditSetPriorityCommand() {
        super("setpriority", ImmutableList.of("priority"), "blood.rank.edit.priority", "/rank edit <rank> setpriority <priority>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 4) {
            Document data = Core.i().getRankManager().getRankData(args[1]);
            if (data == null) {
                sender.sendMessage(ChatUtil.color("&7That rank doesn't exist."));
                return;
            }
            int priority;
            try {
                priority = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatUtil.color("&cInvalid integer for arg #1: &7" + args[3]));
                return;
            }
            Core.i().getMongoManager().getRanksCollection().updateOne(Filters.eq("_id", data.getString("_id")), new Document("$set", new Document("priority", priority)));
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
        }
    }
}
