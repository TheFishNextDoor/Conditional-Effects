package fun.sunrisemc.effects.schedular;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fun.sunrisemc.effects.ConditionalEffectsPlugin;
import fun.sunrisemc.effects.effect.ConditionalEffect;
import fun.sunrisemc.effects.effect.ConditionalEffectManager;

public class EffectsTimer {

    private static final int INTERVAL = 20 * 2 ; // 2 seconds

    private static int id = -1;

    public static void start() {
        if (id != -1) {
            return;
        }
        id = Bukkit.getScheduler().runTaskTimerAsynchronously(ConditionalEffectsPlugin .getInstance(), () -> {
            run();
        }, INTERVAL, INTERVAL).getTaskId();
    }

    public static void stop() {
        if (id == -1) {
            return;
        }
        Bukkit.getScheduler().cancelTask(id);
        id = -1;
    }

    private static void run() {
        List<ConditionalEffect> conditionalEffects = ConditionalEffectManager.getAll();
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            for (ConditionalEffect conditionalEffect : conditionalEffects) {
                if (conditionalEffect.conditionsMet(player)) {
                    conditionalEffect.applyEffects(player);
                }
            }
        }
    }
}