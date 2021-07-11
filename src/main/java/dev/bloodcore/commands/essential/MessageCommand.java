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

public class MessageCommand extends Command {
    public MessageCommand() {
        super("message", ImmutableList.of("msg", "whisper", "tell", "w", "t"), "blood.command.msg");

        new Command("reply", ImmutableList.of("r"), "blood.command.msg") {
            @Override
            public List<String> onTabComplete(CommandSender sender, String[] args) {
                return null;
            }

            @Override
            public void onExecute(CommandSender sender, String[] args) {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(ChatUtil.color("&cOnly players can message."));
                    return;
                }
                if (args.length > 0) {
                    User user = Core.i().getUser(sender.getName());
                    if (user == null) {
                        sender.sendMessage(ChatUtil.color("&cYour profile is not loaded, please relog."));
                        return;
                    }

                    if (!user.getData().containsKey("messaging")) {
                        user.msg("&7You have nobody to reply to.");
                        return;
                    }

                    User messaging = user.get("messaging");
                    if (messaging == null) {
                        user.msg("&7That player is no longer online.");
                        return;
                    }

                    StringBuilder message = new StringBuilder();
                    for (String arg : args) {
                        message.append(" ").append(arg);
                    }
                    Bukkit.dispatchCommand(sender, "msg " + messaging.name() + message.toString());
                } else {
                    sender.sendMessage(ChatUtil.color("&c/r <message..>"));
                }
            }
        };
        new Command("togglesounds", ImmutableList.of("sounds", "messagesounds"), "blood.command.msg") {
            @Override
            public List<String> onTabComplete(CommandSender sender, String[] args) {
                return null;
            }

            @Override
            public void onExecute(CommandSender sender, String[] args) {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(ChatUtil.color("&cOnly players can message."));
                    return;
                }
                User user = Core.i().getUser(sender.getName());
                if (user == null) {
                    sender.sendMessage(ChatUtil.color("&cYour profile is not loaded, please relog."));
                } else {
                    boolean state = !user.getSettings().getBoolean("messageSounds");
                    user.getSettings().append("messageSounds", state);
                    user.msg("&eMessage sounds have been " + (state ? "&aEnabled" : "&cDisabled") + "&e.");
                }
            }
        };
        new Command("togglepm", ImmutableList.of("tpm"), "blood.command.msg") {
            @Override
            public List<String> onTabComplete(CommandSender sender, String[] args) {
                return null;
            }

            @Override
            public void onExecute(CommandSender sender, String[] args) {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(ChatUtil.color("&cOnly players can message."));
                    return;
                }
                User user = Core.i().getUser(sender.getName());
                if (user == null) {
                    sender.sendMessage(ChatUtil.color("&cYour profile is not loaded, please relog."));
                } else {
                    boolean state = !user.getSettings().getBoolean("messageToggle");
                    user.getSettings().append("messageToggle", state);
                    user.msg("&ePrivate messages have been " + (state ? "&aEnabled" : "&cDisabled") + "&e.");
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
            sender.sendMessage(ChatUtil.color("&cOnly players can message."));
            return;
        }

        User user = Core.i().getUser(sender.getName());
        if (user == null) {
            sender.sendMessage(ChatUtil.color("&cYour profile is not loaded, please relog."));
        } else if (args.length > 1) {
            User messaging = Core.i().getUser(args[0]);
            if (messaging == null) {
                user.msg("&cThat player is not online.");
                return;
            }

            if (!user.getSettings().getBoolean("messageToggle")) {
                user.msg("&7You currently have messages disabled, re-enable with &6/togglepm&7.");
                return;
            } else if (!messaging.getSettings().getBoolean("messageToggle")) {
                user.msg("&7That user has messages disabled.");
                return;
            } else if (messaging.getIgnoreList().contains(user.uuid())) {
                user.msg("&7That user has you ignored.");
                return;
            }

            StringBuilder message = new StringBuilder(args[1]);
            for (int i = 2; i < args.length; i++) {
                message.append(" ").append(args[i]);
            }

            user.getData().put("messaging", messaging);
            messaging.getData().put("messaging", user);

            user.getPlayer().sendMessage(ChatUtil.color(Messages.MSG_TO.getString()
                    .replace("%rank_color%", messaging.getRank().getColor())
                    .replace("%name%", messaging.name())
            ) + message);
            messaging.getPlayer().sendMessage(ChatUtil.color(Messages.MSG_FROM.getString()
                    .replace("%rank_color%", user.getRank().getColor())
                    .replace("%name%", user.name())
            ) + message);
            Sound sound;
            try {
                sound = Sound.valueOf(Config.MESSAGE_SOUND_ENUM.getString());
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Message Sound has invalid sound value in config!");
                return;
            }
            if (messaging.getSettings().getBoolean("messageSounds")) {
                messaging.sound(sound, Config.MESSAGE_SOUND_VOLUME.getFloat(), Config.MESSAGE_SOUND_PITCH.getFloat());
            }
        } else {
            user.msg("&cUsage: /msg <player> <message..>");
        }
    }
}
