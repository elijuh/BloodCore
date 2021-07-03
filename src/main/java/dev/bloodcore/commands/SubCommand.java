package dev.bloodcore.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.List;

@Getter
public abstract class SubCommand {
    private final Command parent;
    private final String name, permission, usage;
    private final List<String> aliases;

    public SubCommand(Command parent, String name, List<String> aliases, String permission, String usage) {
        this.parent = parent;
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.usage = usage;
    }

    public abstract List<String> tabComplete(CommandSender sender, String[] args);

    public abstract void execute(CommandSender sender, String[] args);
}
