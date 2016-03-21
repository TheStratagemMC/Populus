package com.thestratagemmc.populus.cmd.clan;

import com.google.common.base.Joiner;
import com.thestratagemmc.populus.Populus;
import com.thestratagemmc.populus.Request;
import com.thestratagemmc.populus.cmd.SubExecutor;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Axel on 2/27/2016.
 */
public class InviteCommand extends SubExecutor {
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        if (isNotPlayer(sender, false)) return false;
        if (!isLeader((Player)sender)) return false;
        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!hasPermission(sender, false)){
            sender.sendMessage(ChatColor.RED+"No permission to run this command.");
            return;
        }
        if (args.length == 0){
            sender.sendMessage(ChatColor.RED + "Please use /"+label+" invite <username> [message]");
            return;
        }

        String name = args[0];
        String message = "";
        if (args.length > 1){
            message = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
        }

        Player target = Bukkit.getPlayer(name);
        if (target == null){
            sender.sendMessage(ChatColor.RED + "Could not locate player "+name);
            return;
        }
        Player player = (Player)sender;
        Clan clan = SimpleClans.getInstance().getClanManager().getClanPlayer(player).getClan();

        Request request = new Request(clan.getTag(), target.getUniqueId(), message);
        Populus.addInviteRequest(target.getUniqueId(), request);
        player.sendMessage(ChatColor.GREEN+"Invited "+target.getName()+"!");
        target.sendMessage(ChatColor.YELLOW + player.getName() + " has invited you to join "+clan.getColorTag()+".");
        target.sendMessage(ChatColor.YELLOW + "To review clan invites, use /clan invites.");
    }

    @Override
    public String getDescription() {
        return "Invite a player to your clan.";
    }
}
