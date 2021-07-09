package dev.bloodcore.etc;

import com.google.common.collect.Lists;
import dev.bloodcore.Core;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public enum Config {
    MONGO_CONNECTION("mongo-db.connection-string", ""),
    SERVER_NAME("server-name", "Hub"),
    GLOBAL_CHAT_TIMER("chat.timer", 0),
    GLOBAL_CHAT_FORMAT("chat.format", "%rank_prefix%%player% &8» &r%message%"),
    RULES("rules", Lists.newArrayList("#1", "#2"));

    private final String path;
    private final Object def;

    Config(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    public int getInt() {
        return Core.i().getConfig().getInt(path);
    }

    public String getString() {
        return Core.i().getConfig().getString(path);
    }


    @Override
    public String toString() {
        return getString();
    }

    public List<String> getStrings(){
        return Core.i().getConfig().getStringList(path);
    }
}
