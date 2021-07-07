package dev.bloodcore.commands.impl.rank.sub;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RankSetCommand extends SubCommand {

    public RankSetCommand() {
        super("set", ImmutableList.of("give"), "blood.rank.set", "/rank set <user> <rank>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return sender instanceof Player ? PlayerUtil.getVisiblePlayers((Player) sender) : PlayerUtil.getAllPlayers();
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
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 3) {
            Document data = Core.i().getMongoManager().getUserFromName(args[1]);
            if (data == null) {
                sender.sendMessage(ChatUtil.color("&cThat user doesn't exist."));
                return;
            }
            Rank rank = Core.i().getRankManager().getRank(args[2]);
            if (rank == null) {
                sender.sendMessage(ChatUtil.color("&cThat rank doesn't exist."));
                return;
            }
            if (data.getString("rank").equals(rank.getId())) {
                sender.sendMessage(ChatUtil.color("&cUser already has that rank."));
                return;
            }
            sender.sendMessage(ChatUtil.color("&aYou have set the rank of &r" + data.getString("display") + " &ato &r" + rank.getColor() + rank.getId() + "&a!"));
            Document update = new Document("$set", new Document("rank", rank.getId()).append("display", rank.getColor() + data.getString("name")));
            Core.i().getMongoManager().getUsersCollection().updateOne(Filters.eq("uuid", data.getString("uuid")), update);
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
        }
    }
}
