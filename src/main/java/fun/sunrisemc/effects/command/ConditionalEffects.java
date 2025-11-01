package fun.sunrisemc.effects.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.effects.ConditionalEffectsPlugin;
import fun.sunrisemc.effects.conditional_effect.ConditionalEffect;
import fun.sunrisemc.effects.conditional_effect.ConditionalEffectManager;
import fun.sunrisemc.effects.permission.Permissions;
import net.md_5.bungee.api.ChatColor;

public class ConditionalEffects implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
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
            if (subCommand.equals("give") && sender.hasPermission(Permissions.GIVE_PERMISSION)) {
                return getOnlinePlayerNames();
            } 
            else if (subCommand.equals("check") && sender.hasPermission(Permissions.CHECK_PERMISSION)) {
                return getOnlinePlayerNames();
            }
        }
        else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("give") && sender.hasPermission(Permissions.GIVE_PERMISSION)) {
                return ConditionalEffectManager.getIds();
            }
            else if (subCommand.equals("check") && sender.hasPermission(Permissions.CHECK_PERMISSION)) {
                return ConditionalEffectManager.getIds();
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            helpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // Reload Command
        if (sender.hasPermission(Permissions.RELOAD_PERMISSION) && subCommand.equals("reload")) {
            ConditionalEffectsPlugin.loadConfigs();
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Configuration reloaded.");
            return true;
        }
        // Give Command
        else if (subCommand.equals("give") && sender.hasPermission(Permissions.GIVE_PERMISSION)) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /ce give <player> <effectId>");
                return true;
            }

            String playerName = args[1];
            String effectId = args[2];

            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + playerName + "' not found.");
                return true;
            }

            Optional<ConditionalEffect> conditionalEffectOptional = ConditionalEffectManager.get(effectId);
            if (!conditionalEffectOptional.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Conditional effect with ID '" + effectId + "' not found.");
                return true;
            }

            ConditionalEffect conditionalEffect = conditionalEffectOptional.get();
            conditionalEffect.applyEffects(player);
            sender.sendMessage(ChatColor.GREEN + "Applied conditional effect '" + effectId + "' to player '" + playerName + "'.");
            return true;
        }
        // Check Command
        else if (subCommand.equals("check") && sender.hasPermission(Permissions.CHECK_PERMISSION)) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /ce check <player> <effectId>");
                return true;
            }

            String playerName = args[1];
            String effectId = args[2];

            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + playerName + "' not found.");
                return true;
            }

            Optional<ConditionalEffect> conditionalEffectOptional = ConditionalEffectManager.get(effectId);
            if (!conditionalEffectOptional.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Conditional effect with ID '" + effectId + "' not found.");
                return true;
            }

            ConditionalEffect conditionalEffect = conditionalEffectOptional.get();
            if (conditionalEffect.conditionsMet(player)) {
                conditionalEffect.applyEffects(player);
                sender.sendMessage(ChatColor.GREEN + "Applied conditional effect '" + effectId + "' to player '" + playerName + "'.");
            } else {
                sender.sendMessage(ChatColor.RED + "Player '" + playerName + "' does not meet the conditions for effect '" + effectId + "'.");
            }
            return true;
        }

        helpMessage(sender);
        return true;
    }

    private void helpMessage(@NonNull CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Conditional Effects Help");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/conditionaleffects help " + ChatColor.WHITE + "Show this help message");
        if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/conditionaleffects reload " + ChatColor.WHITE + "Reload the plugin");
        }
        if (sender.hasPermission(Permissions.GIVE_PERMISSION)) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/conditionaleffects give <player> <effectId> " + ChatColor.WHITE + "Give a conditional effect to a player");
        }
        if (sender.hasPermission(Permissions.CHECK_PERMISSION)) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/conditionaleffects check <player> <effectId> " + ChatColor.WHITE + "Manually check a conditional effect on a player");
        }
    }

    private static ArrayList<String> getOnlinePlayerNames() {
        ArrayList<String> playerNames = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }
}