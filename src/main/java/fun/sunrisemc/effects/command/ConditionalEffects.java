package fun.sunrisemc.effects.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import fun.sunrisemc.effects.ConditionalEffectsPlugin;
import fun.sunrisemc.effects.permission.Permissions;
import net.md_5.bungee.api.ChatColor;

public class ConditionalEffects implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("help");
            if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
                completions.add("reload");
            }
        }
        return completions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            helpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        if (sender.hasPermission(Permissions.RELOAD_PERMISSION) && subCommand.equals("reload")) {
            ConditionalEffectsPlugin.loadConfigs();
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Configuration reloaded.");
            return true;
        }

        helpMessage(sender);
        return true;
    }

    public void helpMessage(CommandSender sender) {
        sender.sendMessage("Conditional Effects Help:");
        sender.sendMessage("/ce help - Show this help message.");
        if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
            sender.sendMessage("/ce reload - Reload the configuration files.");
        }
    }
}