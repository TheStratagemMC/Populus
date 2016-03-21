package com.thestratagemmc.populus.cmd;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by axel on 10/11/15.
 */
public abstract class SubExecutor {
    protected final static ChatColor dg = ChatColor.DARK_GRAY;
    protected final static ChatColor lg = ChatColor.getByChar('7');

    public abstract boolean hasPermission(CommandSender sender, boolean verbose);
    public abstract void execute(CommandSender sender, String label, String[] args);
    public abstract String getDescription();

    public String getName(){ return null; }

    public Clan getClan(CommandSender sender){
        if (!(sender instanceof Player)) return null;
        Player p = (Player)sender;
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(p);
        if (cp == null) return null;
        return cp.getClan();
    }

    public boolean isAdmin(){
        return false;
    }
    public void rnc(CommandSender sender){
        sender.sendMessage(ChatColor.RED + "Error: You are not in a clan!");
    }

    public void cne(CommandSender sender){
        sender.sendMessage(ChatColor.RED + "Error: Clan does not exist.");
    }

    public boolean isNotPlayer(CommandSender sender){
        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Error: Must be a player to perform this command.");
            return true;
        }
        return false;
    }
    public boolean isNotPlayer(CommandSender sender, boolean verbose){
        if (!(sender instanceof Player)){
            if (verbose)sender.sendMessage(ChatColor.RED + "Error: Must be a player to perform this command.");
            return true;
        }
        return false;
    }

    public boolean isLeader(Player p){
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(p);
        if (cp == null) return false;
        if (cp.isLeader()) return true;
        return false;
    }
}
