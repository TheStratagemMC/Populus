package com.thestratagemmc.populus.cmd.clan;

import com.thestratagemmc.populus.cmd.SubExecutor;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Axel on 2/27/2016.
 */
public class ClanBaseCommand extends SubExecutor {
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        if (isNotPlayer(sender, false)) return false;
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer((Player)sender);
        return cp.isTrusted();
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (isNotPlayer(sender)) return;
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer((Player)sender);
        if (!cp.isTrusted()){
            sender.sendMessage(ChatColor.RED + "Must be trusted to run this command!");
            return;
        }


    }

    @Override
    public String getDescription() {
        return null;
    }
}
