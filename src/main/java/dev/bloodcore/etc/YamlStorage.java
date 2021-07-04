package dev.bloodcore.etc;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class YamlStorage {
    private final File file;
    private FileConfiguration config;

    public YamlStorage(File file) {
        this.file = file;
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        reload();
    }

    public void copyDefaults() {
        config.options().copyDefaults(true);
    }

    public void addDefault(String path, Object value) {
        config.addDefault(path, value);
    }

    public Object get(String path) {
        return config.get(path);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        Bukkit.getLogger().info(file.getName() + " successfully loaded.");
    }
}
