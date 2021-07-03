package dev.bloodcore.commands.impl;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.model.Filters;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.User;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RankCommand extends Command {
    public RankCommand() {
        super("rank", ImmutableList.of(), "blood.command.rank");
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        List<String> completion = new ArrayList<>();
        switch (args.length) {
            case 1: {
                for (String sub : new String[]{"list", "info", "set", "create", "delete", "setprefix", "setweight", "setcolor", "permission", "parent"}) {
                    if (StringUtil.startsWithIgnoreCase(sub, args[0])) {
                        completion.add(sub);
                    }
                }
                break;
            }
        }
        return completion;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "list": {
                    StringBuilder builder = new StringBuilder("&4All Ranks &7»");
                    List<Map.Entry<String, Rank>> entries = Core.i().getRankManager().getRanks().entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().getWeight())).collect(Collectors.toList());
                    for (int i = entries.size() - 1; i > -1; i--) {
                        Map.Entry<String, Rank> entry = entries.get(i);
                        builder.append("\n&c").append(entry.getKey()).append(" &8(&r").append(entry.getValue().getDisplay()).append("&8) &8[&4").append(entry.getValue().getWeight()).append("&8]");
                    }
                    user.msg("&8&m-------------------------------");
                    user.msg(builder.toString());
                    user.msg("&8&m-------------------------------");
                    break;
                }
                case "info": {
                    if (args.length == 2) {
                        Rank rank = Core.i().getRankManager().getRank(args[1].toLowerCase());
                        if (rank != null) {
                            StringBuilder builder = new StringBuilder("&4Rank Info &7» &r" + rank.getDisplay())
                                    .append("\n&cWeight: &f").append(rank.getWeight())
                                    .append("\n&cPrefix: &f\"").append(rank.getPrefix()).append("PlayerName&f\"")
                                    .append("\n&cColor: &f\"").append(rank.getColor()).append("PlayerName&f\"")
                                    .append("\n&cPermissions:");
                            for (String permission : rank.getPermissions()) {
                                builder.append("\n&c- &7").append(permission);
                            }
                            builder.append("\n&cParents:");
                            for (String parent : rank.getParents()) {
                                builder.append("\n&c- &7").append(parent);
                            }
                            user.msg("&8&m-------------------------------");
                            user.msg(builder.toString());
                            user.msg("&8&m-------------------------------");
                        } else {
                            user.msg("&7That rank does not exist.");
                        }
                    } else {
                        user.msg("&cUsage: &7/rank info <rank>");
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
                                user.msg("&7You have set the rank of &c" + data.getString("name") + " &7to &f" + rank.getDisplay());
                            } else {
                                user.msg("&cThat rank does not exist.");
                            }
                        } else {
                            user.msg("&cThat player has never joined.");
                        }
                    }
                    break;
                }
                case "create": {
                    if (args.length > 2) {
                        String name, display;
                        int weight;
                        if (Core.i().getRankManager().getRank(args[1]) == null) {
                            display = args[1];
                            name = ChatColor.stripColor(ChatUtil.color(display.toLowerCase()));
                            if (!isSafeName(name)) {
                                user.msg("&cInvalid name for rank: &7" + name);
                                break;
                            }
                            try {
                                weight = Integer.parseInt(args[2]);
                            } catch (NumberFormatException e) {
                                user.msg("&cInvalid integer for weight at arg #2: &7" + args[2]);
                                break;
                            }
                            Rank rank = new Rank(name, display, "", "", weight, ImmutableList.of(), ImmutableList.of());
                            Document data = new Document("_id", rank.getId())
                                    .append("display", rank.getDisplay())
                                    .append("prefix", "")
                                    .append("color", "")
                                    .append("weight", weight)
                                    .append("permissions", ImmutableList.of())
                                    .append("parents", ImmutableList.of());
                            Core.i().getMongoManager().getRanksCollections().insertOne(data);
                            user.sound(Sound.ORB_PICKUP, 1f, 1f);
                            user.actionBar("&aRank successfully created.");
                        } else {
                            user.msg("&7A rank with that name already exists.");
                        }
                    } else {
                        user.msg("&cUsage: &7/rank create <rank> <weight>");
                    }
                    break;
                }
                case "delete": {
                    if (args.length == 2) {
                        Rank rank = Core.i().getRankManager().getRank(args[1]);
                        if (rank != null) {
                            Core.i().getMongoManager().getRanksCollections().deleteOne(Filters.eq("_id", rank.getId()));
                            user.actionBar("&aRank successfully removed.");
                        } else {
                            user.msg("&7That rank doesn't exist.");
                        }
                    } else {
                        user.msg("&7Usage: &7/rank delete <rank>");
                    }
                    break;
                }
                case "setprefix": {
                    if (args.length > 2) {
                        Document data = Core.i().getMongoManager().getRanksCollections().find(new Document("_id", args[1].toLowerCase())).first();
                        if (data != null) {
                            StringBuilder builder = new StringBuilder(args[2]);
                            for (int i = 3; i < args.length; i++) {
                                builder.append(" ").append(args[i]);
                            }
                            String prefix = builder.toString();
                            Document update = new Document("prefix", prefix);
                            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", data.getString("_id")), new Document("$set", update));
                        } else {
                            user.msg("&7That rank doesn't exist.");
                        }
                    } else {
                        user.msg("&cUsage: &7/rank setprefix <rank> <prefix..>");
                    }
                    break;
                }
                case "setcolor": {
                    if (args.length == 3) {
                        Document data = Core.i().getMongoManager().getRanksCollections().find(new Document("_id", args[1].toLowerCase())).first();
                        if (data != null) {
                            String color = args[2];
                            Document update = new Document("color", color);
                            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", data.getString("_id")), new Document("$set", update));
                        } else {
                            user.msg("&7That rank doesn't exist.");
                        }
                    } else {
                        user.msg("&cUsage: &7/rank setcolor <rank> <color>");
                    }
                    break;
                }
                case "setweight": {
                    if (args.length > 2) {
                        Document data = Core.i().getMongoManager().getRanksCollections().find(new Document("_id", args[1].toLowerCase())).first();
                        if (data != null) {
                            int weight;
                            try {
                                weight = Integer.parseInt(args[2]);
                            } catch (NumberFormatException e) {
                                user.msg("&cInvalid integer for weight at arg #2: &7" + args[2]);
                                break;
                            }
                            Document update = new Document("weight", weight);
                            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", data.getString("_id")), new Document("$set", update));
                        } else {
                            user.msg("&7That rank doesn't exist.");
                        }
                    } else {
                        user.msg("&cUsage: &7/rank setweight <rank> <weight>");
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
                                user.msg("&cPlease provide add/remove! provided: &7" + args[2]);
                                break;
                            }
                            String permission = args[3].toLowerCase();
                            List<String> permissions = data.getList("permissions", String.class) == null ? new ArrayList<>() : data.getList("permissions", String.class);
                            if (action == Action.ADD) {
                                if (!permissions.contains(permission)) {
                                    permissions.add(permission);
                                } else {
                                    user.msg("&cThat rank already has permission " + permission + "!");
                                    break;
                                }
                            } else if (action == Action.REMOVE) {
                                if (permissions.contains(permission)) {
                                    permissions.remove(permission);
                                } else {
                                    user.msg("&cThat rank doesn't have permission " + permission + "!");
                                    break;
                                }
                            }
                            Document update = new Document("permissions", permissions);
                            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", args[1].toLowerCase()), new Document("$set", update));
                        } else {
                            user.msg("&7That rank doesn't exist.");
                        }
                    } else {
                        user.msg("&cUsage: &7/rank permission <add|remove> <permission>");
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
                                user.msg("&cPlease provide add/remove! provided: &7" + args[2]);
                                break;
                            }
                            String parent = args[3].toLowerCase();
                            List<String> parents = data.getList("parents", String.class) == null ? new ArrayList<>() : data.getList("parents", String.class);
                            if (action == Action.ADD) {
                                if (!parents.contains(parent)) {
                                    parents.add(parent);
                                } else {
                                    user.msg("&cThat rank already has parent " + parent + "!");
                                    break;
                                }
                            } else if (action == Action.REMOVE) {
                                if (parents.contains(parent)) {
                                    parents.remove(parent);
                                } else {
                                    user.msg("&cThat rank doesn't have parent " + parent + "!");
                                    break;
                                }
                            }
                            Document update = new Document("parents", parents);
                            Core.i().getMongoManager().getRanksCollections().updateOne(Filters.eq("_id", args[1].toLowerCase()), new Document("$set", update));
                        } else {
                            user.msg("&7That rank doesn't exist.");
                        }
                    } else {
                        user.msg("&cUsage: &7/rank parent <add|remove> <parent>");
                    }
                    break;
                }
                default: {
                    help(user);
                }
            }
        } else {
            help(user);
        }
    }

    private void help(User user) {
        user.msg("&8&m-------------------------------");
        user.msg("&4Rank Commands &7»");
        user.msg("&c- &7/rank list");
        user.msg("&c- &7/rank info <rank>");
        user.msg("&c- &7/rank set <player> <rank>");
        user.msg("&c- &7/rank create <rank> <weight>");
        user.msg("&c- &7/rank delete <rank>");
        user.msg("&c- &7/rank setprefix <rank> <prefix..>");
        user.msg("&c- &7/rank setweight <rank> <weight>");
        user.msg("&c- &7/rank setcolor <rank> <color>");
        user.msg("&c- &7/rank permission <rank> <add|remove> <permission>");
        user.msg("&c- &7/rank parent <rank> <add|remove> <parent rank>");
        user.msg("&8&m-------------------------------");
    }

    private boolean isSafeName(String name) {
        for (char c : name.toCharArray()) {
            System.out.println(Character.getType(c));
            if (Character.getType(c) != Character.LOWERCASE_LETTER) {
                return false;
            }
        }
        return true;
    }

    private enum Action {
        ADD,
        REMOVE
    }
}
