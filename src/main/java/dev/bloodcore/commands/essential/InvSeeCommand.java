package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class InvSeeCommand extends Command {
    public InvSeeCommand() {
        super("invsee", ImmutableList.of(), "blood.command.invsee");
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
        if (sender instanceof ConsoleCommandSender){
            sender.sendMessage(ChatUtil.color("&cCan't use this command from console"));
            return;
        }
        User user = Core.i().getUser(sender.getName());
        if (user == null) {
            sender.sendMessage(ChatUtil.color("&cYour profile is not loaded, please relog."));
            //bruh you forgot to return what an idiot lmao
            return;
        }
        //also forget to length check args like a monkey
        if (args.length > 0) {
            User target = Core.i().getUser(args[0]);
            if (target == null) {
                user.msg("&cThat player is not online.");
                return;
            }

            user.getData().put("invseeing", true);
            user.getPlayer().openInventory(target.getPlayer().getInventory());
            user.sound(Sound.CLICK, 0.5f, 1f);
            user.msg("&eOpening inventory of " + target.getRank().getColor() + target.name() + "&e.");
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /invsee <player>"));
        }
    }
}
