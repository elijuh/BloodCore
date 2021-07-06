package dev.bloodcore.world;

import dev.bloodcore.Core;
import dev.bloodcore.etc.YamlStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {

    private final YamlStorage worldConfig;
    private final Core core;

    public WorldListener(Core core) {
        this.worldConfig = core.getWorldConfig();
        this.core = core;
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {

        if (worldConfig.getKeys(false).contains(e.getWorld().getName())) {
            System.out.println("eternal doom");
            if (worldConfig.getBoolean(e.getWorld().getName() + ".disableWeather")) {
                if (e.toWeatherState()) {
                    e.setCancelled(true);
                }
            }
        }

    }

}
