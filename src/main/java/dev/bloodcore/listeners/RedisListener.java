package dev.bloodcore.listeners;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import redis.clients.jedis.JedisPubSub;

public class RedisListener extends JedisPubSub {
    private final JsonParser parser = new JsonParser();

    @Override
    public void onMessage(String channel, String message) {
        switch (channel) {
            case "MESSAGING": {
                JsonObject json = parser.parse(message).getAsJsonObject();
                for (User user : Core.i().getUsers()) {
                    if (json.has("permission") && user.getPlayer().hasPermission(json.get("permission").getAsString())) {
                        user.msg(json.get("message").getAsString());
                    } else if (json.has("to") && user.uuid().equals(json.get("to").getAsString())) {
                        user.msg(json.get("message").getAsString());
                    }
                }
                break;
            }
        }
    }
}
