package dev.bloodcore.chat;

import dev.bloodcore.Core;
import dev.bloodcore.etc.Config;
import dev.bloodcore.etc.Messages;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatManager {
    private int cooldown = Config.GLOBAL_CHAT_TIMER.getInt();
    private boolean muted;

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
        Core.i().getConfig().set(Config.GLOBAL_CHAT_TIMER.getPath(), cooldown);
    }

    public void reload() {
        cooldown = Config.GLOBAL_CHAT_TIMER.getInt();
    }
}
