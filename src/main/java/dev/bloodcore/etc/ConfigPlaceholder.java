package dev.bloodcore.etc;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ConfigPlaceholder {
    GLOBAL_CHAT_MESSAGE("%message%"),
    PREFIX("%rank_prefix%"),
    RANK_COLOR("%rank_color%"),
    PLAYER("%player%"),
    SERVER("%server%"),
    CHAT_COOLDOWN("%chat-cooldown%");

    private final String placeholder;

    public String setPlaceholders(String input, String replace) {
        return input.replace(placeholder, replace);
    }
}
