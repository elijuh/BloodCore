package dev.bloodcore.commands.staff.chatmanager;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatManagerMuteCommand extends SubCommand {

    public ChatManagerMuteCommand() {
        super("mute", ImmutableList.of(), "blood.chatmanager.mute", "/chatmanager mute");

        new Command("mutechat", ImmutableList.of(), getPermission()) {

            @Override
            public List<String> onTabComplete(CommandSender sender, String[] args) {
                return null;
            }

            @Override
            public void onExecute(CommandSender sender, String[] args) {
                Bukkit.dispatchCommand(sender, "chatmanager mute");
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
            Bukkit.broadcastMessage(ChatUtil.color(Core.i().getChatManager().isMuted() ?
                    Messages.GLOBAL_CHAT_MUTED.getString()
                            .replace("%player%", user == null ? Messages.CONSOLE_NAME.getString() : user.getRank().getColor() + user.name()) :
                    Messages.GLOBAL_CHAT_UNMUTED.getString()
                            .replace("%player%", user == null ? Messages.CONSOLE_NAME.getString() : user.getRank().getColor() + user.name())
            ));
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
        }
    }
}
