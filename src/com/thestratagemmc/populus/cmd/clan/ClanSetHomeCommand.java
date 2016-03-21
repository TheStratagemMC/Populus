package com.thestratagemmc.populus.cmd.clan;

import com.thestratagemmc.populus.cmd.SubExecutor;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Axel on 2/27/2016.
 */
public class ClanSetHomeCommand extends SubExecutor{
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        if (isNotPlayer(sender, false)) return false;
        if (!isLeader((Player)sender)) return false;
        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!hasPermission(sender, false)) return;
        Player p = (Player)sender;
        Clan clan = SimpleClans.getInstance().getClanManager().getClanPlayer(p).getClan();
        clan.setHomeLocation(p.getLocation());
        p.sendMessage(lg+"Clan home set!");
    }

    @Override
    public String getDescription() {
        return "Sets clan home.";
    }
}
