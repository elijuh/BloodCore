package dev.bloodcore.etc;

import com.google.common.collect.Lists;
import dev.bloodcore.Core;
import lombok.Getter;

import java.util.List;

@Getter
public enum Config {
    LICENSE("license", "LicenseGoesHere"),
    MONGO_CONNECTION("mongo-db.connection-string", ""),
    REDIS_HOST("redis.host", ""),
    REDIS_PORT("redis.port", 6379),
    REDIS_PASSWORD("redis.password", ""),
    SERVER_NAME("server-name", "Hub"),
    GLOBAL_CHAT_TIMER("chat.timer", 0),
    GLOBAL_CHAT_FORMAT("chat.format", "%rank_prefix%%player% &8» &r%message%"),
    GLOBAL_CHAT_COOLDOWN_MESSAGE("chat.cooldown", "&cYou are on cooldown for &e%delay% &cmore seconds!"),
    MESSAGE_SOUND_ENABLED("messages.sound.enabled", true),
    MESSAGE_SOUND_ENUM("messages.sound.value", "ORB_PICKUP"),
    MESSAGE_SOUND_VOLUME("messages.sound.volume", 1f),
    MESSAGE_SOUND_PITCH("messages.sound.pitch", 1f),
    RULES("rules", Lists.newArrayList("line 1", "line 2")),
    SPAWN_ON_JOIN("spawn_on_join", true),
    JOIN_MESSAGE("join-message", "%prefix%%player% &ahas joined the server."),
    QUIT_MESSAGE("quit-message", "%prefix%%player% &chas left the server.");

    private final String path;
    private final Object def;

    Config(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    public int getInt() {
        return Core.i().getConfig().getInt(path);
    }

    public float getFloat() {
        return (float) getDouble();
    }

    public double getDouble() {
        return Core.i().getConfig().getDouble(path);
    }

    public String getString() {
        return Core.i().getConfig().getString(path);
    }

    public boolean getBoolean(){
        return Core.i().getConfig().getBoolean(path);
   //     return true;
    }
    @Override
    public String toString() {
        return getString();
    }

    public List<String> getStrings(){
        return Core.i().getConfig().getStringList(path);
    }
}
