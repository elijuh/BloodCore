package dev.bloodcore.commands.punishments;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.User;
import dev.bloodcore.punishments.PType;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KickCommand extends Command {
    public KickCommand() {
        super("kick", ImmutableList.of(), "blood.command.kick");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return sender instanceof Player ? PlayerUtil.getVisiblePlayers((Player) sender, args[0]) : PlayerUtil.getAllPlayers(args[0]);
        }
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            User user = Core.i().getUser(args[0]);
            if (user == null) {
                sender.sendMessage(ChatUtil.color("&cThat player is not online!"));
                return;
            }

            boolean silent = false;

            StringBuilder builder = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-s") && !silent) {
                    silent = true;
                } else {
                    if (!builder.toString().isEmpty()) {
                        builder.append(" ");
                    }
                    builder.append(args[i]);
                }
            }

            String reason = builder.toString();
            if (reason.isEmpty()) {
                sender.sendMessage(ChatUtil.color("&cPlease provide a reason."));
                return;
            }

            Document data = new Document("_id", Core.i().getPunishmentManager().nextId())
                    .append("uuid", user.uuid())
                    .append("ip", user.ip())
                    .append("type", PType.KICK.name())
                    .append("time", System.currentTimeMillis())
                    .append("length", -1L)
                    .append("reason", reason)
                    .append("silent", silent)
                    .append("executor", sender instanceof Player ? ((Player) sender).getUniqueId().toString() : null)
                    .append("server", Config.SERVER_NAME.getString());

            Core.i().getMongoManager().getPunishmentsCollection().insertOne(data);
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /kick <player> [-s] <reason...>"));
        }
    }
}
