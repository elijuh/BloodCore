package dev.bloodcore.commands.staff.chatmanager;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChatManagerSetCooldownCommand extends SubCommand {

    public ChatManagerSetCooldownCommand() {
        super("setcooldown", ImmutableList.of(), "blood.chatmanager.setcooldown", "/chatmanager setcooldown <cooldown>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 2) {
            int cooldown;
            try {
                cooldown = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatUtil.color("&cInvalid integer: &7" + args[1]));
                return;
            }
            Core.i().getChatManager().setCooldown(cooldown);
            sender.sendMessage(ChatUtil.color("&aChat cooldown has been set to &f" + cooldown + " &aseconds."));
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: " + getUsage()));
        }
    }
}
