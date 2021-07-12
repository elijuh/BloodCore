package dev.bloodcore.commands.punishments;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.User;
import dev.bloodcore.punishments.HistoryGUI;
import dev.bloodcore.utils.ChatUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryCommand extends Command {
    public HistoryCommand() {
        super("history", ImmutableList.of("h"), "blood.command.history");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatUtil.color("&cOnly players can use this command as it uses a GUI."));
            return;
        }
        if (args.length == 1) {
            Document data = Core.i().getMongoManager().getUserFromName(args[0]);
            if (data == null) {
                sender.sendMessage(ChatUtil.color("&cThat player has never joined."));
            } else {
                User user = Core.i().getUser((Player) sender);
                if (user != null) {
                    user.setCurrentGUI(new HistoryGUI(user, data));
                }
            }
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /history <player>"));
        }
    }
}
