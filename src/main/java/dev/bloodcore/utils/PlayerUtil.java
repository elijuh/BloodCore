package dev.bloodcore.utils;

import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PlayerUtil {
    private final Class<?> CraftPlayer = ReflectionUtil.getCBClass("entity.CraftPlayer");
    private final Class<?> EntityPlayer = ReflectionUtil.getNMSClass("EntityPlayer");

    public int getPing(Player player) {
        try {
            Object craftPlayer = CraftPlayer.cast(player);
            Method getHandleMethod = CraftPlayer.getMethod("getHandle");
            Object entityPlayer = getHandleMethod.invoke(craftPlayer);
            Field pingField = EntityPlayer.getDeclaredField("ping");
            pingField.setAccessible(true);
            return (int) pingField.get(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> getVisiblePlayers(Player p, String prefix) {
        List<String> players = new ArrayList<>();
        for (User user : Core.i().getUsers()) {
            if (p.canSee(user.getPlayer()) && StringUtil.startsWithIgnoreCase(user.name(), prefix)) {
                players.add(user.name());
            }
        }
        return players;
    }

    public List<String> getAllPlayers(String prefix) {
        List<String> players = new ArrayList<>();
        for (User user : Core.i().getUsers()) {
            if (StringUtil.startsWithIgnoreCase(user.name(), prefix)) {
                players.add(user.name());
            }
        }
        return players;
    }
}
