package dev.bloodcore;

import dev.bloodcore.chat.ChatManager;
import dev.bloodcore.ranks.RankManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Core extends JavaPlugin {
    private static Core instance;

    private ChatManager chatManager;
    private RankManager rankManager;

    public void onLoad() {
        instance = this;
    }

    public void onEnable() {
        chatManager = new ChatManager();
        rankManager = new RankManager();
    }

    public static Core i() {
        return instance;
    }
}
