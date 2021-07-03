package dev.bloodcore.commands.impl.rank;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.commands.impl.rank.sub.RankInfoCommand;
import dev.bloodcore.commands.impl.rank.sub.RankListCommand;
import dev.bloodcore.commands.impl.rank.sub.RankSetCommand;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RankCommand extends Command {
    private final List<SubCommand> subcommands = new ArrayList<>();

    public RankCommand() {
        super("rank", ImmutableList.of(), "blood.rank.use");
        subcommands.add(new RankListCommand(this));
        subcommands.add(new RankSetCommand(this));
        subcommands.add(new RankInfoCommand(this));

    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        if (args.length == 1) {
            List<String> completion = new ArrayList<>();
            for (SubCommand sub : getAvailableSubs(user.getPlayer())) {
                if (StringUtil.startsWithIgnoreCase(sub.getName(), args[0])) {
                    completion.add(sub.getName());
                }
            }
            return completion;
        } else if (args.length > 1) {
            SubCommand sub = getSubCommand(args[1]);
            if (sub != null) {
                return sub.tabComplete(user.getPlayer(), args);
            }
        }
        return ImmutableList.of();
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        this.execute(sender, args);
    }

    @Override
    public void onExecute(User user, String[] args) {
        this.execute(user.getPlayer(), args);
            /*
            switch (args[0].toLowerCase()) {
                case "info": {
                    if (args.length == 2) {
                        Rank rank = Core.i().getRankManager().getRank(args[1].toLowerCase());
                        if (rank != null) {
                            StringBuilder builder = new StringBuilder("&6&lRank Info &7» &r" + rank.getColoredDisplay())
                                    .append("\n&ePriority: &f").append(rank.getPriority())
                                    .append("\n&ePrefix: &7(&r").append(rank.getPrefix()).append("Player&7)")
                                    .append("\n&eColor: &7(&r").append(rank.getColor()).append("Player&7)")
                                    .append("\n&ePermissions:\n&8» &7");
                            for (String permission : rank.getPermissions()) {
                                builder.append(permission).append(", ");
                            }
                            if (builder.toString().endsWith(", ")) {
                                builder.delete(builder.length() - 2, builder.length());
                            }
                            builder.append("\n&eParents:\n&8» &7");
                            for (String parent : rank.getParents()) {
                                builder.append(parent).append(", ");
                            }
                            if (builder.toString().endsWith(", ")) {
                                builder.delete(builder.length() - 2, builder.length());
                            }
                            user.msg("&8&m-------------------------------");
                            user.msg(builder.toString());
                            user.msg("&8&m-------------------------------");
                        } else {
                            user.msg("&7That rank does not exist.");
                        }
                    } else {
                        user.msg("&eUsage: &7/rank info <rank>");
                    }
                    break;
                }
                case "set": {
                    if (args.length == 3) {
                        Document data = Core.i().getMongoManager().getData(args[1]);
                        if (data != null) {
                            Rank rank = Core.i().getRankManager().getRank(args[2].toLowerCase());
                            if (rank != null) {
                                Document update = new Document("rank", rank.getId());
                                Core.i().getMongoManager().getUsersCollection().updateOne(Filters.eq("uuid", data.getString("uuid")), new Document("$set", update));
                            } else {
                                user.msg("&eThat rank does not exist.");
                            }
                        } else {
                            user.msg("&eThat player has never joined.");
                        }
                    }
                    break;
                }
                case "create": {
                    if (args.length > 2) {
                        String name, display;
                        int priority;
                        if (Core.i().getRankManager().getRank(args[1]) == null) {
                            display = args[1];
                            name = ChatColor.stripColor(ChatUtil.color(display.toLowerCase()));
                            if (!isSafeName(name)) {
                                user.msg("&eInvalid name for rank: &7" + name);
                                break;
                            }
                            try {
                                priority = Integer.parseInt(args[2]);
                            } catch (NumberFormatException e) {
                                user.msg("&eInvalid integer for priority at arg #2: &7" + args[2]);
                                break;
                            }
                            Rank rank = new Rank(name, display, "", "", priority, new HashSet<>(), new HashSet<>());
                            Document data = new Document("_id", rank.getId())
                                    .append("display", rank.getDisplay())
                                    .append("prefix", "")
                                    .append("color", "")
                                    .append("priority", priority)
                                    .append("permissions", new HashSet<>())
                                    .append("parents", new HashSet<>());
                            Core.i().getMongoManager().getRanksCollections().insertOne(data);
                            user.sound(Sound.ORB_PICKUP, 1f, 1f);
                            user.actionBar("&aRank successfully created.");
                        } else {
                            user.msg("&7A rank with that name already exists.");
                        }
                    } else {
                        user.msg("&eUsage: &7/rank create <rank> <priority>");
                    }
                    break;
                }
                case "delete": {
                    if (args.length == 2) {
                        Rank rank = Core.i().getRankManager().getRank(args[1]);
                        if (rank == null) {
                            user.msg("&7That rank doesn't exist.");
                        } else if (rank.getId().equals("default")) {
                            user.msg("&7You cannot delete the default rank.");
                        } else {
                            Core.i().getMongoManager().getRanksCollections().deleteOne(Filters.eq("_id", rank.getId()));
                            user.actionBar("&aRank successfully removed.");
                        }
                    } else {
                        user.msg("&7Usage: &7/rank delete <rank>");
                    }
                    break;
                }
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

    private void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            SubCommand sub = getSubCommand(args[0].toLowerCase());
            if (sub != null) {
                sub.execute(sender, args);
            } else {
                sender.sendMessage(ChatUtil.color("&7Unknown sub-command, use &c/rank help &7for help."));
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
        return subcommands.stream()
                .filter(sub -> sub.getName().equals(name) || sub.getAliases().contains(name))
                .findFirst().orElse(null);
    }

    private enum Action {
        ADD,
        REMOVE
    }
}
