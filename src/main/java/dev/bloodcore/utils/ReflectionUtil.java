package dev.bloodcore.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@UtilityClass
public class ReflectionUtil {
    private static final String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);

    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class<?> getCBClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void unregisterCommands(CommandMap map, String removing) {
        unregisterCommands(map, Collections.singleton(removing));
    }

    @SuppressWarnings("unchecked")
    public void unregisterCommands(CommandMap map, Collection<String> removing) {
        try {
            Field field = getField(map.getClass(), "knownCommands") != null ? getField(map.getClass(), "knownCommands")
                    : getField(map.getClass().getSuperclass(), "knownCommands");
            Map<String, Command> commands = (Map<String, Command>) field.get(map);

            for (String command : removing) {
                commands.remove(command);
            }

            field.set(map, commands);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
