package dev.bloodcore.ranks;

import dev.bloodcore.utils.ChatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Rank {
    private final String name;
    private String prefix, color;
    private int weight;
    private List<String> permissions;

    public String getDisplay() {
        return ChatUtil.color(color + name);
    }
}
