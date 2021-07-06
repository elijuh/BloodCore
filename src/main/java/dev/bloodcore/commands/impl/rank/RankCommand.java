package dev.bloodcore.commands.impl.rank;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.commands.impl.rank.sub.*;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RankCommand extends Command {
    private final List<SubCommand> subcommands = new ArrayList<>();

    public RankCommand() {
        super("rank", ImmutableList.of(), "blood.rank.use");
        subcommands.add(new RankListCommand());
        subcommands.add(new RankSetCommand());
        subcommands.add(new RankInfoCommand());
        subcommands.add(new RankCreateCommand());
        subcommands.add(new RankDeleteCommand());
        subcommands.add(new RankEditCommand());

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
                sender.sendMessage(ChatUtil.color("&7Unknown sub-command, use &c/rank help &7for help."));
            }
        } else {
            help(sender);
        }
            /*
            switch (args[0].toLowerCase()) {

                case "edit": {
                    if (args.length > 2) {
                        Document data = Core.i().getMongoManager().getRanksCollections().find(new Document("_id", args[1])).first();
                        if (data == null) {
                            user.msg("&eThat rank doesn't exist.");
                            break;
                        }
                        switch (args[2].toLowerCase()) {
                            case "permission": {

                            }
                        }
                    } else {
                        user.msg("&8&m-------------------------------");
                        user.msg("&6» &e/rank edit <rank> permission <add|remove> <perm>");
                        user.msg("&6» &e/rank edit <rank> parent <add|remove> <parent>");
                        user.msg("&6» &e/rank edit <rank> set <option> <value>");
                        user.msg("&8&m-------------------------------");
                    }
                }
                case "setprefix": {
                    if (args.length > 2) {
                        Document data = Core.i().getMongoManager().getRanksCollections().find(new Document("_id", args[1].toLowerCase())).first();
                        if (data != null) {
                            StringBuilder builder = new StringBuilder(args[2]);
                            for (int i = 3; i < args.length; i++) {
                                builder.append(" ").append(args[i]);
                            }
                            String prefix = builder.toString().replace("\"", "");
                            Document update = new Document("prefix", prefix);
                            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", data.getString("_id")), new Document("$set", update));
                        } else {
                            user.msg("&7That rank doesn't exist.");
                        }
                    } else {
                        user.msg("&eUsage: &7/rank setprefix <rank> <prefix..>");
                    }
                    break;
                }
                case "setcolor": {
                    if (args.length == 3) {
                        Document data = Core.i().getMongoManager().getRanksCollections().find(new Document("_id", args[1].toLowerCase())).first();
                        if (data != null) {
                            String color = args[2].replace("\"", "");
                            Document update = new Document("color", color);
                            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", data.getString("_id")), new Document("$set", update));
                        } else {
                            user.msg("&7That rank doesn't exist.");
                        }
                    } else {
                        user.msg("&eUsage: &7/rank setcolor <rank> <color>");
                    }
                    break;
                }
                case "setpriority": {
                    if (args.length > 2) {
                        Document data = Core.i().getMongoManager().getRanksCollections().find(new Document("_id", args[1].toLowerCase())).first();
                        if (data != null) {
                            int priority;
                            try {
                                priority = Integer.parseInt(args[2]);
                            } catch (NumberFormatException e) {
                                user.msg("&eInvalid integer for priority at arg #2: &7" + args[2]);
                                break;
                            }
                            Document update = new Document("priority", priority);
                            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", data.getString("_id")), new Document("$set", update));
                        } else {
                            user.msg("&7That rank doesn't exist.");
                        }
                    } else {
                        user.msg("&eUsage: &7/rank setpriority <rank> <priority>");
                    }
                    break;
                }
                case "permission": {
                    if (args.length == 4) {
                        Document data = Core.i().getMongoManager().getRanksCollections().find(new Document("_id", args[1].toLowerCase())).first();
                        if (data != null) {
                            Action action;
                            try {
                                action = Action.valueOf(args[2].toUpperCase());
                            } catch (IllegalArgumentException e) {
                                user.msg("&ePlease provide add/remove! provided: &7" + args[2]);
                                break;
                            }
                            String permission = args[3].toLowerCase();
                            List<String> permissions = data.getList("permissions", String.class) == null ? new ArrayList<>() : data.getList("permissions", String.class);
                            if (action == Action.ADD) {
                                if (!permissions.contains(permission)) {
                                    permissions.add(permission);
                                } else {
                                    user.msg("&eThat rank already has permission " + permission + "!");
                                    break;
                                }
                            } else if (action == Action.REMOVE) {
                                if (permissions.contains(permission)) {
                                    permissions.remove(permission);
                                } else {
                                    user.msg("&eThat rank doesn't have permission " + permission + "!");
                                    break;
                                }
                            }
                            Document update = new Document("permissions", permissions);
                            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", args[1].toLowerCase()), new Document("$set", update));
                        } else {
                            user.msg("&7That rank doesn't exist.");
                        }
                    } else {
                        user.msg("&eUsage: &7/rank permission <rank> <add|remove> <permission>");
                    }
                    break;
                }
                case "parent": {
                    if (args.length == 4) {
                        Document data = Core.i().getMongoManager().getRanksCollections().find(new Document("_id", args[1].toLowerCase())).first();
                        if (data != null) {
                            Action action;
                            try {
                                action = Action.valueOf(args[2].toUpperCase());
                            } catch (IllegalArgumentException e) {
                                user.msg("&ePlease provide add/remove! provided: &7" + args[2]);
                                break;
                            }
                            Document parent = Core.i().getMongoManager().getRanksCollections().find(new Document("_id", args[3].toLowerCase())).first();
                            if (parent == null) {
                                user.msg("&7That parent rank doesn't exist.");
                                break;
                            }
                            String id = parent.getString("_id");
                            List<String> parents = data.getList("parents", String.class) == null ? new ArrayList<>() : data.getList("parents", String.class);
                            if (action == Action.ADD) {
                                if (!parents.contains(id)) {
                                    parents.add(id);
                                } else {
                                    user.msg("&eThat rank already has parent " + id + "!");
                                    break;
                                }
                            } else if (action == Action.REMOVE) {
                                if (parents.contains(id)) {
                                    parents.remove(id);
                                } else {
                                    user.msg("&eThat rank doesn't have parent " + id + "!");
                                    break;
                                }
                            }
                            Document update = new Document("parents", parents);
                            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", args[1].toLowerCase()), new Document("$set", update));
                        } else {
                            user.msg("&7That rank doesn't exist.");
                        }
                    } else {
                        user.msg("&eUsage: &7/rank parent <rank> <add|remove> <parent>");
                    }
                    break;
                }
                default: {
                    help(user);
                }
            }

             */

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
