package fun.sunrisemc.conditional_effects.conditional_effect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.conditional_effects.ConditionalEffectsPlugin;
import fun.sunrisemc.conditional_effects.file.ConfigFile;

public class ConditionalEffectManager {

    private static @NotNull List<ConditionalEffect> conditionalEffectsList = new ArrayList<>();
    private static @NotNull HashMap<String, ConditionalEffect> conditionalEffectsMap = new HashMap<>();

    public static Optional<ConditionalEffect> get(@NotNull String id) {
        return Optional.ofNullable(conditionalEffectsMap.get(id));
    }
    
    @NotNull
    public static List<ConditionalEffect> getAll() {
        return new ArrayList<>(conditionalEffectsList);
    }

    @NotNull
    public static List<String> getIds() {
        return new ArrayList<>(conditionalEffectsMap.keySet());
    }

    public static void loadConfig() {
        YamlConfiguration config = ConfigFile.get("effects", false);
        for (String id : config.getKeys(false)) {
            ConditionalEffect conditionalEffect = new ConditionalEffect(config, id);
            conditionalEffectsMap.put(id, conditionalEffect);
        }

        conditionalEffectsList = Collections.unmodifiableList(new ArrayList<>(conditionalEffectsMap.values()));

        ConditionalEffectsPlugin.logInfo("Loaded " + conditionalEffectsList.size() + " conditional effects.");
    }
}