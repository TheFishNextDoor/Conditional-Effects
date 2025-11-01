package fun.sunrisemc.effects.conditional_effect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.effects.ConditionalEffectsPlugin;
import fun.sunrisemc.effects.file.ConfigFile;

public class ConditionalEffect {

    private final List<String> SETTINGS = List.of(
        "conditions-check-interval-ticks",
        "effects",
        "conditions"
    );

    private final List<String> CONDITIONS = List.of(
        "worlds",
        "environments",
        "biomes",
        "gamemodes",
        "has-permissions",
        "lacks-permissions",
        "min-x",
        "min-y",
        "min-z",
        "max-x",
        "max-y",
        "max-z",
        "in-water",
        "not-in-water"
    );

    private final String id;

    // Behavior Settings

    private int checkIntervalTicks = 40;

    private ArrayList<PotionEffect> effects = new ArrayList<>();

    // Condition Settings

    private HashSet<String> worlds = new HashSet<>();
    private HashSet<String> environments = new HashSet<>();
    private HashSet<String> biomes = new HashSet<>();
    private HashSet<String> gamemodes = new HashSet<>();

    private ArrayList<String> hasPermissions = new ArrayList<>();
    private ArrayList<String> lacksPermissions = new ArrayList<>();

    private Integer minX = null;
    private Integer maxX = null;
    private Integer minY = null;
    private Integer maxY = null;
    private Integer minZ = null;
    private Integer maxZ = null;

    private boolean inWater = false;
    private boolean notInWater = false;

    ConditionalEffect(@NonNull YamlConfiguration config, @NonNull String id) {
        this.id = id;

        // Settings Checks

        for (String setting : config.getConfigurationSection(id).getKeys(false)) {
            if (!SETTINGS.contains(setting)) {
                ConditionalEffectsPlugin.logWarning("Invalid setting for effect " + id + ": " + setting + ".");
                ConditionalEffectsPlugin.logWarning("Valid settings are: " + String.join(", ", SETTINGS) + ".");
            }
        }

        if (config.contains(id + ".conditions")) {
            for (String condition : config.getConfigurationSection(id + ".conditions").getKeys(false)) {
                if (!CONDITIONS.contains(condition)) {
                    ConditionalEffectsPlugin.logWarning("Invalid condition for effect " + id + ": " + condition + ".");
                    ConditionalEffectsPlugin.logWarning("Valid conditions are: " + String.join(", ", CONDITIONS) + ".");
                }
            }
        }

        // Load Settings

        if (config.contains(id + ".conditions-check-interval-ticks")) {
            this.checkIntervalTicks = ConfigFile.getIntClamped(config, id + ".conditions-check-interval-ticks", 0, Integer.MAX_VALUE);
        }

        for (String effectString : config.getStringList(id + ".effects")) {
            String[] effectStringParts = effectString.split(",");

            if (effectStringParts.length < 1) {
                continue;
            }

            String effectName = effectStringParts[0].trim().toUpperCase();
            PotionEffectType effectType = PotionEffectType.getByName(effectName);
            int effectAmplifier = 0;
            int effectDurationTicks = this.checkIntervalTicks + 10;
            boolean hideParticles = true;
            boolean showIcon = true;

            if (effectType == null) {
                ConditionalEffectsPlugin.logWarning("Invalid potion effect " + effectName + " in conditional effect " + id + ".");
                ConditionalEffectsPlugin.logWarning("Expected format: <EffectType>, [Amplifier], [Duration], [HideParticles], [ShowIcon]");
                continue;
            }

            if (effectStringParts.length >= 2) {
                String amplifierString = effectStringParts[1].trim();
                try {
                    effectAmplifier = Integer.parseInt(amplifierString);
                } catch (NumberFormatException e) {
                    ConditionalEffectsPlugin.logWarning("Invalid potion effect amplifier " + amplifierString + " in conditional effect " + id + ".");
                    ConditionalEffectsPlugin.logWarning("Expected format: <EffectType>, [Amplifier], [Duration], [HideParticles], [ShowIcon]");
                    continue;
                }
            }

            if (effectStringParts.length >= 3) {
                String durationString = effectStringParts[2].trim();
                try {
                    effectDurationTicks = Integer.parseInt(durationString);
                } catch (NumberFormatException e) {
                    ConditionalEffectsPlugin.logWarning("Invalid potion effect duration " + durationString + " in conditional effect " + id + ".");
                    ConditionalEffectsPlugin.logWarning("Expected format: <EffectType>, [Amplifier], [Duration], [HideParticles], [ShowIcon]");
                    continue;
                }
            }

            if (effectStringParts.length >= 4) {
                hideParticles = Boolean.parseBoolean(effectStringParts[3].trim());
            }

            if (effectStringParts.length >= 5) {
                showIcon = Boolean.parseBoolean(effectStringParts[4].trim());
            }

            PotionEffect effect = new PotionEffect(effectType, effectDurationTicks, effectAmplifier, hideParticles, showIcon);
            this.effects.add(effect);
        }

        // Load Conditions

        for (String worldName : config.getStringList(id + ".conditions.worlds")) {
            this.worlds.add(worldName);
        }

        for (String environmentName : config.getStringList(id + ".conditions.environments")) {
            this.environments.add(normalizeName(environmentName));
        }

        for (String biomeName : config.getStringList(id + ".conditions.biomes")) {
            this.biomes.add(normalizeName(biomeName));
        }

        for (String gamemode : config.getStringList(id + ".conditions.gamemodes")) {
            this.gamemodes.add(normalizeName(gamemode));
        }

        for (String permission : config.getStringList(id + ".conditions.has-permissions")) {
            this.hasPermissions.add(permission);
        }

        for (String permission : config.getStringList(id + ".conditions.lacks-permissions")) {
            this.lacksPermissions.add(permission);
        }

        if (config.contains(id + ".conditions.min-x")) {
            this.minX = config.getInt(id + ".conditions.min-x");
        }
        if (config.contains(id + ".conditions.max-x")) {
            this.maxX = config.getInt(id + ".conditions.max-x");
        }
        if (config.contains(id + ".conditions.min-y")) {
            this.minY = config.getInt(id + ".conditions.min-y");
        }
        if (config.contains(id + ".conditions.max-y")) {
            this.maxY = config.getInt(id + ".conditions.max-y");
        }
        if (config.contains(id + ".conditions.min-z")) {
            this.minZ = config.getInt(id + ".conditions.min-z");
        }
        if (config.contains(id + ".conditions.max-z")) {
            this.maxZ = config.getInt(id + ".conditions.max-z");
        }

        if (config.contains(id + ".conditions.in-water")) {
            this.inWater = config.getBoolean(id + ".conditions.in-water");
        }
        if (config.contains(id + ".conditions.not-in-water")) {
            this.notInWater = config.getBoolean(id + ".conditions.not-in-water");
        }
    }

    public String getId() {
        return id;
    }

    public boolean checkInterval(int tickCount) {
        if (checkIntervalTicks <= 0) {
            return false;
        }
        return tickCount % checkIntervalTicks == 0;
    }

    public boolean conditionsMet(@NonNull Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();
        Block block = location.getBlock();

        if (!worlds.isEmpty() && !worlds.contains(world.getName())) {
            return false;
        }

        if (!environments.isEmpty() && !environments.contains(normalizeName(world.getEnvironment().name()))) {
            return false;
        }

        if (!biomes.isEmpty() && !biomes.contains(normalizeName(block.getBiome().name()))) {
            return false;
        }

        if (!gamemodes.isEmpty() && !gamemodes.contains(normalizeName(player.getGameMode().name()))) {
            return false;
        }

        for (String permission : hasPermissions) {
            if (!player.hasPermission(permission)) {
                return false;
            }
        }

        for (String permission : lacksPermissions) {
            if (player.hasPermission(permission)) {
                return false;
            }
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

        if (inWater && player.isInWater()) {
            return false;
        }
        if (notInWater && !player.isInWater()) {
            return false;
        }

        return true;
    }

    public void applyEffects(@NonNull Player player) {
        player.addPotionEffects(effects);
    }

    private String normalizeName(@NonNull String biomeName) {
        return biomeName.trim().toUpperCase().replace(" ", "_").replace("-", "_");
    }
}