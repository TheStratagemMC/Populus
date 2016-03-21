package com.thestratagemmc.populus.war;

import com.thestratagemmc.populus.Populus;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Axel on 2/12/2016.
 */
public class War {
    private long timeStarted;
    private long lastPay;
    private static final int amount = 120;
    private static final int thirtyMin = 1000 * 60 * 30;
    private int length;
    private World world;
    private List<Stronghold> strongholds;
    private int currentStronghold = 0;

    public War(World world){
        timeStarted = System.currentTimeMillis();
        length = 0;
        this.world = world;

        strongholds = new ArrayList<Stronghold>();
    }


    public War(World world, List<Stronghold> strongholds){
        timeStarted = System.currentTimeMillis();
        length = 0;
        this.world = world;
        this.strongholds = strongholds;
    }

    public void startTimers(Plugin plugin){
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                strongholdTimer();
            }
        }, 20l, 20l);
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                scoreboardTimer();
            }
        }, 40l, 40l);
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                payTimer();
            }
        }, 40l, 40l);
    }

    public World getWorld() {
        return world;
    }

    public void forcePay(){
        lastPay = System.currentTimeMillis() - thirtyMin;
    }
    public void payTimer(){
        if (System.currentTimeMillis() - lastPay > thirtyMin){
            lastPay = System.currentTimeMillis();
            for (Stronghold hold : strongholds){
                String own = hold.getOwner();
                if (own == null) continue;
                ChatColor c = hold.getColor();

                for (Player player : world.getPlayers()){
                    player.sendMessage(c+"$"+amount+ " has been payed to the order of "+own+" for owning "+hold.getName()+".");
                }

                Populus.getInstance().addMoney(own, amount);

            }
        }
    }

    public void strongholdTimer(){
        currentStronghold++;
        if (currentStronghold >= strongholds.size()) currentStronghold = 0;
        Stronghold hold = strongholds.get(currentStronghold);
        HashMap<String,Integer> currentAmount = new HashMap<>();
        for (Player player : world.getPlayers()){
            if (!hold.isInStronghold(player.getLocation())) continue;
            ClanPlayer p = SimpleClans.getInstance().getClanManager().getClanPlayer(player);
            if (p == null){
                player.sendMessage(ChatColor.GREEN + "Welcome to the Capture Point! "+ ChatColor.BLUE + "Join or create a clan to get capturing.");
                return;
            }
            Clan clan = p.getClan();
            String tag = clan.getTag();
            if (currentAmount.containsKey(tag)) currentAmount.put(tag, currentAmount.get(tag)+1);
            else currentAmount.put(tag, 1);
        }

        String top = null;
        boolean tie = false;
        for (String team : currentAmount.keySet()){
            if (top == null) top = team;
            else{
                if (currentAmount.get(top) == currentAmount.get(team)){
                    tie = true;
                }
                else if (currentAmount.get(top) < currentAmount.get(team)){
                    top = team;
                    tie = false;
                }
            }
        }
        if (top == null) return;
        if (tie) return;
        int amount = currentAmount.get(top);
        for (String team : currentAmount.keySet()){
            if (team.equalsIgnoreCase(top)) continue;
            amount-=currentAmount.get(team);
        }

        if (amount > 0){
            if (top.equals(hold.getOwner())) return;
            hold.addForPlayersStanding(top, amount);
           // Bukkit.broadcast(amount+"...","test");
            hold.apply();
        }

    }

    public void scoreboardTimer(){
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = scoreboard.registerNewObjective("dummy","dummy");
        obj.setDisplayName(ChatColor.RED+ChatColor.BOLD.toString()+"CLAN WARS");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        //names, treetops, mancave? Barracks

        //Treetops
        //(388,3293)
        //OWNED by Frgtn

        //Barracks
        //(38,239)
        //76% - Stalk

        int line = 17;
        int spaces = 1;
        for (Stronghold hold : strongholds){
            spaces++;
            StringBuilder blank = new StringBuilder();
            for (int i = 0; i < spaces; i++){
                blank.append(" ");
            }
            obj.getScore(blank.toString()).setScore(line--);

            obj.getScore((hold.isOwned() ? ChatColor.WHITE : ChatColor.YELLOW) + ChatColor.BOLD.toString() + hold.getName()).setScore(line--);
            //Bukkit.broadcast(""+(hold == null), "test");
            //Bukkit.broadcast(""+(hold.getCoordinates() == null), "test");
            //Bukkit.broadcast(hold.getCoordinates(), "test");
            obj.getScore(hold.getCoordinates()).setScore(line--);

            String l = null;
            if (hold.isOwned()){
                l = hold.getColor() + "OWNED by "+hold.getOwner() + blank.toString();
            }
            else if (hold.getLastTag() != null){
                String t = hold.getLastTag();
                l = hold.getPercent(t) +"% - "+t;
            }
            else l = "Unowned";
            obj.getScore(l).setScore(line--);

        }

        for (Player player : world.getPlayers()){
            player.setScoreboard(scoreboard);
        }
    }

    public void addStronghold(Stronghold hold){
        strongholds.add(hold);
        hold.apply();
        String owner = hold.getOwner();
        ChatColor color = hold.getColor();
        for (Location loc : hold.getBlocksToChange()){


                loc.getBlock().setData(Populus.getWoolColor(color));


        }
    }

    public List<Stronghold> getStrongholds(){
        return strongholds;
    }

}
