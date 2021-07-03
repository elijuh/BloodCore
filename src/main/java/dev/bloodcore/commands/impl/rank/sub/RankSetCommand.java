package dev.bloodcore.commands.impl.rank.sub;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.commands.impl.rank.RankCommand;
import dev.bloodcore.etc.User;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RankSetCommand extends SubCommand {

    public RankSetCommand(RankCommand parent) {
        super(parent, "set", ImmutableList.of("give"), "blood.rank.set", "/rank set <user> <rank>");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> completion = new ArrayList<>();
            for (User user : Core.i().getUsers()) {
                if (StringUtil.startsWithIgnoreCase(user.name(), args[1])) {
                    completion.add(user.name());
                }
            }
            return completion;
        } else if (args.length == 3) {
            List<String> completion = new ArrayList<>();
            for (Rank rank : Core.i().getRankManager().getRanks()) {
                if (StringUtil.startsWithIgnoreCase(rank.getId(), args[2])) {
                    completion.add(rank.getId());
                }
            }
            return completion;
        }
        return ImmutableList.of();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 3) {
            Document data = Core.i().getMongoManager().getData(args[1]);
            if (data == null) {
                sender.sendMessage(ChatUtil.color("&cThat user doesn't exist."));
                return;
            }
            Rank rank = Core.i().getRankManager().getRank(args[2]);
            if (rank == null) {
                sender.sendMessage(ChatUtil.color("&cThat rank doesn't exist."));
                return;
            }
            Core.i().getMongoManager().getUsersCollection().updateOne(Filters.eq("uuid", data.getString("uuid")), new Document("$set", new Document("rank", rank.getId())));
        } else {
            sender.sendMessage(ChatUtil.color("&eUsage: &7" + getUsage()));
        }
    }
}
