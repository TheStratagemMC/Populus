package com.thestratagemmc.populus.cmd;

import com.thestratagemmc.populus.Populus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Axel on 2/24/2016.
 */
public class CSetBalCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("populus.admin")) return true;

        if (args.length < 2){
            sender.sendMessage("Please specify name and amount.");
        }


        String team = args[0];
        int amount = Integer.valueOf(args[1]);
        Populus.getInstance().money.put(team, amount);
        sender.sendMessage(ChatColor.GREEN + "Set "+team+" to $"+amount+"!");
        return true;
    }
}
