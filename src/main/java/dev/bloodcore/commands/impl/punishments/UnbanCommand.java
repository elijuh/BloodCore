package dev.bloodcore.commands.impl.punishments;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.punishments.PType;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UnbanCommand extends Command {
    public UnbanCommand() {
        super("unban", ImmutableList.of(), "blood.unban");
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
        if (args.length > 0) {
            Document user = Core.i().getMongoManager().getUserFromName(args[0]);
            if (user == null) {
                sender.sendMessage(ChatUtil.color("&cThat player has never joined!"));
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

            String reason = builder.toString().isEmpty() ? "None" : builder.toString();

            Document info = new Document("by", sender instanceof Player ? ((Player) sender).getUniqueId().toString() : null)
                    .append("reason", reason)
                    .append("silent", silent);

            if (Core.i().getPunishmentManager().getActivePunishment(user, PType.BAN) != null) {
                Core.i().getPunishmentManager().remove(user, PType.BAN, info);
            } else {
                sender.sendMessage(ChatUtil.color("&cThat player is not banned!"));
            }
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /unban <player> [-s] [reason...]"));
        }
    }
}
