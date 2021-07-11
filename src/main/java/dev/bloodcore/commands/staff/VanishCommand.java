package dev.bloodcore.commands.staff;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

public class VanishCommand extends Command {
    public VanishCommand() {
        super("vanish", ImmutableList.of("v"), "blood.command.vanish");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return;
        }
        User user = Core.i().getUser(sender.getName());
        if (user == null) {
            return;
        }

        if (user.isVanished()) {
            Core.i().getStaffManager().unvanish(user);
        } else {
            Core.i().getStaffManager().vanish(user);
        }
        user.msg(Messages.VANISH.getString().replace("%state%", user.isVanished() ? "&aEnabled" : "&cDisabled"));
    }
}
