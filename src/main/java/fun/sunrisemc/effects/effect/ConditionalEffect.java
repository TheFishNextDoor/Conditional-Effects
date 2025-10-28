package fun.sunrisemc.effects.effect;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ConditionalEffect {

    private final String id;

    private HashSet<String> worlds = new HashSet<>();
    private HashSet<String> environments = new HashSet<>();
    private HashSet<String> biomes = new HashSet<>();

    private Integer minX = null;
    private Integer maxX = null;
    private Integer minY = null;
    private Integer maxY = null;
    private Integer minZ = null;
    private Integer maxZ = null;

    ConditionalEffect(YamlConfiguration config, String id) {
        this.id = id;

        for (String worldName : config.getStringList(id + ".worlds")) {
            this.worlds.add(worldName);
        }

        for (String environmentName : config.getStringList(id + ".environments")) {
            this.environments.add(environmentName);
        }

        for (String biomeName : config.getStringList(id + ".biomes")) {
            this.biomes.add(biomeName);
        }

        if (config.contains(id + ".min-x")) {
            this.minX = config.getInt(id + ".min-x");
        }
        if (config.contains(id + ".max-x")) {
            this.maxX = config.getInt(id + ".max-x");
        }
        if (config.contains(id + ".min-y")) {
            this.minY = config.getInt(id + ".min-y");
        }
        if (config.contains(id + ".max-y")) {
            this.maxY = config.getInt(id + ".max-y");
        }
        if (config.contains(id + ".min-z")) {
            this.minZ = config.getInt(id + ".min-z");
        }
        if (config.contains(id + ".max-z")) {
            this.maxZ = config.getInt(id + ".max-z");
        }
    }

    public String getId() {
        return id;
    }

    public boolean conditionsMet(Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();

        if (!worlds.isEmpty() && !worlds.contains(world.getName())) {
            return false;
        }

        if (!environments.isEmpty() && !environments.contains(world.getEnvironment().name())) {
            return false;
        }

        if (!biomes.isEmpty() && !biomes.contains(location.getBlock().getBiome().name())) {
            return false;
        }

        if (minX != null && location.getBlockX() < minX) {
            return false;
        }
        if (minY != null && location.getBlockY() < minY) {
            return false;
        }
        if (minZ != null && location.getBlockZ() < minZ) {
            return false;
        }
        if (maxX != null && location.getBlockX() > maxX) {
            return false;
        }
        if (maxY != null && location.getBlockY() > maxY) {
            return false;
        }
        if (maxZ != null && location.getBlockZ() > maxZ) {
            return false;
        }

        return true;
    }

    public void applyEffects(Player player) {
        // Placeholder for applying effects to the player
    }
}