package dev.bloodcore.commands.staff;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.commands.staff.chatmanager.ChatManagerClearCommand;
import dev.bloodcore.commands.staff.chatmanager.ChatManagerMuteCommand;
import dev.bloodcore.commands.staff.chatmanager.ChatManagerSetCooldownCommand;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatManagerCommand extends Command {
    private final List<SubCommand> subcommands = new ArrayList<>();

    public ChatManagerCommand() {
        super("chatmanager", ImmutableList.of("cm"), "blood.command.chatmanager");

        subcommands.add(new ChatManagerClearCommand());
        subcommands.add(new ChatManagerMuteCommand());
        subcommands.add(new ChatManagerSetCooldownCommand());
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
                sender.sendMessage(ChatUtil.color("&7Unknown sub-command, use &c/chatmanager help &7for help."));
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
            StringBuilder help = new StringBuilder("&6&lChat Commands &7»");
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
