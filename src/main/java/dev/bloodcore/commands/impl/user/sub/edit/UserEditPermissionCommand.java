package dev.bloodcore.commands.impl.user.sub.edit;

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

public class UserEditPermissionCommand extends SubCommand {
    private final List<String > options = ImmutableList.of("add", "remove");
    public UserEditPermissionCommand() {
        super("permission", ImmutableList.of("perm", "p"), "blood.user.edit.permission", "/user edit <user> perm <add|remove> <permission>");
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
            Document user = Core.i().getMongoManager().getUserFromName(args[1]);
            if (user == null) {
                sender.sendMessage(ChatUtil.color("&7That user doesn't exist."));
                return;
            }
            String permission = args[4].toLowerCase();
            List<String> permissions = user.getList("permissions", String.class);
            if (permissions == null) {
                permissions = new ArrayList<>();
            }
            if (args[3].equalsIgnoreCase("add")) {
                if (!permissions.contains(permission)) {
                    permissions.add(permission);
                    sender.sendMessage(ChatUtil.color("&aYou have added permission &r" + permission + " &ato &r" + user.getString("display")));
                } else {
                    sender.sendMessage(ChatUtil.color("&cThat user already has permission " + permission + "."));
                    return;
                }
            } else if (args[3].equalsIgnoreCase("remove")) {
                if (permissions.contains(permission)) {
                    permissions.remove(permission);
                    sender.sendMessage(ChatUtil.color("&aYou have removed permission &r" + permission + " &afrom &r" + user.getString("display")));
                } else {
                    sender.sendMessage(ChatUtil.color("&cThat user doesn't have permission " + permission + "."));
                    return;
                }
            } else {
                sender.sendMessage(ChatUtil.color("&cPlease provide add/remove for arg #1: &7" + args[3]));
                return;
            }
            Core.i().getMongoManager().getUsersCollection().updateOne(Filters.eq("uuid", user.getString("uuid")), new Document("$set", new Document("permissions", permissions)));
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
        }
    }
}
