package dev.bloodcore.commands.impl.rank.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.commands.impl.rank.sub.edit.*;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RankEditCommand extends SubCommand {
    private final List<SubCommand> subcommands = new ArrayList<>();

    public RankEditCommand() {
        super("edit", ImmutableList.of("e"), "blood.rank.edit", "/rank edit <rank>");
        subcommands.add(new RankEditPermissionCommand());
        subcommands.add(new RankEditParentCommand());
        subcommands.add(new RankEditSetPriorityCommand());
        subcommands.add(new RankEditSetPrefixCommand());
        subcommands.add(new RankEditSetColorCommand());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Core.i().getRankManager().getRanks().stream().map(Rank::getId)
                    .filter(s -> StringUtil.startsWithIgnoreCase(s, args[1]))
                    .collect(Collectors.toList());
        } else if (args.length == 3) {
            List<String> completion = new ArrayList<>();
            for (SubCommand sub : getAvailableSubs(sender)) {
                if (StringUtil.startsWithIgnoreCase(sub.getName(), args[2])) {
                    completion.add(sub.getName());
                }
                for (String alias : sub.getAliases()) {
                    if (StringUtil.startsWithIgnoreCase(alias, args[0])) {
                        completion.add(alias);
                    }
                }
            }
            return completion;
        } else if (args.length > 3) {
            SubCommand sub = getSubCommand(args[2]);
            if (sub != null) {
                return sub.tabComplete(sender, args);
            }
        }
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length > 2) {
            SubCommand sub = getSubCommand(args[2].toLowerCase());
            if (sub != null) {
                sub.execute(sender, args);
            } else {
                sender.sendMessage(ChatUtil.color("&7Unknown edit action, use &c/rank edit &7for help."));
            }
        } else {
            help(sender);
        }
    }

    private void help(CommandSender sender) {
        sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
        List<SubCommand> subs = getAvailableSubs(sender);
        if (subs.isEmpty()) {
            sender.sendMessage(ChatUtil.color("&eYou don't have permission to use any subcommands."));
        } else {
            StringBuilder help = new StringBuilder("&6&lRank Commands &7»");
            for (SubCommand sub : getAvailableSubs(sender)) {
                help.append("\n&6» &e").append(sub.getUsage());
            }
            sender.sendMessage(ChatUtil.color(help.toString()));
        }
        sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
    }

    private List<SubCommand> getAvailableSubs(CommandSender sender) {
        return subcommands.stream()
                .filter(c -> sender.hasPermission(c.getPermission()))
                .collect(Collectors.toList());
    }

    private SubCommand getSubCommand(String name) {
        String a = name.toLowerCase();
        return subcommands.stream()
                .filter(sub -> (sub.getName().equals(a) || sub.getAliases().contains(a)))
                .findFirst().orElse(null);
    }
}
