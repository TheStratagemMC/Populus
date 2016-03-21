package com.thestratagemmc.populus.war;

import com.thestratagemmc.populus.Populus;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Axel on 2/23/2016.
 */
public class SplitClaims {
    protected int maxNumber = 100;
    private HashMap<String,Integer> map = new HashMap<>();

    protected int total(){
        int i = 0;
        for (int d : map.values()){
            i+=d;
        }
        return i;
    }

    public void add(String tag, int amount){
        if (map.containsKey(tag)) map.put(tag, map.get(tag)+amount);
        else map.put(tag, amount);

        int total = total();
        if (total > maxNumber){
            balance(tag);
        }
    }

    public void apply(Set<Location> blocks){
        List<Location> newList = new ArrayList<>();

        newList.addAll(blocks);
        int total = total();
        if (total > maxNumber){
            balance(null);
        }
        String victor = getCurrentVictor();
        if (victor != null){
            for (Location loc : blocks){
                ChatColor c = getColor(victor);
                if ( c== null) c = Populus.getDefaultColor(victor);

                byte data = Populus.getWoolColor(c);
                loc.getBlock().setData(data);
            }
            return;
        }

        for (String key : map.keySet()){
            int amount = map.get(key);
            if (amount < 1) continue;

            float percentage = ((float)amount)/maxNumber;
      //      Bukkit.broadcast("amount: "+amount, "test");
     //      Bukkit.broadcast("max: "+maxNumber, "test");
           // Bukkit.broadcast("kasjdf: "+percentage, "test");
        //    Bukkit.broadcast("size: "+blocks.size(),"test");
            int blocksChange = (int)Math.ceil(percentage*blocks.size());
        //    Bukkit.broadcast("change: "+blocksChange,"test");
            //Bukkit.broadcast(blocksChange+"", "test");

            for (int i = 0; i < blocksChange; i++){
                Location loc = newList.get(ThreadLocalRandom.current().nextInt(newList.size()));
                // set block to team color
                ChatColor c = getColor(key);
                if ( c== null) c = Populus.getDefaultColor(key);

                byte data = Populus.getWoolColor(c);
                loc.getBlock().setData(data);
                //newList.remove(loc);
            }
        }
    }

    public ChatColor getColor(String tag){
        if (tag == null) return ChatColor.WHITE;

        Clan clan = SimpleClans.getInstance().getClanManager().getClan(tag);
        if (clan == null) return ChatColor.WHITE;
        String c = clan.getColorTag();
        ChatColor ch = ChatColor.getByChar(c.substring(c.lastIndexOf('§') +1, c.lastIndexOf('§')+3));
        if (ch == null) return Populus.getDefaultColor(tag);
        return ch;
    }

    protected Object randomFromSet(Set myHashSet){
        int size = myHashSet.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(Object obj : myHashSet)
        {
            if (i == item)
                return obj;
            i = i + 1;
        }
        return null;
    }

    public void balance(String tag){
        int total = total();
        int remove = total - maxNumber;
        int iterations = (int)Math.floor(remove/5)+1;
        for (int i = 0; i < iterations; i++){
            if (remove < 1) break;
            Set<String> keys = map.keySet();
            if (keys.size() == 1){
                map.put(keys.iterator().next(), 100);

                return;
            }
            String t = (String)randomFromSet(keys);
            while (t.equals(tag)){
                t = (String)randomFromSet(keys);
            }

            int current = map.get(t);
            while (current < 1){
                map.remove(t);
                t = (String)randomFromSet(keys);
                current = map.get(t);
            }

            remove-=current;
            current-=5;
            if (current < 1){
                map.remove(t);
            }
            else map.put(t, current);

        }
    }

    public String getCurrentVictor(){
        for (String key : map.keySet()){
            if (map.get(key) >= maxNumber) return key;
        }
        return null;
    }

    public int get(String team){
        return (int)Math.floor(100 * ((float)map.get(team)/maxNumber));
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public HashMap getMap(){
        return map;
    }
}
