package dev.bloodcore.db;

import dev.bloodcore.Core;
import dev.bloodcore.etc.Config;
import dev.bloodcore.listeners.RedisListener;
import lombok.Getter;
import redis.clients.jedis.Jedis;

@Getter
public class RedisManager {
    private final Core plugin = Core.i();
    private Jedis subJedis, pubJedis;

    public RedisManager() {
        String host = Config.REDIS_HOST.getString();
        int port = Config.REDIS_PORT.getInt();
        String password = Config.REDIS_PASSWORD.getString();

        new Thread(()-> {
            subJedis = new Jedis(host, port);
            subJedis.auth(password);
            pubJedis = new Jedis(host, port);
            pubJedis.auth(password);
            subJedis.subscribe(new RedisListener(), "MESSAGING");
        }).start();
    }

    public void shutdown() {
        subJedis.getClient().shutdown();
        pubJedis.getClient().shutdown();
    }
}
