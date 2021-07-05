package dev.bloodcore.commands.impl.essential;

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

public class FeedCommand extends Command {
    public FeedCommand() {
        super("feed", ImmutableList.of("food"), "blood.feed");
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
            {
                p.setFoodLevel(20);
                p.setSaturation(40);
                p.sendMessage(ChatUtil.color(Config.FEED_SELF.getString()));
            }
        } else {

            Player target = Core.i().getServer().getPlayer(args[0]);

            if (target == null || !target.isOnline()) {
                p.sendMessage(ChatUtil.color("&cThat player isn't online!"));
                return;
            }
            User userTarget = Core.i().getUser(target.getName());
            User userSender = Core.i().getUser(p.getName());


            target.setFoodLevel(20);
            target.setSaturation(40);

            p.sendMessage(ChatUtil.color(Config.FEED_OTHER.getString().replace("%target%", target.getName()).replace("%target_prefix%", userTarget.getRank().getPrefix()).replace("%state%", "enabled")));
            target.sendMessage(ChatUtil.color(Config.FEED_OTHER_RECEIVER.getString().replace("%sender%", p.getName()).replace("%sender_prefix%", userSender.getRank().getPrefix()).replace("%state%", "enabled")));


        }

    }
}
