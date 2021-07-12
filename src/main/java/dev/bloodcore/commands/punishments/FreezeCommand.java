package dev.bloodcore.commands.punishments;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.User;
import dev.bloodcore.punishments.PType;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FreezeCommand extends Command {
    public FreezeCommand() {
        super("freeze", ImmutableList.of("ss"), "blood.command.freeze");
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
        if (args.length == 1) {
            User user = Core.i().getUser(args[0]);
            if (user == null) {
                sender.sendMessage(ChatUtil.color("&cThat player is not online!"));
                return;
            }
            user.setFrozen(!user.isFrozen());
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /freeze <player>"));
        }
    }
}
