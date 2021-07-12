package dev.bloodcore.commands.staff;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.punishments.PType;
import dev.bloodcore.utils.ChatUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AltsCommand extends Command {
    public AltsCommand() {
        super("alts", ImmutableList.of("accounts"), "blood.command.alts");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Document target = Core.i().getMongoManager().getUserFromName(args[0]);
            if (target == null) {
                sender.sendMessage(ChatUtil.color("&cThat player has never joined."));
                return;
            }
            Bukkit.getScheduler().runTaskAsynchronously(Core.i(), ()-> {
                StringBuilder accounts = new StringBuilder("&6Showing Accounts on &r" + target.getString("name") + "'s IP&6.");
                for (Document user : Core.i().getPunishmentManager().getAccounts(target.getString("ip"))) {
                    accounts.append("\n&8» ");
                    accounts.append(user.getString("display"));
                    if (Core.i().getPunishmentManager().getActivePunishment(user, PType.IPBAN) != null) {
                        accounts.append(" &8(&4IP Banned&8)");
                    } else if (Core.i().getPunishmentManager().getActivePunishment(user, PType.BAN) != null) {
                        accounts.append(" &8(&cBanned&8)");
                    } else if (Core.i().getPunishmentManager().getActivePunishment(user, PType.MUTE) != null) {
                        accounts.append(" &8(&eMuted&8)");
                    } else {
                        accounts.append(" &8(&7Not Punished&8)");
                    }
                }
                sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
                sender.sendMessage(ChatUtil.color(accounts.toString()));
                sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
            });
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /alts <player>"));
        }
    }
}
