package com.mythmc.commands.command.tab;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("reload");
            completions.add("test");
            completions.add("open");
            completions.add("claim");
        }
        if (args.length == 2 && args[0].equals("test")) {
            completions.add("normal");
            completions.add("offday");
        }
        if (args.length == 2 && args[0].equals("open")) {
            completions.add("main");
            completions.add("reward");
        }
        return completions;
    }
}
