package dev.bloodcore.commands.impl.rank.sub.edit;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.utils.ChatUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RankEditSetPrefixCommand extends SubCommand {
    public RankEditSetPrefixCommand() {
        super("setprefix", ImmutableList.of("prefix"), "blood.rank.edit.prefix", "/rank edit <rank> setprefix <prefix..>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length > 3) {
            Document data = Core.i().getRankManager().getRankData(args[1]);
            if (data == null) {
                sender.sendMessage(ChatUtil.color("&7That rank doesn't exist."));
                return;
            }
            StringBuilder builder = new StringBuilder(args[3]);
            for (int i = 4; i < args.length; i++) {
                builder.append(" ").append(args[i]);
            }
            String prefix = builder.toString();
            if (prefix.startsWith("\"") && prefix.endsWith("\"")) {
                prefix = prefix.substring(1, prefix.length() - 1);
            }
            Core.i().getMongoManager().getRanksCollection().updateOne(Filters.eq("_id", data.getString("_id")), new Document("$set", new Document("prefix", prefix)));
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
        }
    }
}
