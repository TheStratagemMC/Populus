package com.thestratagemmc.populus.cmd.clan;

import com.thestratagemmc.populus.cmd.SubExecutor;
import net.md_5.bungee.api.ChatColor;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by Axel on 2/26/2016.
 */
public class ListCommand extends SubExecutor {
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(dg+"Top Clans");
        sender.sendMessage("");
        List<Clan> clans = SimpleClans.getInstance().getClanManager().getClans();
        SimpleClans.getInstance().getClanManager().sortClansBySize(clans);

        int rank = 1;
        while(rank < 11){
            Clan clan = clans.get(rank-1);
            sender.sendMessage(dg+"["+ ChatColor.GOLD+rank+dg+"] "+lg+clan.getName()+": "+ChatColor.getByChar('3')+clan.getMembers().size() +" members, "+ChatColor.YELLOW+clan.getTotalKDR()+" KDR");
            rank++;
        }
    }

    @Override
    public String getDescription() {
        return "Lists all clans.";
    }
}
