package dev.bloodcore.commands.rank.sub.edit;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RankEditParentCommand extends SubCommand {
    private final List<String > options = ImmutableList.of("add", "remove");
    public RankEditParentCommand() {
        super("parent", ImmutableList.of("inheritance"), "blood.rank.edit.parent", "/rank edit <rank> parent <add|remove> <parent>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 4) {
            List<String> completion = new ArrayList<>();
            for (String option : options) {
                if (StringUtil.startsWithIgnoreCase(option, args[3])) {
                    completion.add(option);
                }
            }
            return completion;
        } else if (args.length == 5) {
            if (!options.contains(args[3])) {
                return ImmutableList.of();
            }
            Rank rank = Core.i().getRankManager().getRank(args[1]);
            if (rank == null) {
                return ImmutableList.of();
            }
            Set<String> parents = rank.getParents();
            List<String> completion = new ArrayList<>();
            for (Rank otherRank : Core.i().getRankManager().getRanks()) {
                if (StringUtil.startsWithIgnoreCase(otherRank.getId(), args[4]) && !parents.contains(otherRank.getId()) && otherRank != rank) {
                    completion.add(otherRank.getId());
                }
            }
            return completion;
        }
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 5) {
            Document data = Core.i().getRankManager().getRankData(args[1]);
            if (data == null) {
                sender.sendMessage(ChatUtil.color("&7That rank doesn't exist &c(" + args[1] + ")"));
                return;
            }
            Rank parent = Core.i().getRankManager().getRank(args[4]);
            if (parent == null) {
                sender.sendMessage(ChatUtil.color("&7That rank doesn't exist &c(" + args[4] + ")"));
                return;
            }
            List<String> parents = data.getList("parents", String.class);
            if (parents == null) {
                parents = new ArrayList<>();
            }
            String id = parent.getId();
            if (args[3].equalsIgnoreCase("add")) {
                if (!parents.contains(id) && !parent.getId().equals(data.getString("_id"))) {
                    parents.add(id);
                } else {
                    sender.sendMessage(ChatUtil.color("&cThat rank already has parent " + id + "."));
                    return;
                }
            } else if (args[3].equalsIgnoreCase("remove")) {
                if (parents.contains(id)) {
                    parents.remove(id);
                } else {
                    sender.sendMessage(ChatUtil.color("&cThat rank doesn't have parent " + id + "."));
                    return;
                }
            } else {
                sender.sendMessage(ChatUtil.color("&cPlease provide add/remove for arg #1: &7" + args[3]));
                return;
            }
            Core.i().getMongoManager().getRanksCollection().updateOne(Filters.eq("_id", data.getString("_id")), new Document("$set", new Document("parents", parents)));
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
        }
    }
}
