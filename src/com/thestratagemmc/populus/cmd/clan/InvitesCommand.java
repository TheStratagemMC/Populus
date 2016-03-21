package com.thestratagemmc.populus.cmd.clan;

import com.thestratagemmc.populus.Populus;
import com.thestratagemmc.populus.Request;
import com.thestratagemmc.populus.cmd.SubExecutor;
import net.md_5.bungee.api.ChatColor;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Axel on 2/27/2016.
 */
public class InvitesCommand extends SubExecutor {
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (isNotPlayer(sender)) return;
        Player p = (Player)sender;
        if (args.length == 0){
            List<Request> requests = Populus.getInviteRequests(p.getUniqueId());
            if (requests.size() == 0){
                sender.sendMessage(lg+"You have no pending invites to join a clan.");
                return;
            }
            else{
                sender.sendMessage(dg+"==="+lg+"Invites"+dg+"===");
                for (Request req : requests){
                    String msg = req.message;
                    Clan clan = SimpleClans.getInstance().getClanManager().getClan(req.clan);
                    if (clan == null){
                        Populus.removeInviteRequest(p.getUniqueId(), req.clan);
                        continue;
                    }
                    sender.sendMessage(clan.getColorTag()+lg+" is requesting you to join their clan. "+(msg.isEmpty() ? "" : "Message: "+msg));
                }
                sender.sendMessage("");
                sender.sendMessage(ChatColor.AQUA+"Use /clan invites accept <tag> "+ChatColor.AQUA+"to accept an invite request!");
                ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(p);
                if (cp != null){
                    if (cp.getClan() != null){
                        sender.sendMessage(ChatColor.RED + "Note: Accepting the invite request will cause you to leave "+cp.getClan().getTag()+".");
                    }
                }
            }
        }
        else{
            if (args[0].equalsIgnoreCase("accept")){
                if (args.length < 2){
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Use /clan invites accept <tag>.");
                    return;
                }
                String tag = args[1];
                List<Request> requests = Populus.getInviteRequests(p.getUniqueId());
                for (Request request : requests){
                    if (request.clan.equalsIgnoreCase(tag)){
                        Clan clan = SimpleClans.getInstance().getClanManager().getClan(tag);
                        if (clan == null){
                            p.sendMessage(ChatColor.RED+"Sorry, clan no longer exists!");
                            Populus.removeInviteRequest(p.getUniqueId(), request.clan);
                            return;
                        }
                        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(p);
                        if (cp == null) cp = SimpleClans.getInstance().getClanManager().getCreateClanPlayer(p.getUniqueId());
                        clan.addPlayerToClan(cp);
                        for (Player player : Populus.getInstance().war.getWorld().getPlayers()){
                            player.sendMessage("*" + ChatColor.AQUA + p.getName() + " has joined "+clan.getColorTag()+".");
                        }
                        p.sendMessage("Accepted request!");
                        Populus.removeInviteRequest(p.getUniqueId(), clan.getTag());
                        return;
                    }
                }
                p.sendMessage(ChatColor.RED + "Could not find request from '"+tag+"'.");
            } else if (args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("reject")) {
                if (args.length < 2){
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Use /clan invites deny <tag>.");
                    return;
                }
                String tag = args[1];
                boolean did = Populus.removeInviteRequest(p.getUniqueId(), tag);
                p.sendMessage((did ? lg+"Denied request!" : ChatColor.RED +"No request found from "+tag));
            }
        }
    }

    @Override
    public String getDescription() {
        return "View and respond to pending clan invitations.";
    }
}
