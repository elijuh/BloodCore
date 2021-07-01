package dev.bloodcore.chat;

import dev.bloodcore.etc.Config;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatManager {
    private int cooldown = Config.CHAT_COOLDOWN.getInt();
    private boolean muted;

    public void reload() {
        cooldown = Config.CHAT_COOLDOWN.getInt();
    }
}
