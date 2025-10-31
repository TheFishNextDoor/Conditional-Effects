package fun.sunrisemc.effects.conditional_effect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;

import fun.sunrisemc.effects.ConditionalEffectsPlugin;
import fun.sunrisemc.effects.file.ConfigFile;

public class ConditionalEffectManager {

    private static List<ConditionalEffect> conditionalEffects = new ArrayList<>();
    private static HashMap<String, ConditionalEffect> conditionalEffectsMap = new HashMap<>();

    public static Optional<ConditionalEffect> get(String id) {
        return Optional.ofNullable(conditionalEffectsMap.get(id));
    }
    
    public static List<ConditionalEffect> getAll() {
        return new ArrayList<>(conditionalEffects);
    }

    public static List<String> getIds() {
        return new ArrayList<>(conditionalEffectsMap.keySet());
    }

    public static void loadConfig() {
        ArrayList<ConditionalEffect> tempConditionalEffects = new ArrayList<>();

        YamlConfiguration config = ConfigFile.get("effects", false);
        for (String id : config.getKeys(false)) {
            ConditionalEffect conditionalEffect = new ConditionalEffect(config, id);
            tempConditionalEffects.add(conditionalEffect);
            conditionalEffectsMap.put(id, conditionalEffect);
        }

        conditionalEffects = Collections.unmodifiableList(tempConditionalEffects);

        ConditionalEffectsPlugin.logInfo("Loaded " + conditionalEffects.size() + " conditional effects.");
    }
}