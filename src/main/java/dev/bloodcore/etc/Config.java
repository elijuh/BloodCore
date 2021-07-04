package dev.bloodcore.etc;

import dev.bloodcore.Core;
import lombok.Getter;

import java.util.List;

@Getter
public enum Config {
    CORE_PREFIX("prefix", "&6&lCore &8» "),
    SERVER_NAME("server-name", "Hub"),
    CHAT_COOLDOWN("chat-manager.cooldown-seconds", 3),
    CHAT_COOLDOWN_MSG("chat-manager.cooldown-message", "&cPlease wait &7%chat-cooldown%s &cbefore sending another msg."),
    GLOBAL_CHAT_FORMAT("chat-format", "%rank_prefix%%player% &8» &r%message%"),
    FLY_TOGGLE_SELF("commands.fly.toggle-self", "&eYou've %state% flight"),
    FLY_TOGGLE_OTHER("commands.fly.toggle-other", "&eYou've %state% fly for %target_prefix%%target%"),

    FLY_TOGGLE_OTHER_RECEIVER("commands.fly.toggle-other-receiver", "&eYour fly was %state% by %sender_prefix%%sender%");
    private static final YamlStorage messages = Core.i().getMessages();
    private final String path;
    private final Object def;

    Config(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    public List<String> getStringList() {
        return messages.getStringList(path);
    }

    public int getInt() {
        return messages.getInt(path);
    }

    public long getLong() {
        return messages.getLong(path);
    }

    public double getDouble() {
        return messages.getDouble(path);
    }

    public String getString() {
        return messages.getString(path);
    }


    @Override
    public String toString() {
        return getString();
    }
}
