package dev.bloodcore.ranks;

import dev.bloodcore.utils.ChatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class Rank {
    private final String id;
    private String prefix;
    private int priority;
    private Set<String> permissions;
    private Set<String> parents;

    public String getColor() {
        return ChatColor.getLastColors(ChatUtil.color(prefix));
    }
}
