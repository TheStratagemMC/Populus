package com.thestratagemmc.populus.cmd;

import com.thestratagemmc.populus.Populus;
import com.thestratagemmc.populus.Request;
import com.thestratagemmc.populus.cmd.clan.*;
import com.thestratagemmc.populus.cmd.clan.AlliancesCommand;
import com.thestratagemmc.populus.cmd.clan.CoordsCommand;
import com.thestratagemmc.populus.cmd.clan.HomeCommand;
import com.thestratagemmc.populus.cmd.clan.InviteCommand;
import com.thestratagemmc.populus.cmd.clan.ListCommand;
import com.thestratagemmc.populus.cmd.clan.ProfileCommand;
import com.thestratagemmc.populus.cmd.clan.RivalriesCommand;
import com.thestratagemmc.populus.cmd.clan.VitalsCommand;
import net.md_5.bungee.api.ChatColor;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.text.NumberFormat;
import java.util.*;

/**
 * Created by axel on 10/11/15.
 */
public class ClanCommand implements CommandExecutor {
    HashMap<String,SubExecutor> subCommands = new HashMap<>();
    HashMap<String,String> aliases = new HashMap<>();
    public ClanCommand(){

        //leader commands
        register("applications", new ApplicationsCommand());
        alias("applications","apps");
        register("invite", new InviteCommand());
        register("vitals", new VitalsCommand());
        register("coords", new CoordsCommand());
        register("sethome", new ClanSetHomeCommand());
        //public info
        register("rivalries", new RivalriesCommand());
        register("alliances", new AlliancesCommand());
        alias("alliances","allies","allys");
        alias("rivalries","rivals");
        register("player", new PlayerCommand());
        alias("player","lookup","stats");
        register("list", new ListCommand());

        //clan specific commands
        register("profile", new ProfileCommand());
        alias("profile","clan","info");
        register("home", new HomeCommand());


        //player specific commands
        register("invites", new InvitesCommand());
        register("apply", new ApplyCommand());
    }

    String line = ChatColor.getByChar('9')+"--------------------------------";
    String dg = ChatColor.DARK_GRAY.toString();
    String lg = ChatColor.getByChar('7').toString();

    void alias(String targetCommand, String... a){
        for (String s : a){
            aliases.put(s, targetCommand);
        }
    }
    void register(String subcommand, SubExecutor executor){
        subCommands.put(subcommand, executor);
    }

    void c2s(String command, Class clazz) {
        subCommands.put(command, C2S.get(command, clazz));
    }

    void c2s(String command, Class clazz, String description){
        subCommands.put(command, C2S.get(command, clazz, description));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0){
            if (!(sender instanceof Player)) return false;
            Player player = (Player)sender;
            ClanPlayer _player = SimpleClans.getInstance().getClanManager().getClanPlayer(player);

            if (_player == null || _player.getClan() == null){
                player.sendMessage(line);
                player.sendMessage(ChatColor.getByChar('3') + "You are currently not in a clan! "
                + "Apply to join a clan or create one.");
                player.sendMessage(line);
                return true;
            }

            Clan clan = _player.getClan();
            int length = clan.getName().length();
            int sides = (40 - length)/2;
            StringBuilder _line = new StringBuilder();
            for (int i =0; i < sides; i++){
                _line.append("-");
            }

            String line = _line.toString();

            player.sendMessage(ChatColor.DARK_GRAY + line+ChatColor.AQUA+clan.getName()+ChatColor.DARK_GRAY+line);
            player.sendMessage(dg + "Tag: "+lg+ StringUtils.capitalize(clan.getTag()));
            player.sendMessage(dg + "Leader"+(clan.getLeaders().size() > 1 ? "s" : "")+": "+lg+clan.getLeadersString(ChatColor.RED.toString(), ", "));
            player.sendMessage(dg + "Members Online: "+lg+clan.getOnlineMembers().size()+ChatColor.WHITE+"/"+lg+clan.getMembers().size());

            player.sendMessage("");

            if (clan.getHomeLocation() == null){
                player.sendMessage(ChatColor.RED + "Warning! Your clan does not have a base set. Until your clan does, you will not be able to participate in clan wars.");
            }
            List<Request> invites = Populus.getInviteRequests(player.getUniqueId());
            if (invites.size() > 0){
                player.sendMessage(ChatColor.GOLD + "You have "+ NumberFormat.getIntegerInstance().format(invites.size()) +" pending clan invite requests. Use "+ChatColor.LIGHT_PURPLE+"/clan invites "+ChatColor.GOLD + " to view them.");
            }
            if (clan.isLeader(player)){
                List<Request> applications = Populus.getJoinRequests(clan.getTag());
                if (applications.size() > 0){
                    player.sendMessage(ChatColor.YELLOW + "Your clan has "+applications.size() + " pending join applications.");
                }
            }

            player.sendMessage(ChatColor.getByChar('3') + "Use /"+command.getLabel()+" help for a list of commands you can do.");
            return true;
        }
        else if (args.length > 0){
            if (args[0].equalsIgnoreCase("help")){
                sender.sendMessage(line);
                for (Map.Entry<String,SubExecutor> entry : subCommands.entrySet()){
                    if (entry.getValue().hasPermission(sender, false)){
                        sender.sendMessage((entry.getValue().isAdmin() ? ChatColor.RED : dg)+"/"+s + " "+(entry.getValue().getName() != null ? entry.getValue().getName() : entry.getKey())+": "+lg+entry.getValue().getDescription());
                    }
                }

            }
            else{
                if (aliases.containsKey(args[0].toLowerCase())){
                    String key = aliases.get(args[0].toLowerCase());
                    SubExecutor exec = subCommands.get(key);
                    if (!exec.hasPermission(sender, true)){
                        return true;
                    }
                    exec.execute(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
                    return true;
                }
                for (Map.Entry<String,SubExecutor> entry : subCommands.entrySet()){
                    if (entry.getKey().equalsIgnoreCase(args[0])){
                        if (entry.getValue().hasPermission(sender, true)){
                            entry.getValue().execute(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
                        }
                        return true;
                    }
                }
                SimpleClans.getInstance().getCommandManager().processClan(sender, args);
                //sender.sendMessage(ChatColor.DARK_RED +"Error: "+ChatColor.RED+"Command '"+args[0]+"' not found.");
            }
        }


        return true;
    }

    public class StringDa{
        private String string;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }
}
