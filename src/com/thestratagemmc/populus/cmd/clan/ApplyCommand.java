package com.thestratagemmc.populus.cmd.clan;

import com.google.common.base.Joiner;
import com.thestratagemmc.populus.Populus;
import com.thestratagemmc.populus.Request;
import com.thestratagemmc.populus.cmd.SubExecutor;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Axel on 2/27/2016.
 */
public class ApplyCommand extends SubExecutor {
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (isNotPlayer(sender)) return;
        Player p = (Player)sender;
        if (args.length == 0){
            p.sendMessage(ChatColor.RED+ "Please specify what clan you want to apply for!");
            p.sendMessage(ChatColor.RED + "/clan apply <tag> [message]");
            return;
        }
        String tag = args[0];
        String message = "";
        if (args.length > 1){
            message = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
        }

        Clan clan = SimpleClans.getInstance().getClanManager().getClan(tag);
        if (clan == null){
            p.sendMessage(ChatColor.DARK_RED+"Error: Clan does not exist.");
            return;
        }
        Populus.addJoinRequest(clan.getTag(), new Request(clan.getTag(), p.getUniqueId(), message));
        p.sendMessage(ChatColor.GREEN + "Join request sent to"+clan.getColorTag()+ChatColor.GREEN+"!");
        for (ClanPlayer cp :clan.getOnlineMembers()){
            Player pl = Bukkit.getPlayer(cp.getUniqueId());
            if (pl != null){
                pl.sendMessage(ChatColor.LIGHT_PURPLE + p.getName() + " has applied to join your clan!" +(cp.isLeader() ? ChatColor.DARK_PURPLE+"To review, use /clan apps." : ChatColor.YELLOW+"Tell your leader to review the application!"));
            }
        }
    }

    @Override
    public String getDescription() {
        return "Apply to join an existing clan.";
    }
}
