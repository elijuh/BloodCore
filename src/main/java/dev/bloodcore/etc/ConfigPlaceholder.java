package dev.bloodcore.etc;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ConfigPlaceholder {
    PLAYER("%player%"),
    SERVER("%server%"),
    CHAT_COOLDOWN("%chat-cooldown%");

    private final String placeholder;

    public void setPlaceholders(String input, String replace) {
        input.replace(placeholder, replace);
    }
}
