package dev.bloodcore.commands.essential;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.commands.Command;
import dev.bloodcore.etc.Config;
import dev.bloodcore.utils.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RulesCommand extends Command {
    public RulesCommand() {
        super("rules", ImmutableList.of(), null);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {

        return null;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        List<String> rules = Config.RULES.getStrings();
        if (rules.isEmpty()) {
            sender.sendMessage(ChatUtil.color("&cNo rules have been defined yet!"));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String rule : rules) {
            stringBuilder.append(rule).append("\n");
        }
        sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
        sender.sendMessage(ChatUtil.color(stringBuilder.toString()));
        sender.sendMessage(ChatUtil.color("&8&m-------------------------------"));
    }
}
