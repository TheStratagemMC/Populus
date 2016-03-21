package com.thestratagemmc.populus.cmd.clan;

import com.thestratagemmc.populus.Populus;
import com.thestratagemmc.populus.cmd.SubExecutor;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

/**
 * Created by axel on 10/20/15.
 */
public class ProfileCommand extends SubExecutor {

    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (args.length == 0){
            Clan c = getClan(sender);
            if ( c == null) {
                rnc(sender);
                return;
            }
            print(sender, c);
        }

        else {
            Clan c = SimpleClans.getInstance().getClanManager().getClan(args[0]);
            if ( c == null){
                cne(sender);
                return;
            }
            print(sender, c);
        }
    }
    @Override
    public String getDescription() {
        return "Gets clan profile.";
    }

    public String getName(){
        return "profile [tag]";
    }

    public void print(CommandSender player, Clan clan){
        int length = clan.getName().length();
        int sides = (40 - length)/2;
        StringBuilder _line = new StringBuilder();
        for (int i =0; i < sides; i++){
            _line.append("-");
        }

        String line = _line.toString();

        player.sendMessage(net.md_5.bungee.api.ChatColor.DARK_GRAY + line+ net.md_5.bungee.api.ChatColor.AQUA+clan.getName()+ net.md_5.bungee.api.ChatColor.DARK_GRAY+line);
        player.sendMessage(dg + "Tag: "+lg+ Populus.getColor(clan)+StringUtils.capitalize(clan.getTag()));
        player.sendMessage(dg + "Leader"+(clan.getLeaders().size() > 1 ? "s" : "")+": "+lg+clan.getLeadersString(net.md_5.bungee.api.ChatColor.RED.toString(), ", "));
        //player.sendMessage(dg + "Members Online: "+lg+clan.getOnlineMembers().size()+ net.md_5.bungee.api.ChatColor.WHITE+"/"+lg+clan.getMembers().size());

        player.sendMessage(dg+"Founded:" +ChatColor.getByChar('3') +clan.getFoundedString());
        player.sendMessage(dg + "KDR: "+ ChatColor.YELLOW + clan.getTotalKDR() +"");
        player.sendMessage(dg+"Kills: "+lg+"[Rivals: "+clan.getTotalRival()+", Neutrals: "+clan.getTotalNeutral()+", Civilians: "+clan.getTotalCivilian()+"]");
        player.sendMessage(dg+"Deaths: "+lg+clan.getTotalDeaths()+ " deaths");
        player.sendMessage("");
        player.sendMessage(dg+"Allies: "+clan.getAllyString(lg+", "));
        player.sendMessage(dg+"Rivals: "+clan.getRivalString(lg+", "));
    }


}
