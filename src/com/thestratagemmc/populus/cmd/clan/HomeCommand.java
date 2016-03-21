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
public class HomeCommand extends SubExecutor {
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        if (isNotPlayer(sender, false)) return false;
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer((Player)sender);
        if (cp == null) return false;
        if (cp.getClan() == null) return false;

        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!hasPermission(sender, false)){
            sender.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
            return;
        }
        Player p = (Player)sender;
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(p);
        if (cp.getClan().getHomeLocation() == null){
            p.sendMessage(ChatColor.RED +"Clan does not have a home location set!");
        }
        p.teleport(cp.getClan().getHomeLocation());
        p.sendMessage(ChatColor.DARK_AQUA + "Teleported you to your clan base.");
    }

    @Override
    public String getDescription() {
        return "Teleports you to your clan base.";
    }
}
