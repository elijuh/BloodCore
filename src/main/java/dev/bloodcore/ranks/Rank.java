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
    private final String id;
    private String display;
    private String prefix, color;
    private int weight;
    private List<String> permissions;
    private List<String> parents;

    public String getDisplay() {
        return ChatUtil.color(color + display);
    }
}
