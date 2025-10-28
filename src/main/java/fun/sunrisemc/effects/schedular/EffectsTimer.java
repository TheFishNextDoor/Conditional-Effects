package fun.sunrisemc.effects.schedular;

import org.bukkit.Bukkit;

import fun.sunrisemc.effects.ConditionalEffectsPlugin;

public class EffectsTimer {

    private static final int INTERVAL = 20 * 2 ; // 2 seconds

    private static int id = -1;

    public static void start() {
        if (id != -1) {
            return;
        }
        id = Bukkit.getScheduler().runTaskTimerAsynchronously(ConditionalEffectsPlugin .getInstance(), () -> {
            ConditionalEffectsPlugin.logInfo("Timer");
        }, INTERVAL, INTERVAL).getTaskId();
    }

    public static void stop() {
        if (id == -1) {
            return;
        }
        Bukkit.getScheduler().cancelTask(id);
        id = -1;
    }
}