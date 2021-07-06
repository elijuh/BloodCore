package dev.bloodcore.etc;

import dev.bloodcore.Core;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public enum Config {
    MONGO_CONNECTION("mongo-db.connection-string", ""),
    SERVER_NAME("server-name", "Hub"),
    GLOBAL_CHAT_TIMER("chat.timer", 0),
    GLOBAL_CHAT_FORMAT("chat.format", "%rank_prefix%%player% &8» &r%message%");

    private static final FileConfiguration config = Core.i().getConfig();
    private final String path;
    private final Object def;

    Config(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    public int getInt() {
        return config.getInt(path);
    }

    public String getString() {
        return config.getString(path);
    }


    @Override
    public String toString() {
        return getString();
    }
}
