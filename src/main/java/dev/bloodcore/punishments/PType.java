package dev.bloodcore.punishments;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PType {
    WARN("Warn"),
    KICK("Kick"),
    MUTE("Mute"),
    BAN("Ban"),
    IPBAN("IP Ban");

    private final String display;
}
