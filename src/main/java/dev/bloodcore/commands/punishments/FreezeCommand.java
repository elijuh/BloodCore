package dev.bloodcore.commands.punishments;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.commands.essential.MessageCommand;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.PlayerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FreezeCommand extends Command {
    public FreezeCommand() {
        super("freeze", ImmutableList.of("ss"), "blood.command.freeze");
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
        if (args.length == 1) {
            User user = Core.i().getUser(args[0]);
            if (user == null) {
                sender.sendMessage(ChatUtil.color("&cThat player is not online!"));
                return;
            }

            if (user.isFrozen()) {
                user.getPlayer().sendMessage(ChatUtil.color("&aYou're no longer frozen!"));
                sender.sendMessage(ChatUtil.color("&aYou've unfrozen " + user.name()));

                user.setFrozen(false);
            } else {
                sender.sendMessage(ChatUtil.color("&aYou've frozen " + user.name()));
                user.setFrozen(true);
                StringBuilder stringBuilder = new StringBuilder();
                String[] values = new String[]{"&f████&c█&f████", "&f███&c█&6█&c█&f███ &4&lDo NOT log out!", "&f██&c█&6█&0█&6█&c█&f██ &cIf you do, you will be banned!", "&f██&c█&6█&0█&6█&c█&f██  &ePlease connect to the waiting room below", "&f█&c█&6██&0█&6██&c█&f█ &e%frozen-link%", "&f█&c█&6█████&c█&f█", "&c█&6███&0█&6███&c█", "&c█████████"};
                for (String value : values) {
                    stringBuilder.append(value).append("\n");
                }

                String reason = stringBuilder.toString().replace("%frozen-link%", Messages.FREEZE_LINK.getString());;
                //todo reason.replace

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!user.isFrozen()) {
                            cancel();
                        } else {
                            user.msg(reason);
                        }
                    }
                }.runTaskTimer(Core.i(), 0L, 200L);
            }


        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /freeze <player>"));
        }
    }
}
