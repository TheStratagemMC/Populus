package com.thestratagemmc.populus.cmd.clan;

import com.thestratagemmc.populus.cmd.SubExecutor;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Axel on 2/26/2016.
 */
public class PlayerCommand extends SubExecutor {
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0){
            if (!(sender instanceof Player)){
                sender.sendMessage("Must be a player!");
                return;
            }
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer((Player)sender);
            if (cp == null){
                sender.sendMessage("Not a registered clan player.");
                return;
            }
            print(sender, cp);
        }
        else{
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(args[0]);
            if (cp == null){
                sender.sendMessage(ChatColor.RED + "Could not find user '"+args[0]+"!");
                return;
            }
            print(sender, cp);
        }
    }

    @Override
    public String getDescription() {
        return "Returns player statistics.";
    }

    public void print(CommandSender player, ClanPlayer cp){
        int length = cp.getName().length();
        int sides = (40 - length)/2;
        StringBuilder _line = new StringBuilder();
        for (int i =0; i < sides; i++){
            _line.append("-");
        }

        String line = _line.toString();

        player.sendMessage(dg + line+ net.md_5.bungee.api.ChatColor.LIGHT_PURPLE+cp.getName()+ dg+line);

        Clan clan = cp.getClan();
        player.sendMessage(dg+"Clan: "+lg+clan.getColorTag());
        player.sendMessage(dg+"Kills: "+lg+cp.getWeightedKills());
        player.sendMessage(dg+"KDR: "+ChatColor.YELLOW+cp.getKDR());
        player.sendMessage(dg+"Deaths: "+ChatColor.RED + cp.getDeaths());

        player.sendMessage(dg+"Past Clans: "+cp.getPastClansString(dg+", "));
    }

    public String getName(){
        return "player [username]";
    }
}
