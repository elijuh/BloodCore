package dev.bloodcore.commands.world;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.commands.world.sub.*;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorldCommand extends Command {
    private final List<SubCommand> subcommands = new ArrayList<>();

    public WorldCommand() {
        super("world", ImmutableList.of(), "blood.command.world");

        subcommands.add(new WorldCreateCommand());
        subcommands.add(new WorldDeleteCommand());
        subcommands.add(new WorldLoadCommand());
        subcommands.add(new WorldUnloadCommand());
        subcommands.add(new WorldTeleportCommand());
        subcommands.add(new WorldListCommand());
        subcommands.add(new WorldSettingsCommand());

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completion = new ArrayList<>();
            for (SubCommand sub : getAvailableSubs(sender)) {
                if (StringUtil.startsWithIgnoreCase(sub.getName(), args[0])) {
                    completion.add(sub.getName());
                }
                for (String alias : sub.getAliases()) {
                    if (StringUtil.startsWithIgnoreCase(alias, args[0])) {
                        completion.add(alias);
                    }
                }
            }
            return completion;
        } else if (args.length > 1) {
            SubCommand sub = getSubCommand(args[0]);
            if (sub != null) {
                return sub.tabComplete(sender, args);
            }
        }
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help")) {
                help(sender);
                return;
            }
            SubCommand sub = getSubCommand(args[0].toLowerCase());
            if (sub != null) {
                sub.execute(sender, args);
            } else {
                sender.sendMessage(ChatUtil.color("&7Unknown sub-command, use &c/world help &7for help."));
            }
            return;
        }
        help(sender);


    }

    private void help(CommandSender sender) {
        sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
        List<SubCommand> subs = getAvailableSubs(sender);
        if (subs.isEmpty()) {
            sender.sendMessage(ChatUtil.color("&eYou don't have permission to use any subcommands."));
        } else {
            StringBuilder help = new StringBuilder("&6&lWorld Commands &7??");
            for (SubCommand sub : getAvailableSubs(sender)) {
                help.append("\n&6?? &e").append(sub.getUsage());
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