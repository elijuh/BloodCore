package dev.bloodcore.commands.punishments;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Config;
import dev.bloodcore.punishments.PType;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class IPBanCommand extends Command {
    public IPBanCommand() {
        super("ipban", ImmutableList.of("banip", "blacklist"), "blood.command.ipban");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return sender instanceof Player ? PlayerUtil.getVisiblePlayers((Player) sender) : PlayerUtil.getAllPlayers();
        }
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            Document user = Core.i().getMongoManager().getUserFromName(args[0]);
            if (user == null) {
                sender.sendMessage(ChatUtil.color("&cThat player has never joined!"));
                return;
            }

            if (Core.i().getPunishmentManager().getActivePunishment(user, PType.IPBAN) != null) {
                sender.sendMessage(ChatUtil.color("&cTarget is already ipbanned!"));
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
                    .append("uuid", user.getString("uuid"))
                    .append("ip", user.getString("ip"))
                    .append("type", PType.IPBAN.name())
                    .append("time", System.currentTimeMillis())
                    .append("length", -1L)
                    .append("reason", reason)
                    .append("silent", silent)
                    .append("executor", sender instanceof Player ? ((Player) sender).getUniqueId().toString() : null)
                    .append("server", Config.SERVER_NAME.getString());

            Core.i().getMongoManager().getPunishmentsCollection().insertOne(data);
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /ipban <player> [-s] <reason...>"));
        }
    }
}