package fun.sunrisemc.effects.effect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import fun.sunrisemc.effects.file.ConfigFile;

public class ConditionalEffectManager {

    private static List<ConditionalEffect> conditionalEffects = new ArrayList<>();
    
    public static List<ConditionalEffect> getAll() {
        return new ArrayList<>(conditionalEffects);
    }

    public static void loadConfig() {
        ArrayList<ConditionalEffect> tempConditionalEffects = new ArrayList<>();

        YamlConfiguration config = ConfigFile.get("unlocks", false);
        for (String id : config.getKeys(false)) {
            ConditionalEffect conditionalEffect = new ConditionalEffect(config, id);
            tempConditionalEffects.add(conditionalEffect);
        }

        conditionalEffects = Collections.unmodifiableList(tempConditionalEffects);
    }
}