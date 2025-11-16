package fun.sunrisemc.conditional_effects.conditional_effect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.conditional_effects.ConditionalEffectsPlugin;
import fun.sunrisemc.conditional_effects.utils.ConfigUtils;

public class ConditionalEffect {

    private final @NotNull List<String> SETTINGS = List.of(
        "conditions-check-interval-ticks",
        "effects",
        "conditions"
    );

    private final @NotNull List<String> CONDITIONS = List.of(
        "worlds",
        "environments",
        "biomes",
        "gamemodes",
        "has-permissions",
        "missing-permissions",
        "min-x",
        "min-y",
        "min-z",
        "max-x",
        "max-y",
        "max-z",
        "in-water",
        "not-in-water"
    );

    private final @NotNull String id;

    // Behavior Settings

    private int checkIntervalTicks = 40;

    private @NotNull ArrayList<PotionEffect> effects = new ArrayList<>();

    // Condition Settings

    private @NotNull HashSet<String> worlds = new HashSet<>();
    private @NotNull HashSet<String> environments = new HashSet<>();
    private @NotNull HashSet<String> biomes = new HashSet<>();
    private @NotNull HashSet<String> gamemodes = new HashSet<>();

    private @NotNull ArrayList<String> hasPermissions = new ArrayList<>();
    private @NotNull ArrayList<String> missingPermissions = new ArrayList<>();

    private Optional<Integer> minX = Optional.empty();
    private Optional<Integer> maxX = Optional.empty();
    private Optional<Integer> minY = Optional.empty();
    private Optional<Integer> maxY = Optional.empty();
    private Optional<Integer> minZ = Optional.empty();
    private Optional<Integer> maxZ = Optional.empty();

    private boolean inWater = false;
    private boolean notInWater = false;

    ConditionalEffect(@NotNull YamlConfiguration config, @NotNull String id) {
        this.id = id;

        // Settings Checks

        ConfigurationSection settingsConfig = config.getConfigurationSection(id);
        if (settingsConfig != null) {
            for (String setting : settingsConfig.getKeys(false)) {
                if (!SETTINGS.contains(setting)) {
                    ConditionalEffectsPlugin.logWarning("Invalid setting for effect " + id + ": " + setting + ".");
                    ConditionalEffectsPlugin.logWarning("Valid settings are: " + String.join(", ", SETTINGS) + ".");
                }
            }
        }

        if (config.contains(id + ".conditions")) {
            ConfigurationSection conditionsConfig = config.getConfigurationSection(id + ".conditions");
            if (conditionsConfig != null) {
                for (String condition : conditionsConfig.getKeys(false)) {
                    if (!CONDITIONS.contains(condition)) {
                        ConditionalEffectsPlugin.logWarning("Invalid condition for effect " + id + ": " + condition + ".");
                        ConditionalEffectsPlugin.logWarning("Valid conditions are: " + String.join(", ", CONDITIONS) + ".");
                    }
                }
            }
        }

        // Load Settings

        if (config.contains(id + ".conditions-check-interval-ticks")) {
            this.checkIntervalTicks = ConfigUtils.getIntClamped(config, id + ".conditions-check-interval-ticks", 0, Integer.MAX_VALUE);
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

        for (String permission : config.getStringList(id + ".conditions.missing-permissions")) {
            this.missingPermissions.add(permission);
        }

        if (config.contains(id + ".conditions.min-x")) {
            this.minX = Optional.of(config.getInt(id + ".conditions.min-x"));
        }
        if (config.contains(id + ".conditions.max-x")) {
            this.maxX = Optional.of(config.getInt(id + ".conditions.max-x"));
        }
        if (config.contains(id + ".conditions.min-y")) {
            this.minY = Optional.of(config.getInt(id + ".conditions.min-y"));
        }
        if (config.contains(id + ".conditions.max-y")) {
            this.maxY = Optional.of(config.getInt(id + ".conditions.max-y"));
        }
        if (config.contains(id + ".conditions.min-z")) {
            this.minZ = Optional.of(config.getInt(id + ".conditions.min-z"));
        }
        if (config.contains(id + ".conditions.max-z")) {
            this.maxZ = Optional.of(config.getInt(id + ".conditions.max-z"));
        }

        if (config.contains(id + ".conditions.in-water")) {
            this.inWater = config.getBoolean(id + ".conditions.in-water");
        }
        if (config.contains(id + ".conditions.not-in-water")) {
            this.notInWater = config.getBoolean(id + ".conditions.not-in-water");
        }
    }

    @NotNull
    public String getId() {
        return id;
    }

    public boolean checkInterval(int tickCount) {
        if (checkIntervalTicks <= 0) {
            return false;
        }
        return tickCount % checkIntervalTicks == 0;
    }

    public boolean conditionsMet(@NotNull Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();
        Block block = location.getBlock();

        if (world == null) {
            return false;
        }

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

        for (String permission : missingPermissions) {
            if (player.hasPermission(permission)) {
                return false;
            }
        }

        if (minX.isPresent() && location.getBlockX() < minX.get()) {
            return false;
        }
        if (minY.isPresent() && location.getBlockY() < minY.get()) {
            return false;
        }
        if (minZ.isPresent() && location.getBlockZ() < minZ.get()) {
            return false;
        }
        if (maxX.isPresent() && location.getBlockX() > maxX.get()) {
            return false;
        }
        if (maxY.isPresent() && location.getBlockY() > maxY.get()) {
            return false;
        }
        if (maxZ.isPresent() && location.getBlockZ() > maxZ.get()) {
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

    public void applyEffects(@NotNull Player player) {
        player.addPotionEffects(effects);
    }

    @NotNull
    private String normalizeName(@NotNull String biomeName) {
        return biomeName.trim().toUpperCase().replace(" ", "_").replace("-", "_");
    }
}