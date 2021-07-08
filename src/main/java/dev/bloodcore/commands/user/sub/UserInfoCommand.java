package dev.bloodcore.commands.user.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class UserInfoCommand extends SubCommand {
    private final DateFormat dateFormat = Core.i().getDateFormat();
    public UserInfoCommand() {
        super("info", ImmutableList.of("i"), "blood.user.info", "/user info <user>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return sender instanceof Player ? PlayerUtil.getVisiblePlayers((Player) sender) : PlayerUtil.getAllPlayers();
        }
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Document user = Core.i().getMongoManager().getUserFromName(args[1]);
            if (user == null) {
                sender.sendMessage(ChatUtil.color("&7That user doesn't exist."));
                return;
            }
            Document data = user.get("data", Document.class);
            Rank rank = Core.i().getRankManager().getRank(user.getString("rank"), true);
            String timezone = " &7(" + dateFormat.getTimeZone().getDisplayName(dateFormat.getTimeZone().useDaylightTime(), TimeZone.SHORT) + "&7)";
            StringBuilder builder = new StringBuilder("&6&lUser Info &7» &f").append(user.getString("name"))
                    .append("\n&eID: &7&o").append(user.getString("uuid"))
                    .append("\n&eRank: &f").append(rank.getColor()).append(rank.getId())
                    .append("\n&eDisplay: &f").append(rank.getPrefix()).append(user.getString("name"))
                    .append("\n&eFirst Join: &f").append(Core.i().getDateFormat().format(new Date(data.getLong("firstJoin")))).append(timezone)
                    .append("\n&eLast Join: &f").append(Core.i().getDateFormat().format(new Date(data.getLong("lastJoin")))).append(timezone)
                    .append("\n ")
                    .append("\n&6Permissions:\n&8» &a");
            List<String> permissions = user.getList("permissions", String.class);
            if (user.getList("permissions", String.class) != null && !permissions.isEmpty()) {
                String permission = permissions.get(0);
                builder.append(permission);
                for (int i = 1; i < permissions.size(); i++) {
                    permission = permissions.get(i);
                    builder.append("&7, &a").append(permission);
                }
            } else {
                builder.append("&8(&7None&8)");
            }
            sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
            sender.sendMessage(ChatUtil.color(builder.toString()));
            sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
        }
    }
}
