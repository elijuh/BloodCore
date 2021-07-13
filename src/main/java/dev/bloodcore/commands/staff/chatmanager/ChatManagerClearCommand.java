package dev.bloodcore.commands.staff.chatmanager;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatManagerClearCommand extends SubCommand {

    public ChatManagerClearCommand() {
        super("clear", ImmutableList.of(), "blood.chatmanager.clear", "/chatmanager clear");

        new Command("clearchat", ImmutableList.of(), getPermission()) {

            @Override
            public List<String> onTabComplete(CommandSender sender, String[] args) {
                return null;
            }

            @Override
            public void onExecute(CommandSender sender, String[] args) {
                Bukkit.dispatchCommand(sender, "chatmanager clear");
            }
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            User user = sender instanceof Player ? Core.i().getUser(sender.getName()) : null;
            Core.i().getChatManager().setMuted(!Core.i().getChatManager().isMuted());
            StringBuilder message = new StringBuilder();
            //lol imagine sending 500 chat packets to each player
            for (int i = 0; i < 500; i++) {
                message.append("\n").append(ChatColor.values()[i % 16]).append(" ");
            }
            for (User all : Core.i().getUsers()) {
                if (!all.getPlayer().hasPermission("blood.bypass.clearchat")) {
                    all.msg(message.toString());
                }
                all.msg(Messages.GLOBAL_CHAT_CLEARED.getString()
                        .replace("%player%", user == null ? Messages.CONSOLE_NAME.getString() : user.getRank().getColor() + user.name())
                );
            }
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
        }
    }
}
