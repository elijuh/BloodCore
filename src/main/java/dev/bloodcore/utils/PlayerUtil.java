package dev.bloodcore.utils;

import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PlayerUtil {

    public List<String> getVisiblePlayers(Player p) {
        List<String> players = new ArrayList<>();
        for (User user : Core.i().getUsers()) {
            if (p.canSee(user.getPlayer())) {
                players.add(user.name());
            }
        }
        return players;
    }

    public List<String> getAllPlayers() {
        List<String> players = new ArrayList<>();
        for (User user : Core.i().getUsers()) {
            players.add(user.name());
        }
        return players;
    }
}
