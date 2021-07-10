package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

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
                if (args.length > 0) {
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

                    user.getPlayer().teleport(requesting.getPlayer().getLocation());
                } else {
                    sender.sendMessage(ChatUtil.color("&c/tpa <player>"));
                }
            }
        };
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
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatUtil.color("&cOnly players can tpa."));
            return;
        }

        User user = Core.i().getUser(sender.getName());
        if (user == null) {
            sender.sendMessage(ChatUtil.color("&cYour profile is not loaded, please relog."));
        } else if (args.length > 1) {
            User target = Core.i().getUser(args[0]);
            if (target == null) {
                user.msg("&cThat player is not online.");
                return;
            }


            StringBuilder message = new StringBuilder(args[1]);
            for (int i = 2; i < args.length; i++) {
                message.append(" ").append(args[i]);
            }

            user.getData().put("tpa_request", target);
          //  target.getData().put("messaging", user);

            user.getPlayer().sendMessage(ChatUtil.color(Messages.MSG_TO.getString()
                    .replace("%rank_color%", target.getRank().getColor())
                    .replace("%name%", target.name())
            ) + message);
            target.getPlayer().sendMessage(ChatUtil.color(Messages.MSG_FROM.getString()
                    .replace("%rank_color%", user.getRank().getColor())
                    .replace("%name%", user.name())
            ) + message);

          // if (messaging.getSettings().getBoolean("messageSounds")) {
          //     messaging.sound(sound, Config.MESSAGE_SOUND_VOLUME.getFloat(), Config.MESSAGE_SOUND_PITCH.getFloat());
          // }
        } else {
            user.msg("&cUsage: /msg <player> <message..>");
        }
    }
}
