package dev.bloodcore.commands.impl;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.commands.Command;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RankCommand extends Command {
    public RankCommand() {
        super("rank", ImmutableList.of("ranks"), "blood.command.rank");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        List<String> completion = new ArrayList<>();
        switch (args.length) {
            case 1: {
                for (String sub : new String[]{"list", "set", "create", "delete", "setprefix", "setsuffix", "setweight", "parent"}) {
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
    public void onExecute(Player p, String[] args) {
        switch (args.length) {
            case 1: {

            }
            default: {
                help(p);
            }
        }
    }

    private void help(Player p) {
        p.sendMessage(ChatUtil.color("&8&m-------------------------------"));
        p.sendMessage(ChatUtil.color("&4Rank Commands &7»"));
        p.sendMessage(ChatUtil.color("&7- &c/rank list"));
        p.sendMessage(ChatUtil.color("&7- &c/rank set <player> <rank>"));
        p.sendMessage(ChatUtil.color("&7- &c/rank create <rank>"));
        p.sendMessage(ChatUtil.color("&7- &c/rank delete <rank>"));
        p.sendMessage(ChatUtil.color("&7- &c/rank setprefix <rank> <prefix>"));
        p.sendMessage(ChatUtil.color("&7- &c/rank setsuffix <rank> <suffix>"));
        p.sendMessage(ChatUtil.color("&7- &c/rank setweight <rank> <weight>"));
        p.sendMessage(ChatUtil.color("&7- &c/rank parent <add|remove> <rank>"));
        p.sendMessage(ChatUtil.color("&8&m-------------------------------"));
    }
}
