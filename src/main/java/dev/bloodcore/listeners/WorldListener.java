package dev.bloodcore.listeners;

import dev.bloodcore.Core;
import dev.bloodcore.etc.YamlStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {

    private final YamlStorage worldConfig;

    public WorldListener() {
        this.worldConfig = Core.i().getWorldConfig();
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        if (worldConfig.getBoolean(e.getWorld().getName() + ".disableWeather") && e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

}
