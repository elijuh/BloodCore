package dev.bloodcore.etc;

import com.google.common.collect.ImmutableMap;
import dev.bloodcore.Core;
import lombok.Getter;

import java.util.Map;

@Getter
public enum Config {
    SERVER_NAME("server-name", "Hub"),
    CHAT_COOLDOWN("chat-manager.cooldown-seconds", 3),
    CHAT_COOLDOWN_MSG("chat-manager.cooldown-message", "&cPlease wait &7%chat-cooldown%s &cbefore sending another msg.");

    private final String path;
    private final Object def;

    Config(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    public int getInt() {
        return Core.i().getConfig().contains(path) ? Core.i().getConfig().getInt(path) : (int) def;
    }

    public long getLong() {
        return Core.i().getConfig().contains(path) ? Core.i().getConfig().getLong(path) : (long) def;
    }

    public double getDouble() {
        return Core.i().getConfig().contains(path) ? Core.i().getConfig().getDouble(path) : (double) def;
    }

    public String getString() {
        return getString(ImmutableMap.of());
    }

    public String getString(Map<ConfigPlaceholder, String> placeholders) {
        String string = Core.i().getConfig().contains(path) ? Core.i().getConfig().getString(path) : (String) def;
        for (Map.Entry<ConfigPlaceholder, String> entry : placeholders.entrySet()) {
            entry.getKey().setPlaceholders(string, entry.getValue());
        }
        return string;
    }

}
