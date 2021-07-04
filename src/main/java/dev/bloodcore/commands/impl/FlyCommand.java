package dev.bloodcore.commands.impl;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FlyCommand extends Command {
    public FlyCommand() {
        super("fly", ImmutableList.of("flight"), "bloodcore.fly");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completion = new ArrayList<>();
            Set<User> users = new HashSet<>();
            if (sender instanceof Player) {
                users.addAll(Core.i().getUsers().stream().filter(user -> ((Player) sender).canSee(user.getPlayer())).collect(Collectors.toSet()));
            }
            for (User user : users) {
                if (StringUtil.startsWithIgnoreCase(user.name(), args[0])) {
                    completion.add(user.name());
                }
            }
            return completion;
        }
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (args.length == 0) {
            if (p.getAllowFlight()) {
                p.setFlying(false);
                p.setAllowFlight(false);

                p.sendMessage(ChatUtil.color(Config.FLY_TOGGLE_SELF.getString().replace("%state%", "disabled")));
            } else {
                p.setAllowFlight(true);
                p.sendMessage(ChatUtil.color(Config.FLY_TOGGLE_SELF.getString().replace("%state%", "enabled")));
            }
        } else {

            Player target = Core.i().getServer().getPlayer(args[0]);

            if (target == null || !target.isOnline()) {
                p.sendMessage(ChatUtil.color("&cThat player isn't online!"));
                return;
            }
            User userTarget = Core.i().getUser(target.getName());
            User userSender = Core.i().getUser(p.getName());
            if (target.getAllowFlight()) {
                target.setAllowFlight(false);
                target.setFlying(false);
                p.sendMessage(ChatUtil.color(Config.FLY_TOGGLE_OTHER.getString().replace("%target%", target.getName()).replace("%target_prefix%", userTarget.getRank().getPrefix()).replace("%state%", "disabled")));
                target.sendMessage(ChatUtil.color(Config.FLY_TOGGLE_OTHER_RECEIVER.getString().replace("%sender%", p.getName()).replace("%sender_prefix%", userSender.getRank().getPrefix()).replace("%state%", "disabled")));

            } else {
                target.setAllowFlight(true);
                target.setFlying(true);
                p.sendMessage(ChatUtil.color(Config.FLY_TOGGLE_OTHER.getString().replace("%target%", target.getName()).replace("%target_prefix%", userTarget.getRank().getPrefix()).replace("%state%", "enabled")));
                target.sendMessage(ChatUtil.color(Config.FLY_TOGGLE_OTHER_RECEIVER.getString().replace("%sender%", p.getName()).replace("%sender_prefix%", userSender.getRank().getPrefix()).replace("%state%", "enabled")));
            }

        }

    }
}
