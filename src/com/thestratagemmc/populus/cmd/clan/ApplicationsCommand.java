package com.thestratagemmc.populus.cmd.clan;

import com.thestratagemmc.populus.Populus;
import com.thestratagemmc.populus.Request;
import com.thestratagemmc.populus.cmd.SubExecutor;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Axel on 2/27/2016.
 */
public class ApplicationsCommand extends SubExecutor {
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        if (isNotPlayer(sender, false)) return false;
        Player p = (Player)sender;
        return isLeader(p);
    }

    @Override
    public void execute(CommandSender sender, final String label, String[] args) {
        if (isNotPlayer(sender)) return;
        final Player p = (Player)sender;
        if (!isLeader(p)) {
            p.sendMessage(ChatColor.RED + "Must be a leader to perform this command.");
            return;
        }
        Clan clan = SimpleClans.getInstance().getClanManager().getClanPlayer(p).getClan();
        if (args.length == 0){
            final List<Request> req = Populus.getJoinRequests(clan.getTag());
            final List<String> outputs = new ArrayList<>();
            p.sendMessage(dg+"==="+lg+"Applications"+dg+"===");
            Bukkit.getScheduler().runTaskAsynchronously(Populus.getInstance(), new Runnable() {
                @Override
                public void run() {
                    for (Request r : req){
                        String name =Bukkit.getOfflinePlayer(r.player).getName();
                        outputs.add(lg + name + " has requested to join your clan."+(r.message.isEmpty() ? "" : " Message: "+r.message));
                    }

                    Bukkit.getScheduler().runTask(Populus.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            for (String out : outputs){
                                p.sendMessage(out);
                            }
                            p.sendMessage(ChatColor.LIGHT_PURPLE+"To accept an application, run /clan apps accept <username>.");
                        }
                    });
                }
            });
        }
        else{
            if (args[0].equalsIgnoreCase("accept")){
                if (args.length < 2){
                    sender.sendMessage(ChatColor.RED+"Please specify a player.");
                    return;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null){
                    sender.sendMessage(ChatColor.RED+args[1] +" is not online!");
                    return;
                }
                Populus.removeJoinRequest(clan.getTag(), player.getUniqueId());
                ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(player);
                if (cp == null) cp = SimpleClans.getInstance().getClanManager().getCreateClanPlayer(player.getUniqueId());
                clan.addPlayerToClan(cp);
                player.sendMessage(ChatColor.GREEN + "Your application to "+clan.getColorTag()+ ChatColor.GREEN + " was accepted!");
                for (ClanPlayer c : clan.getOnlineMembers()){
                    Player dp = Bukkit.getPlayer(c.getUniqueId());
                    if ( dp != null){
                        dp.sendMessage(ChatColor.GREEN + player.getName() + " has joined the clan. Welcome!");
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("reject")){
                if (args.length < 2){
                    sender.sendMessage(ChatColor.RED +"Please specify a player.");
                    return;
                }

                OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
                if (op == null){
                    sender.sendMessage(ChatColor.RED +"Could not find player "+args[1]);
                    return;
                }
                if (op.hasPlayedBefore()){
                    UUID id = op.getUniqueId();
                    if (Populus.removeJoinRequest(clan.getTag(), id)){
                        p.sendMessage(lg +"Rejected "+op.getName()+"'s application!");
                        return;
                    }
                    else{
                        p.sendMessage(lg +"Could not find application from "+op.getName()+".");
                    }
                }
                else{
                    sender.sendMessage(ChatColor.RED +"Could not find player "+args[1]);
                }

            }
        }

    }

    @Override
    public String getDescription() {
        return "View pending applications for your clan.";
    }
}
