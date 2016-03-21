package com.thestratagemmc.populus.cmd.clan;

import com.thestratagemmc.populus.cmd.SubExecutor;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Axel on 2/26/2016.
 */
public class RivalriesCommand extends SubExecutor {
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (isNotPlayer(sender)) return;
        execute((Player)sender, args);
    }

    @Override
    public String getDescription() {
        return "See all clan rivalries.";
    }

    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();
        if(arg.length == 0) {
            if(plugin.getPermissionsManager().has(player, "simpleclans.anyone.rivalries")) {
                List clans = plugin.getClanManager().getClans();
                plugin.getClanManager().sortClansByKDR(clans);
                ChatBlock chatBlock = new ChatBlock();
                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + plugin.getLang("rivalries") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, headColor + plugin.getLang("legend") + ChatColor.DARK_RED + " [" + plugin.getLang("war") + "]");
                ChatBlock.sendBlank(player);
                chatBlock.setAlignment(new String[]{"l", "l"});
                chatBlock.addRow(new String[]{plugin.getLang("clan"), plugin.getLang("rivals")});
                Iterator more = clans.iterator();

                while(more.hasNext()) {
                    Clan clan = (Clan)more.next();
                    if(clan.isVerified()) {
                        chatBlock.addRow(new String[]{"  " + ChatColor.AQUA + clan.getName(), clan.getRivalString(ChatColor.DARK_GRAY + ", ")});
                    }
                }

                boolean more1 = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());
                if(more1) {
                    plugin.getStorageManager().addChatBlock(player, chatBlock);
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("view.next.page"), new Object[]{plugin.getSettingsManager().getCommandMore()}));
                }

                ChatBlock.sendBlank(player);
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.rivalries"), new Object[]{plugin.getSettingsManager().getCommandClan()}));
        }

    }
}
