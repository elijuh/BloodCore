package dev.bloodcore.commands.impl.rank.sub.edit;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.utils.ChatUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RankEditSetColorCommand extends SubCommand {
    public RankEditSetColorCommand() {
        super("setcolor", ImmutableList.of("color"), "blood.rank.edit.color", "/rank edit <rank> setcolor <color>");
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
            String color = args[3];
            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", data.getString("_id")), new Document("$set", new Document("color", color)));
        }
    }
}
