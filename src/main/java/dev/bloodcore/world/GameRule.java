package dev.bloodcore.world;

import org.bukkit.Bukkit;

public class GameRule {
    public GameRule(){
        for (String gameRule : Bukkit.getWorlds().get(0).getGameRules()) {
            
        }
    }
}
