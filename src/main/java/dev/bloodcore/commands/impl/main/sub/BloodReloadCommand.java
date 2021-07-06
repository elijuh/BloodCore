package dev.bloodcore.commands.impl.main.sub;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.commands.SubCommand;
import dev.bloodcore.etc.Messages;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BloodReloadCommand extends SubCommand {
    public BloodReloadCommand() {
        super("reload", ImmutableList.of(), "blood.admin", "/blood reload");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        long start = System.nanoTime();
        Core.i().reload();
        long end = System.nanoTime();
        double time = Math.round((end - start) / 10000.0) / 100.0;
        sender.sendMessage(ChatUtil.color(Messages.CORE_PREFIX + "&7Configuration reloaded in &a" + time + "ms&7."));
    }
}
