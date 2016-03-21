package com.thestratagemmc.populus.cmd.clan;

import com.thestratagemmc.populus.cmd.SubExecutor;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by axel on 10/20/15.
 */
public class CoordsCommand extends SubExecutor{
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        if (!(sender instanceof Player)){
            if (verbose)sender.sendMessage("Not a player.");
            return false;
        }
        Player player = (Player)sender;
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(player);

        //Clan clan = SimpleClans.getInstance().getClanManager().getClanPlayer(player).getClan();
        if (cp == null){
            if (verbose)player.sendMessage("Not in a clan.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();
        if (!(sender instanceof Player)) return;
        Player player = (Player)sender;
        if (plugin.getPermissionsManager().has(player, "simpleclans.member.coords"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            if (cp != null)
            {
                Clan clan = cp.getClan();
                if (clan.isVerified())
                {
                    if (cp.isTrusted())
                    {
                        if (arg.length == 0)
                        {
                            ChatBlock chatBlock = new ChatBlock();

                            chatBlock.setFlexibility(new boolean[] { true, false, false, false });
                            chatBlock.setAlignment(new String[] { "l", "c", "c", "c" });

                            chatBlock.addRow(new String[] { "  " + headColor + plugin.getLang("name"), plugin.getLang("distance"), plugin.getLang("coords.upper"), plugin.getLang("world") });

                            List<ClanPlayer> members = Helper.stripOffLinePlayers(clan.getMembers());

                            Map<Integer, List<String>> rows = new TreeMap();
                            for (ClanPlayer cpm : members)
                            {
                                Player p = cpm.toPlayer();
                                if (p != null)
                                {
                                    String name = (cpm.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : cpm.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : plugin.getSettingsManager().getPageUnTrustedColor()) + cpm.getName();
                                    Location loc = p.getLocation();
                                    int distance = (int)Math.ceil(loc.toVector().distance(player.getLocation().toVector()));
                                    String coords = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
                                    String world = loc.getWorld().getName();

                                    List<String> cols = new ArrayList();
                                    cols.add("  " + name);
                                    cols.add(ChatColor.AQUA + "" + distance);
                                    cols.add(ChatColor.WHITE + "" + coords);
                                    cols.add(world);
                                    rows.put(Integer.valueOf(distance), cols);
                                }
                            }
                            if (!rows.isEmpty())
                            {
                                for (List<String> col : rows.values()) {
                                    chatBlock.addRow(new String[] { (String)col.get(0), (String)col.get(1), (String)col.get(2), (String)col.get(3) });
                                }
                                ChatBlock.sendBlank(player);
                                ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + Helper.capitalize(clan.getName()) + subColor + " " + plugin.getLang("coords") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                                ChatBlock.sendBlank(player);

                                boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());
                                if (more)
                                {
                                    plugin.getStorageManager().addChatBlock(player, chatBlock);
                                    ChatBlock.sendBlank(player);
                                    ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("view.next.page"), new Object[]{plugin.getSettingsManager().getCommandMore()}));
                                }
                                ChatBlock.sendBlank(player);
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.are.the.only.member.online"));
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.coords"), new Object[] { plugin.getSettingsManager().getCommandClan() }));
                        }
                    }
                    else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.coords"));
                    }
                }
                else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }

    @Override
    public String getDescription() {
        return "Shows coordinates of clan members.";
    }
}
