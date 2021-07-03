package dev.bloodcore.ranks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class Rank {
    private final String id;
    private String prefix, color;
    private int priority;
    private Set<String> permissions;
    private Set<String> parents;
}
