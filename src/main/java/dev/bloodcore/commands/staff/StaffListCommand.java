package dev.bloodcore.commands.staff;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.User;
import dev.bloodcore.staff.StaffGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StaffListCommand extends Command {
    public StaffListCommand() {
        super("stafflist", ImmutableList.of("sl"), "blood.command.stafflist");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        User user = Core.i().getUser((Player) sender);
        if (user != null) {
            user.setCurrentGUI(new StaffGUI(user.getPlayer()));
        }
    }
}
