package com.thestratagemmc.populus.cmd;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by axel on 10/10/15.
 */
public class ClanChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0){
            sender.sendMessage(ChatColor.BLUE + "/cc <message> - speaks in clan chat.");
            return true;
        }
        if (!(sender instanceof Player)) return false;
        Player player = (Player)sender;
        ClanPlayer cplayer = SimpleClans.getInstance().getClanManager().getClanPlayer(player);

        Clan clan = cplayer.getClan();
        if (clan == null){
            player.sendMessage(ChatColor.RED + "Error: You are not in a clan.");
            return true;
        }

        StringBuilder b = new StringBuilder();
        for (String string : args){
            b.append(string + " ");
        }

        String brackets = ChatColor.DARK_GRAY+"[%rank%"+ChatColor.DARK_GRAY+"] ";
        if (cplayer.getRank().isEmpty()) brackets = "";
        for (ClanPlayer targ : clan.getOnlineMembers()){
            Player target = Bukkit.getPlayer(targ.getUniqueId());

            if (target == null) continue;
            target.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',ChatColor.AQUA+"[C] "
                            +brackets.replaceAll("%rank%", cplayer.getRank())+ChatColor.WHITE+player.getName()
                    +ChatColor.getByChar('7')+ " "+b.toString())
            );
        }

        return true;
    }
}
