package com.thestratagemmc.populus.cmd;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.thestratagemmc.populus.Populus;
import com.thestratagemmc.populus.war.Stronghold;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Axel on 2/12/2016.
 */
public class CreateStronghold implements CommandExecutor {

    WorldEditPlugin worldEdit;
    Populus populus;

    public CreateStronghold(Populus populus) {
        this.populus = populus;
        worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (!sender.hasPermission("populus.createstronghold")) return false;

        if (!(sender instanceof Player)) return false;


        Player player = (Player)sender;

        Selection sel = worldEdit.getSelection(player);
        if (sel == null){
            player.sendMessage(ChatColor.RED + "Must make a WorldEdit selection first.");
            return true;
        }

        if (args.length == 0){
            StringBuilder out = new StringBuilder();
            for (Stronghold hold : populus.war.getStrongholds()){
                out.append(hold.getName()+", ");
            }
            player.sendMessage(out.toString());
            return true;
        }


        String name = args[0];
        Location min = sel.getMinimumPoint();
        Location max = sel.getMaximumPoint();
        Set<Location> woolBlocks = new HashSet<>();
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++){
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++){
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++){
                    Location loc = new Location(min.getWorld(), x, y, z);
                    if (loc.getBlock().getType() == Material.WOOL){
                        woolBlocks.add(loc);
                    }
                }
            }
        }
        Stronghold hold = new Stronghold(min, max, name, woolBlocks);
        populus.saveStronghold(hold);
        populus.war.addStronghold(hold);

        return true;
    }
}
