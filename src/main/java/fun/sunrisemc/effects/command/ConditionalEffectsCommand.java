package fun.sunrisemc.effects.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.sunrisemc.effects.ConditionalEffectsPlugin;
import fun.sunrisemc.effects.conditional_effect.ConditionalEffect;
import fun.sunrisemc.effects.conditional_effect.ConditionalEffectManager;
import fun.sunrisemc.effects.permission.Permissions;

public class ConditionalEffectsCommand implements CommandExecutor, TabCompleter {

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        // /conditionaleffects <subcommand>
        if (args.length == 1) {
            ArrayList<String> completions = new ArrayList<>();
            completions.add("help");
            if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
                completions.add("reload");
            }
            if (sender.hasPermission(Permissions.GIVE_PERMISSION)) {
                completions.add("give");
            }
            if (sender.hasPermission(Permissions.CHECK_PERMISSION)) {
                completions.add("check");
            }
            return completions;
        }
        else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();

            // /conditionaleffects give <player>
            if (subCommand.equals("give") && sender.hasPermission(Permissions.GIVE_PERMISSION)) {
                return getOnlinePlayerNames();
            }
            // /conditionaleffects check <player>
            else if (subCommand.equals("check") && sender.hasPermission(Permissions.CHECK_PERMISSION)) {
                return getOnlinePlayerNames();
            }
        }
        else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();

            // /conditionaleffects give <player> <effectId>
            if (subCommand.equals("give") && sender.hasPermission(Permissions.GIVE_PERMISSION)) {
                return ConditionalEffectManager.getIds();
            }
            // /conditionaleffects check <player> <effectId>
            else if (subCommand.equals("check") && sender.hasPermission(Permissions.CHECK_PERMISSION)) {
                return ConditionalEffectManager.getIds();
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0) {
            helpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // Reload Command
        if (sender.hasPermission(Permissions.RELOAD_PERMISSION) && subCommand.equals("reload")) {
            ConditionalEffectsPlugin.loadConfigs();
            sender.sendMessage(ChatColor.DARK_PURPLE + "Configuration reloaded.");
            return true;
        }
        // Give Command
        else if (subCommand.equals("give") && sender.hasPermission(Permissions.GIVE_PERMISSION)) {
            // Ensure the user provided enough arguments
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /ce give <player> <effectId>");
                return true;
            }

            // Get player
            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + playerName + "' not found.");
                return true;
            }

            // Get conditional effect
            String effectId = args[2];
            Optional<ConditionalEffect> conditionalEffectOptional = ConditionalEffectManager.get(effectId);
            if (!conditionalEffectOptional.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Conditional effect with ID '" + effectId + "' not found.");
                return true;
            }

            // Apply conditional effect and notify sender
            ConditionalEffect conditionalEffect = conditionalEffectOptional.get();
            conditionalEffect.applyEffects(player);
            sender.sendMessage(ChatColor.GREEN + "Applied conditional effect '" + effectId + "' to player '" + playerName + "'.");
            return true;
        }
        // Check Command
        else if (subCommand.equals("check") && sender.hasPermission(Permissions.CHECK_PERMISSION)) {
            // Ensure the user provided enough arguments
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /ce check <player> <effectId>");
                return true;
            }

            // Get player
            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + playerName + "' not found.");
                return true;
            }

            // Get conditional effect
            String effectId = args[2];
            Optional<ConditionalEffect> conditionalEffectOptional = ConditionalEffectManager.get(effectId);
            if (!conditionalEffectOptional.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Conditional effect with ID '" + effectId + "' not found.");
                return true;
            }

            // Check conditions and notify sender
            ConditionalEffect conditionalEffect = conditionalEffectOptional.get();
            if (conditionalEffect.conditionsMet(player)) {
                conditionalEffect.applyEffects(player);
                sender.sendMessage(ChatColor.GREEN + "Applied conditional effect '" + effectId + "' to player '" + playerName + "'.");
            } 
            else {
                sender.sendMessage(ChatColor.RED + "Player '" + playerName + "' does not meet the conditions for effect '" + effectId + "'.");
            }
            return true;
        }

        helpMessage(sender);
        return true;
    }

    private void helpMessage(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Conditional Effects Help");
        sender.sendMessage(ChatColor.DARK_PURPLE + "/conditionaleffects help " + ChatColor.WHITE + "Show this help message");
        if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "/conditionaleffects reload " + ChatColor.WHITE + "Reload the plugin");
        }
        if (sender.hasPermission(Permissions.GIVE_PERMISSION)) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "/conditionaleffects give <player> <effectId> " + ChatColor.WHITE + "Give a conditional effect to a player");
        }
        if (sender.hasPermission(Permissions.CHECK_PERMISSION)) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "/conditionaleffects check <player> <effectId> " + ChatColor.WHITE + "Manually check a conditional effect on a player");
        }
        sender.sendMessage(ChatColor.DARK_PURPLE + "Note: Commands you do not have permission for will not be shown.");
    }

    @NotNull
    private static ArrayList<String> getOnlinePlayerNames() {
        ArrayList<String> playerNames = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == null) {
                continue;
            }
            playerNames.add(player.getName());
        }
        return playerNames;
    }
}