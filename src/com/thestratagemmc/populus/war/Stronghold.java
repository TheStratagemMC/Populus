package com.thestratagemmc.populus.war;

import com.thestratagemmc.populus.Populus;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

/**
 * Created by Axel on 2/12/2016.
 */
public class Stronghold {
    private Location min;
    private Location max;
    private String coordinates;
    private Set<Location> blocksToChange = new HashSet<>();
    private SplitClaims split = new SplitClaims();
    private long lastUpdate;
    private String name;
    private String ownerTag = null;
    private String lastTag = null;
    private int factor = 5;

    public Stronghold(Location min, Location max, String name, Set<Location> blocksToChange){
        this.min = min;
        this.max = max;
        coordinates = "("+avg(min.getBlockX(), max.getBlockX())+","+avg(min.getBlockZ(), max.getBlockZ())+")";
        this.name = name;
        this.blocksToChange = blocksToChange;
    }

    public Stronghold(Location min, Location max, String ownerTag, String name, Set<Location> blocksToChange) {
        this.ownerTag = ownerTag;
        this.min = min;
        this.max = max;
        split.add(ownerTag, split.getMaxNumber());
        this.name = name;
        this.blocksToChange = blocksToChange;
        coordinates = "("+avg(min.getBlockX(), max.getBlockX())+","+avg(min.getBlockZ(), max.getBlockZ())+")";
    }

    public Stronghold(Location min, Location max, String name, Set<Location> blocks, HashMap<String,Integer> amounts){
        this.min = min;
        this.max = max;
        this.name = name;
        coordinates = "("+avg(min.getBlockX(), max.getBlockX())+","+avg(min.getBlockZ(), max.getBlockZ())+")";
        this.blocksToChange = blocks;
        for (String key : amounts.keySet()){
            split.add(key, amounts.get(key));

        }
    }

    private int avg(int i1, int i2){
        return (int)Math.floor((i1+i2)/2);
    }

    public boolean isInStronghold(Location location){
        if (min.getBlockX() <= location.getBlockX() && min.getBlockY() <= location.getBlockY() && min.getBlockZ() <= location.getBlockZ()){
            if (max.getBlockX() >= location.getBlockX() && max.getBlockY() >= location.getBlockY() && max.getBlockZ() >= location.getBlockZ()){
                return true;
            }
        }
        return false;
    }

    public void addForPlayersStanding(String team, int players){
        lastTag = team;
        lastUpdate = System.currentTimeMillis();
        split.add(team, players * factor);
    }

    public int getPercent(String team){
        return split.get(team);
    }

    public void apply(){
        split.apply(blocksToChange);
    }

    public String getCoordinates(){
        return coordinates;
    }

    public String getName() {
        return name;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }


    public String getLastTag() {
        return lastTag;
    }

    public boolean isOwned(){
        return split.getCurrentVictor() != null;
    }

    public String getOwner(){
        ownerTag = split.getCurrentVictor();
        return ownerTag;
    }

    public ChatColor getColor(){
        String tag = getOwner();
        if (tag == null) return ChatColor.WHITE;

        Clan clan = SimpleClans.getInstance().getClanManager().getClan(tag);
        if (clan == null){
            ownerTag = null;
            return ChatColor.WHITE;
        }
        String c = clan.getColorTag();
        ChatColor ch = ChatColor.getByChar(c.substring(c.lastIndexOf('§') +1, c.lastIndexOf('§')+3));
        if (ch == null) return Populus.getDefaultColor(tag);
        return ch;
    }

    public YamlConfiguration save(){
        YamlConfiguration config = new YamlConfiguration();
        config.set("name", name);
        config.set("min", fl(min));
        config.set("max",fl(max));
        List<String> wool = new ArrayList<>();
        for (Location loc : blocksToChange){
            wool.add(fl(loc));
        }
        config.set("wool", wool);

        config.createSection("split");
        HashMap<String,Integer> points = split.getMap();
        for (String key : points.keySet()){
            config.getConfigurationSection("split").set(key, points.get(key));
        }

        return config;
    }

    public Set<Location> getBlocksToChange() {
        return blocksToChange;
    }

    public Location gl(String string){
        String[] s = string.split("//");
        return new Location(Bukkit.getWorld(s[0]), Double.valueOf(s[1]), Double.valueOf(s[2]), Double.valueOf(s[3]), Float.valueOf(s[4]), Float.valueOf(s[5]));
    }

    public String fl(Location location){
        return location.getWorld().getName()+"//"+location.getX()+"//"+location.getY()+"//"+location.getZ()+"//"+location.getYaw()+"//"+location.getPitch();
    }
}
