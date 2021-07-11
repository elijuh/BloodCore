package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TpaCommand extends Command {
    public TpaCommand() {
        super("tpa", ImmutableList.of(), "blood.command.tpa");

        new Command("tpaccept", ImmutableList.of(), "blood.command.tpa") {
            @Override
            public List<String> onTabComplete(CommandSender sender, String[] args) {
                return null;
            }

            @Override
            public void onExecute(CommandSender sender, String[] args) {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(ChatUtil.color("&cOnly players can tpa."));
                    return;
                }
                if (args.length == 0) {
                    User user = Core.i().getUser(sender.getName());
                    if (user == null) {
                        sender.sendMessage(ChatUtil.color("&cYour profile is not loaded, please relog."));
                        return;
                    }

                    if (!user.getData().containsKey("tpa_request")) {
                        user.msg("&7You have nobody to accept");
                        return;
                    }

                    User requesting = user.get("tpa_request");
                    if (requesting == null) {
                        user.msg("&7That player is no longer online.");
                        return;
                    }

                    user.getData().remove("tpa_request");
                    requesting.getPlayer().teleport(user.getPlayer());
                    requesting.msg(user.getRank().getColor() + user.name() + " &ehas accepted your teleport request&e!");
                    user.msg("&eYou have been teleported to " + requesting.getRank().getColor() + requesting.name() + "&e!");
                } else {
                    sender.sendMessage(ChatUtil.color("&cUsage: /tpaccept"));
                }
            }
        };
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
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatUtil.color("&cOnly players can tpa."));
            return;
        }

        User user = Core.i().getUser(sender.getName());
        if (user == null) {
            sender.sendMessage(ChatUtil.color("&cYour profile is not loaded, please relog."));
        } else if (args.length > 0) {
            User target = Core.i().getUser(args[0]);
            if (target == null) {
                user.msg("&cThat player is not online.");
                return;
            } else if (target.getIgnoreList().contains(user.uuid())) {
                user.msg("&cThat player has you ignored.");
                return;
            }

            target.getData().put("tpa_request", user);
            user.msg("&eYou have sent a tpa request to " + target.getRank().getColor() + target.name() + "&e!");
            target.msg("&eYou have received a tpa request from " + user.getRank().getColor() + user.name() + "&e!");
            target.msg("&eType &a/tpaccept &eto accept.");
        } else {
            user.msg("&cUsage: /tpa <player>");
        }
    }
}
