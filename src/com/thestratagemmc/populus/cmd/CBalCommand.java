package com.thestratagemmc.populus.cmd;

import com.thestratagemmc.populus.Populus;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Axel on 2/24/2016.
 */
public class CBalCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0){
            if (!(sender instanceof Player)) return false;
            Player player = (Player)sender;
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(player);
            if (cp == null) {
                sender.sendMessage(ChatColor.RED + "You are not in a clan!");
                return true;
            }
            String tag = cp.getTag();
            int amount = Populus.getInstance().getMoney(tag);
            sender.sendMessage(cp.getClan().getColorTag() + "'s balance: $"+amount);
            return true;
        }
        String tag = args[0];
        Clan clan = SimpleClans.getInstance().getClanManager().getClan(tag);

        if (clan == null){
            sender.sendMessage(ChatColor.RED + "Clan '"+tag+"' does not exist!");
        }
        int amount = Populus.getInstance().getMoney(tag);
        sender.sendMessage(clan.getColorTag() + "'s balance: $"+amount);
        return true;
    }
}
