package fun.sunrisemc.conditional_effects.repeating_task;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fun.sunrisemc.conditional_effects.ConditionalEffectsPlugin;
import fun.sunrisemc.conditional_effects.conditional_effect.ConditionalEffect;
import fun.sunrisemc.conditional_effects.conditional_effect.ConditionalEffectManager;

public class ConditionsCheckTask {

    private static final int INTERVAL_TICKS = 1 ;

    private static int id = -1;

    private static int tickCount = 0;

    public static void start() {
        if (id != -1) {
            return;
        }
        id = Bukkit.getScheduler().runTaskTimer(ConditionalEffectsPlugin .getInstance(), () -> {
            if (tickCount == Integer.MAX_VALUE) {
                tickCount = 0;
            }
            tickCount++;

            List<ConditionalEffect> conditionalEffects = ConditionalEffectManager.getAll();
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            for (ConditionalEffect conditionalEffect : conditionalEffects) {
                if (!conditionalEffect.checkInterval(tickCount)) {
                    continue;
                }
                for (Player player : players) {
                    if (player == null || !player.isOnline()) {
                        continue;
                    }
                    if (conditionalEffect.conditionsMet(player)) {
                        conditionalEffect.applyEffects(player);
                    }
                }
            }
        }, INTERVAL_TICKS, INTERVAL_TICKS).getTaskId();
    }

    public static void stop() {
        if (id == -1) {
            return;
        }
        Bukkit.getScheduler().cancelTask(id);
        id = -1;
    }
}