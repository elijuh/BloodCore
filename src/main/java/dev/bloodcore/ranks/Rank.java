package dev.bloodcore.ranks;

import dev.bloodcore.Core;
import dev.bloodcore.utils.ChatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.HashSet;
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

    public Set<String> gettAllParents() {
        Set<String> parents = new HashSet<>();
        for (String parent : this.parents) {
            parents.add(parent);
            Rank parentRank = Core.i().getRankManager().getRank(parent);
            if (parentRank != null) {
                parents.addAll(parentRank.parents);
            }
        }

        return parents;
    }

    public String getColor() {
        return ChatColor.getLastColors(ChatUtil.color(prefix));
    }
}
