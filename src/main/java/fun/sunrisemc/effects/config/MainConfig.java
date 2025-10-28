package fun.sunrisemc.effects.config;

import org.bukkit.configuration.file.YamlConfiguration;

import fun.sunrisemc.effects.file.ConfigFile;

public class MainConfig {

    public final int CHECK_INTERVAL_TICKS;

    public MainConfig() {
        YamlConfiguration config = ConfigFile.get("config", true);

        this.CHECK_INTERVAL_TICKS = getIntClamped(config, "check-interval-ticks", 1, 1_000_000);
    }

    private int getIntClamped(YamlConfiguration config, String path, int min, int max) {
        int value = config.getInt(path);
        return Math.clamp(value, min, max);
    }
}