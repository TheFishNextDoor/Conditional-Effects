package fun.sunrisemc.conditional_effects;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.sunrisemc.conditional_effects.command.ConditionalEffectsCommand;
import fun.sunrisemc.conditional_effects.conditional_effect.ConditionalEffectManager;
import fun.sunrisemc.conditional_effects.repeating_task.ConditionsCheckTask;

public class ConditionalEffectsPlugin extends JavaPlugin {

    private static @Nullable ConditionalEffectsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        registerCommand("conditionaleffects", new ConditionalEffectsCommand());

        loadConfigs();

        ConditionsCheckTask.start();

        logInfo("Plugin enabled.");
    }

    @Override
    public void onDisable() {
        ConditionsCheckTask.stop();
        logInfo("Plugin disabled.");
    }

    public static void loadConfigs() {
        ConditionalEffectManager.loadConfig();
    }

    @NotNull
    public static ConditionalEffectsPlugin getInstance() {
        if (instance != null) {
            return instance;
        }
        else {
            throw new IllegalStateException("Plugin instance is not initialized.");
        }
    }

    public static void logInfo(@NotNull String message) {
        getInstance().getLogger().info(message);
    }

    public static void logWarning(@NotNull String message) {
        getInstance().getLogger().warning(message);
    }

    public static void logSevere(@NotNull String message) {
        getInstance().getLogger().severe(message);
    }

    private boolean registerCommand(@NotNull String commandName, @NotNull CommandExecutor commandExecutor) {
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