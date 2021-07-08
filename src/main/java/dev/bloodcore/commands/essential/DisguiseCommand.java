package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Disguise;
import dev.bloodcore.etc.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DisguiseCommand extends Command {

    public DisguiseCommand() {
        super("disguise", ImmutableList.of("nick"), "blood.command.disguise");

        new Command("unnick", ImmutableList.of("undisguise"), "blood.command.disguise") {
            @Override
            public List<String> onTabComplete(CommandSender sender, String[] args) {
                return ImmutableList.of();
            }

            @Override
            public void onExecute(CommandSender sender, String[] args) {
                if (sender instanceof Player) {
                    ((Player) sender).performCommand("nick clear");
                }
            }
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;
        User user = Core.i().getUser(sender.getName());
        if (args.length == 0) {
            user.msg("&cUsage: /disguise <name|clear>");
            return;
        }

        String arg = args[0];
        Disguise disguise = user.get("disguise");
        if (arg.equalsIgnoreCase("clear")) {
            if (disguise != null) {
                disguise.remove();
                user.msg("&aYou have cleared your disguise.");
            } else {
                user.msg("&cYou are not disguised.");
            }
        } else {
            user.msg("&7Disguising...");
            if (disguise != null) {
                disguise.remove();
            }
            Core.i().getHttpUtility().getTextureAndSignature(arg, ((texture, signature) -> {
                if (texture == null || signature == null) {
                    user.msg("&cFailed to find &f\"" + arg + "\"'s &cskin.");
                } else {
                    Disguise dis = new Disguise(user.getPlayer(), arg, texture, signature);
                    dis.apply();
                    user.getData().put("disguise", dis);
                    user.msg("&aDisguised as &f\"" + arg + "\"&a!");
                }
            }));
        }
    }

}

