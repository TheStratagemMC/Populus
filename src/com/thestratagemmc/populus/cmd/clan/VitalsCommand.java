package com.thestratagemmc.populus.cmd.clan;

import com.thestratagemmc.populus.cmd.SubExecutor;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by axel on 10/11/15.
 */
public class VitalsCommand extends SubExecutor{
    private SimpleClans plugin = SimpleClans.getInstance();

    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        if (!(sender instanceof Player)){
            if (verbose) sender.sendMessage("Must be a player to perform this command.");
            return false;
        }
        Player player = (Player)sender;

        if (SimpleClans.getInstance().getClanManager().getClanPlayer(player) == null){
            if (verbose)sender.sendMessage(ChatColor.RED + "Must be in a clan to perform this command.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        String headColor = SimpleClans.getInstance().getSettingsManager().getPageHeadingsColor();
        String subColor = SimpleClans.getInstance().getSettingsManager().getPageSubTitleColor();
        
        Player player = (Player)sender;
        ChatBlock.sendBlank(player);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m=---=---= &r&lV I T A L S&c&m=---=---="));
        Clan clan = SimpleClans.getInstance().getClanManager().getClanPlayer(player).getClan();

        ChatBlock chatBlock = new ChatBlock();

        //ChatBlock.saySingle(player, SimpleClans.getInstance().getSettingsManager().getPageClanNameColor() + Helper.capitalize(clan.getName()) + subColor + " " + SimpleClans.getInstance().getLang("vitals") + " " + headColor + Helper.generatePageSeparator(SimpleClans.getInstance().getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);
        ChatBlock.sendMessage(player, headColor + SimpleClans.getInstance().getLang("weapons") + ": " + MessageFormat.format(SimpleClans.getInstance().getLang("0.s.sword.1.2.b.bow.3.4.a.arrow"), new Object[] { ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.DARK_GRAY, ChatColor.getByChar('7') }));
        ChatBlock.sendMessage(player, headColor + SimpleClans.getInstance().getLang("materials") + ": " + ChatColor.AQUA + "Diamond" + ChatColor.DARK_GRAY + ", " + ChatColor.YELLOW + "Gold" + ChatColor.DARK_GRAY + ", " + ChatColor.GRAY + "Stone/Chain" + ChatColor.DARK_GRAY + ", " + ChatColor.WHITE + "Iron" + ChatColor.DARK_GRAY + ", " + ChatColor.GOLD + "Wood/Leather");

        ChatBlock.sendBlank(player);

        chatBlock.setFlexibility(new boolean[] { true, false, false, false, false, false });
        chatBlock.setAlignment(new String[] { "l", "l", "l", "c", "c", "c" });

        chatBlock.addRow(new String[] { "  " + ChatColor.getByChar('5').toString() + SimpleClans.getInstance().getLang("name"), SimpleClans.getInstance().getLang("health"), SimpleClans.getInstance().getLang("hunger"), SimpleClans.getInstance().getLang("food"), SimpleClans.getInstance().getLang("armor"), SimpleClans.getInstance().getLang("weapons") });

        List<ClanPlayer> members = Helper.stripOffLinePlayers(clan.getLeaders());
        members.addAll(Helper.stripOffLinePlayers(clan.getNonLeaders()));
        for (ClanPlayer cpm : members)
        {
            Player p = cpm.toPlayer();
            if (p != null)
            {
                String name = (cpm.isLeader() ? ChatColor.getByChar('3').toString() : cpm.isTrusted() ? ChatColor.WHITE.toString() : ChatColor.getByChar('7').toString()) + cpm.getName();
                String health = SimpleClans.getInstance().getClanManager().getHealthString(p.getHealth());
                String hunger = SimpleClans.getInstance().getClanManager().getHungerString(p.getFoodLevel());
                String armor = getArmorString(p.getInventory());
                String weapons = getWeaponString(p.getInventory());
                String food = getFoodString(p.getInventory());

                chatBlock.addRow(new String[] { "  " + name, ChatColor.RED + health, hunger, ChatColor.WHITE + food, armor, weapons });
            }
        }


        chatBlock.sendBlock(player);

    }

    @Override
    public String getDescription() {
        return "Displays the status of online clan members.";
    }

    public String getArmorString(PlayerInventory inv){
        String out = "";

        ItemStack h = inv.getHelmet();
        if (h != null) {
            if (h.getType().equals(Material.CHAINMAIL_HELMET)) {
                out = out + ChatColor.WHITE + this.plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.DIAMOND_HELMET)) {
                out = out + ChatColor.AQUA + this.plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.GOLD_HELMET)) {
                out = out + ChatColor.YELLOW + this.plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.IRON_HELMET)) {
                out = out + ChatColor.GRAY + this.plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.LEATHER_HELMET)) {
                out = out + ChatColor.GOLD + this.plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.AIR)) {
                out = out + ChatColor.BLACK + this.plugin.getLang("armor.h");
            } else {
                out = out + ChatColor.RED + this.plugin.getLang("armor.h");
            }
        }
        ItemStack c = inv.getChestplate();
        if (c != null) {
            if (c.getType().equals(Material.CHAINMAIL_CHESTPLATE)) {
                out = out + ChatColor.WHITE + this.plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.DIAMOND_CHESTPLATE)) {
                out = out + ChatColor.AQUA + this.plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.GOLD_CHESTPLATE)) {
                out = out + ChatColor.YELLOW + this.plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.IRON_CHESTPLATE)) {
                out = out + ChatColor.GRAY + this.plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.LEATHER_CHESTPLATE)) {
                out = out + ChatColor.GOLD + this.plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.AIR)) {
                out = out + ChatColor.BLACK + this.plugin.getLang("armor.c");
            } else {
                out = out + ChatColor.RED + this.plugin.getLang("armor.c");
            }
        }
        ItemStack l = inv.getLeggings();
        if (l != null) {
            if (l.getType().equals(Material.CHAINMAIL_LEGGINGS)) {
                out = out + ChatColor.WHITE + this.plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.DIAMOND_LEGGINGS)) {
                out = out + this.plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.GOLD_LEGGINGS)) {
                out = out + this.plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.IRON_LEGGINGS)) {
                out = out + this.plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.LEATHER_LEGGINGS)) {
                out = out + this.plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.AIR)) {
                out = out + this.plugin.getLang("armor.l");
            } else {
                out = out + this.plugin.getLang("armor.l");
            }
        }
        ItemStack b = inv.getBoots();
        if (b != null) {
            if (b.getType().equals(Material.CHAINMAIL_BOOTS)) {
                out = out + ChatColor.WHITE + this.plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.DIAMOND_BOOTS)) {
                out = out + ChatColor.AQUA + this.plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.GOLD_BOOTS)) {
                out = out + ChatColor.YELLOW + this.plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.IRON_BOOTS)) {
                out = out + ChatColor.WHITE + this.plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.LEATHER_BOOTS)) {
                out = out + ChatColor.GOLD + this.plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.AIR)) {
                out = out + ChatColor.BLACK + this.plugin.getLang("armor.B");
            } else {
                out = out + ChatColor.RED + this.plugin.getLang("armor.B");
            }
        }
        if (out.length() == 0) {
            out = ChatColor.getByChar('7') + "None";
        }
        return out;
    }

    public String getWeaponString(PlayerInventory inv)
    {
        String headColor = this.plugin.getSettingsManager().getPageHeadingsColor();

        String out = "";

        int count = getItemCount(inv.all(Material.DIAMOND_SWORD));
        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out = out + ChatColor.AQUA + this.plugin.getLang("weapon.S") + countString;
        }
        count = getItemCount(inv.all(Material.GOLD_SWORD));
        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out = out + ChatColor.YELLOW + this.plugin.getLang("weapon.S") + countString;
        }
        count = getItemCount(inv.all(Material.IRON_SWORD));
        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out = out + ChatColor.WHITE + this.plugin.getLang("weapon.S") +countString;
        }
        count = getItemCount(inv.all(Material.STONE_SWORD));
        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out = out + ChatColor.GRAY + this.plugin.getLang("weapon.S") + countString;
        }
        count = getItemCount(inv.all(Material.WOOD_SWORD));
        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out = out + ChatColor.GOLD + this.plugin.getLang("weapon.S") + countString;
        }
        count = getItemCount(inv.all(Material.BOW));
        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out = out + ChatColor.BLUE+ this.plugin.getLang("weapon.B") + countString;
        }
        count = getItemCount(inv.all(Material.ARROW));
        if (count > 0) {
            out = out + ChatColor.getByChar('7') + this.plugin.getLang("weapon.A") + count;
        }
        if (out.length() == 0) {
            out = ChatColor.getByChar('7')+ "None";
        }
        return out;
    }

    private int getItemCount(HashMap<Integer, ? extends ItemStack> all)
    {
        int count = 0;
        for (ItemStack is : all.values()) {
            count += is.getAmount();
        }
        return count;
    }

    public String getFoodString(PlayerInventory inv)
    {
        double out = 0.0D;

        int count = getItemCount(inv.all(320));
        if (count > 0) {
            out += count * 4;
        }
        count = getItemCount(inv.all(Material.COOKED_FISH));
        if (count > 0) {
            out += count * 3;
        }
        count = getItemCount(inv.all(Material.COOKIE));
        if (count > 0) {
            out += count * 1;
        }
        count = getItemCount(inv.all(Material.CAKE));
        if (count > 0) {
            out += count * 6;
        }
        count = getItemCount(inv.all(Material.CAKE_BLOCK));
        if (count > 0) {
            out += count * 9;
        }
        count = getItemCount(inv.all(Material.MUSHROOM_SOUP));
        if (count > 0) {
            out += count * 4;
        }
        count = getItemCount(inv.all(Material.BREAD));
        if (count > 0) {
            out += count * 3;
        }
        count = getItemCount(inv.all(Material.APPLE));
        if (count > 0) {
            out += count * 2;
        }
        count = getItemCount(inv.all(Material.GOLDEN_APPLE));
        if (count > 0) {
            out += count * 5;
        }
        count = getItemCount(inv.all(Material.RAW_BEEF));
        if (count > 0) {
            out += count * 2;
        }
        count = getItemCount(inv.all(364));
        if (count > 0) {
            out += count * 4;
        }
        count = getItemCount(inv.all(319));
        if (count > 0) {
            out += count * 2;
        }
        count = getItemCount(inv.all(Material.RAW_CHICKEN));
        if (count > 0) {
            out += count * 1;
        }
        count = getItemCount(inv.all(Material.COOKED_CHICKEN));
        if (count > 0) {
            out += count * 3;
        }
        count = getItemCount(inv.all(Material.ROTTEN_FLESH));
        if (count > 0) {
            out += count * 2;
        }
        count = getItemCount(inv.all(360));
        if (count > 0) {
            out += count * 2;
        }
        if (out == 0.0D) {
            return ChatColor.getByChar('7') + this.plugin.getLang("none");
        }
        return new DecimalFormat("#.#").format(out) + "" + ChatColor.GOLD + "h";
    }

}
