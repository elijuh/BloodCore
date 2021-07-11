package dev.bloodcore.commands.staff;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

public class StaffModeCommand extends Command {
    public StaffModeCommand() {
        super("staffmode", ImmutableList.of("staff", "mod", "modmode"), "blood.command.staffmode");
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

        if (user.isStaffMode()) {
            Core.i().getStaffManager().leaveStaffMode(user);
        } else {
            Core.i().getStaffManager().enterStaffMode(user);
        }
        user.msg(Messages.STAFFMODE.getString().replace("%state%", user.isStaffMode() ? "&aEnabled" : "&cDisabled"));
    }
}
