package dev.bloodcore.etc;

import dev.bloodcore.Core;

public enum Config {
    SERVER_NAME("server-name", "Hub"),
    CHAT_COOLDOWN("chat-manager.cooldown-seconds", 3);

    private final String path;
    private final Object def;

    Config(String path, Object def) {
        this.path = path;
        this.def = def;
        Core.i().getConfig().addDefault(path, def);
        Core.i().saveConfig();
    }

    public int getInt() {
        return Core.i().getConfig().contains(path) ? Core.i().getConfig().getInt(path) : (int) def;
    }

    public long getLong() {
        return Core.i().getConfig().contains(path) ? Core.i().getConfig().getLong(path) : (long) def;
    }

    public double getDouble() {
        return Core.i().getConfig().contains(path) ? Core.i().getConfig().getDouble(path) : (double) def;
    }

    public String getString() {
        return Core.i().getConfig().contains(path) ? Core.i().getConfig().getString(path) : (String) def;
    }

}
