package dev.bloodcore.commands.impl.rank.sub.edit;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.utils.ChatUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RankEditPermissionCommand extends SubCommand {
    private final List<String > options = ImmutableList.of("add", "remove");
    public RankEditPermissionCommand() {
        super("permission", ImmutableList.of("perm", "p"), "blood.rank.edit.permission", "/rank edit <rank> permission <add|remove> <permission>");
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
        }
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 5) {
            Document data = Core.i().getRankManager().getRankData(args[1]);
            if (data == null) {
                sender.sendMessage(ChatUtil.color("&7That rank doesn't exist."));
                return;
            }
            String permission = args[4].toLowerCase();
            List<String> permissions = data.getList("permissions", String.class);
            if (permissions == null) {
                permissions = new ArrayList<>();
            }
            if (args[3].equalsIgnoreCase("add")) {
                if (!permissions.contains(permission)) {
                    permissions.add(permission);
                } else {
                    sender.sendMessage(ChatUtil.color("&cThat rank already has permission " + permission + "."));
                    return;
                }
            } else if (args[3].equalsIgnoreCase("remove")) {
                if (permissions.contains(permission)) {
                    permissions.remove(permission);
                } else {
                    sender.sendMessage(ChatUtil.color("&cThat rank doesn't have permission " + permission + "."));
                    return;
                }
            } else {
                sender.sendMessage(ChatUtil.color("&cPlease provide add/remove for arg #1: &7" + args[3]));
                return;
            }
            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", data.getString("_id")), new Document("$set", new Document("permissions", permissions)));
        } else {
            sender.sendMessage(ChatUtil.color("&eUsage: &7/" + getUsage()));
        }
    }
}
