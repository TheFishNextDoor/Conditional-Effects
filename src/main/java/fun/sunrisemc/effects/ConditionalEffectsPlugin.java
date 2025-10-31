package fun.sunrisemc.effects;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.effects.command.ConditionalEffects;
import fun.sunrisemc.effects.conditional_effect.ConditionalEffectManager;
import fun.sunrisemc.effects.repeating_task.EffectsTimer;

public class ConditionalEffectsPlugin extends JavaPlugin {

    private static ConditionalEffectsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        registerCommand("conditionaleffects", new ConditionalEffects());

        loadConfigs();

        EffectsTimer.start();

        logInfo("Plugin enabled.");
    }

    @Override
    public void onDisable() {
        EffectsTimer.stop();
        logInfo("Plugin disabled.");
    }

    public static void loadConfigs() {
        ConditionalEffectManager.loadConfig();
    }

    public static ConditionalEffectsPlugin getInstance() {
        return instance;
    }

    public static void logInfo(@NonNull String message) {
        getInstance().getLogger().info(message);
    }

    public static void logWarning(@NonNull String message) {
        getInstance().getLogger().warning(message);
    }

    public static void logSevere(@NonNull String message) {
        getInstance().getLogger().severe(message);
    }

    private boolean registerCommand(@NonNull String commandName, @NonNull CommandExecutor commandExecutor) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            logSevere("Command '" + commandName + "' not found in plugin.yml.");
            return false;
        }

        command.setExecutor(commandExecutor);

        if (commandExecutor instanceof TabCompleter) {
            command.setTabCompleter((TabCompleter) commandExecutor);
        }

        return true;
    }
}