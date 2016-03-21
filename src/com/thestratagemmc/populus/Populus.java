package com.thestratagemmc.populus;

import com.earth2me.essentials.Essentials;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.thestratagemmc.populus.cmd.*;
import com.thestratagemmc.populus.war.Stronghold;
import com.thestratagemmc.populus.war.War;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by axel on 10/10/15.
 */
public class Populus extends JavaPlugin implements Listener {

    public static boolean doClanChat = true;
    static Populus instance;
    public War war;
    public static HashMap<String,Location> bases = new HashMap<>();
    static List<ChatColor> possibleColors = Arrays.asList(ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.DARK_GREEN, ChatColor.DARK_RED, ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE);
    public static HashMap<String,ChatColor> defaultColor = new HashMap<>();
    public final HashMap<String,Integer> money = new HashMap<>();
    public static final HashMap<String,List<Request>> joinRequests = new HashMap<>();
    public static final HashMap<UUID,List<Request>> inviteRequests = new HashMap<>();



    public static List<Request> getJoinRequests(String clan){
        if (!joinRequests.containsKey(clan)) return new ArrayList<>();
        return joinRequests.get(clan);
    }

    public static Location getBase(String tag){
        if (bases.containsKey(tag)) return bases.get(tag);
        return null;
    }

    public static void removeJoinRequest(String clan, Request request){
        if (!joinRequests.containsKey(clan)) return;
        List<Request> req = joinRequests.get(clan);
        req.remove(request);
        joinRequests.put(clan, req);
    }

    public static boolean removeJoinRequest(String clan, UUID player){
        if (!joinRequests.containsKey(clan)) return false;
        boolean did = false;
        List<Request> req = joinRequests.get(clan);
        List<Request> n = new ArrayList<>();
        for (Request r : req){
            if (r.player.equals(player)) {
                did = true;
                continue;
            }
            n.add(r);
        }

        joinRequests.put(clan, n);
        return did;
    }

    public static void addJoinRequest(String clan, Request request){
        List<Request> req;
        if (joinRequests.containsKey(clan)) req = joinRequests.get(clan);
        else req = new ArrayList<>();
        req.add(request);
        joinRequests.put(clan, req);
    }

    public static boolean hasJoinRequest(String clan, UUID id){
        List<Request> req = getJoinRequests(clan);
        for (Request r : req){
            if (r.player.equals(id)) return true;
        }
        return false;
    }

    public static List<Request> getInviteRequests(UUID id){
        if (!inviteRequests.containsKey(id)) return new ArrayList<>();
        return inviteRequests.get(id);
    }

    public static boolean removeInviteRequest(UUID id, String clan){
        List<Request> req = getInviteRequests(id);
        boolean did = false;
        List<Request> out = new ArrayList<>();
        for (Request r : req){
            if (!r.clan.equalsIgnoreCase(clan)) {
                out.add(r);
            }
            else did = true;
        }
        inviteRequests.put(id, out);
        return did;
    }


    public static void addInviteRequest(UUID id, Request request){
        List<Request> req = getInviteRequests(id);
        req.add(request);
        inviteRequests.put(id, req);
    }

    public static boolean hasInviteRequest(UUID id, String clan){
        List<Request> req = getInviteRequests(id);
        for (Request r : req){
            if (r.clan.equalsIgnoreCase(clan)) return true;
        }
        return false;
    }

    private Essentials ess;
    public static HashMap<ChatColor,Byte> cb = new HashMap<>();
    static{
        a('1', (byte) 11);
        a('2', (byte)13);
        a('3', (byte)9);
        a('4', (byte)14);
        a('5', (byte)10);
        a('6', (byte)1);
        a('7', (byte)8);
        a('8', (byte)7);
        a('9', (byte)11);
        a('a', (byte)5);
        a('b', (byte)3);
        a('c', (byte)6);
        a('d', (byte)2);
        a('e', (byte)4);
        a('f', (byte)0);
    }
    public static void a(char c, byte b){
        cb.put(ChatColor.getByChar(c), b);
    }
    public static ChatColor getDefaultColor(String team){
        if (defaultColor.containsKey(team)) return defaultColor.get(team);
        ChatColor c = randomColor();
        defaultColor.put(team, c);
        return c;
    }
    public static ChatColor randomColor(){
        return possibleColors.get(ThreadLocalRandom.current().nextInt(possibleColors.size()));
    }
    public static byte getWoolColor(ChatColor color){
        return cb.get(color);
    }
    public void onEnable(){
        instance = this;
        getConfig().options().copyDefaults(true);
        getConfig().addDefault("clan-chat", true);
        getConfig().addDefault("war-world","clans");
        saveConfig();

        ess = (Essentials)getServer().getPluginManager().getPlugin("Essentials");
        getServer().getPluginManager().registerEvents(this, this);

        //doClanChat = getConfig().getBoolean("clan-chat");
        getCommand("clanchat").setExecutor(new ClanChatCommand());
        getCommand("cstronghold").setExecutor(new CreateStronghold(this));
        getCommand("cbal").setExecutor(new CBalCommand());
        getCommand("csetbal").setExecutor(new CSetBalCommand());
        getCommand("cclan").setExecutor(new ClanCommand());
        getCommand("cforce").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
                if (!sender.hasPermission("populus.admin")) return false;
                war.forcePay();
                sender.sendMessage("Tried to force pay.");
                return true;
            }
        });
        //getCommand("cclan").setExecutor(new ClanCommand());
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                war = new War(Bukkit.getWorld("clans"));
                war.startTimers(instance);
                if (!getDataFolder().exists()) getDataFolder().mkdir();
                File strongDir = new File(getDataFolder(), "strongholds");
                if (!strongDir.exists()) strongDir.mkdir();

                for (File file : strongDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".yml");
                    }
                })){
                    try{
                        YamlConfiguration config = new YamlConfiguration();
                        config.load(file);

                        String name = config.getString("name");
                        String minS = config.getString("min");
                        String maxS = config.getString("max");
                        List<String> wool = config.getStringList("wool");
                        ConfigurationSection sec = config.getConfigurationSection("split");

                        HashMap<String,Integer> amounts = new HashMap<>();
                        for (String key : sec.getKeys( false )){
                            amounts.put(key, sec.getInt(key));
                        }
                        Set<Location> wb = new HashSet<>();
                        for (String s : wool){
                            wb.add(gl(s));
                        }

                        Stronghold hold = new Stronghold(gl(minS), gl(maxS),name, wb, amounts);
                        war.addStronghold(hold);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }

                try{
                    File m = new File(getDataFolder(), "money.txt");
                    if (!m.exists()) m.createNewFile();
                    else{
                        for (String line : Files.readLines(m, Charset.defaultCharset())){
                            String[] args = line.split(" ");
                            money.put(args[0], Integer.valueOf(args[1]));
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{
                    File r = new File(getDataFolder(), "join-requests.txt");
                    if (!r.exists()) r.createNewFile();
                    else{
                        for (String line : Files.readLines(r, Charset.defaultCharset())){
                            try{
                                Request req = Request.fromString(line);
                                addJoinRequest(req.clan, req);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                try{
                    File i = new File(getDataFolder(), "invite-requests.txt");
                    if (!i.exists()) i.createNewFile();
                    else{
                        for (String line : Files.readLines(i, Charset.defaultCharset())){
                            try{
                                Request req = Request.fromString(line);
                                addInviteRequest(req.player, req);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{
                    File b = new File(getDataFolder(), "bases.txt");
                    if (!b.exists()) b.createNewFile();
                    else{
                        for (String line : Files.readLines(b, Charset.defaultCharset())){
                            String[] parts = line.split("|");
                            String tag = parts[0];
                            Location loc = gl(parts[1]);

                            bases.put(tag, loc);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                saveMoney();
            }
        }, 80l, 80l);
    }

    public void onDisable(){
        for (Stronghold hold : war.getStrongholds()){
            saveStronghold(hold);
        }


        saveMoney();
        saveRequests();
        saveBases();
    }

    public void saveBases(){
        File b = new File(getDataFolder(), "bases.txt");
        List<String> out = new ArrayList<>();
        for (Map.Entry<String,Location> entry : bases.entrySet()){
            out.add(entry.getKey()+"|"+fl(entry.getValue()));
        }

        if (b.exists()) b.delete();
        try{
            b.createNewFile();
            FileOutputStream fout = new FileOutputStream(b);

            fout.write(Joiner.on("\n").join(out).getBytes());
            fout.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void saveRequests(){
        File j = new File(getDataFolder(), "join-requests.txt");
        List<String> jlines = new ArrayList<>();
        for (Map.Entry<String,List<Request>> entry : joinRequests.entrySet()){
            for (Request request : entry.getValue()){
                jlines.add(request.toString());
            }
        }
        try{
            if (j.exists()) j.delete();
            j.createNewFile();
            FileOutputStream out = new FileOutputStream(j);
            out.write(Joiner.on("\n").join(jlines).getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File i = new File(getDataFolder(), "invite-requests.txt");
        List<String> ilines = new ArrayList<>();
        for (Map.Entry<UUID,List<Request>> entry : inviteRequests.entrySet()){
            for (Request request : entry.getValue()){
                ilines.add(request.toString());
            }
        }
        try{
            if (i.exists()) i.delete();
            i.createNewFile();
            FileOutputStream out = new FileOutputStream(i);
            out.write(Joiner.on("\n").join(ilines).getBytes());
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    public void saveMoney(){
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String,Integer> entry : money.entrySet()){
            lines.add(entry.getKey() + " "+entry.getValue());
        }
        File m = new File(getDataFolder(), "money.txt");
        try{
            if (m.exists()) m.delete();
            m.createNewFile();
            FileOutputStream out = new FileOutputStream(m);
            out.write(Joiner.on("\n").join(lines).getBytes());
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void saveStronghold(Stronghold hold){
        try{
            File df = new File(getDataFolder(), "strongholds");
            if (!df.exists()) df.mkdir();
            File file = new File(df, hold.getName()+".yml");
            if (!file.exists()) file.createNewFile();
            hold.save().save(file);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Populus getInstance(){
        return instance;
    }
    public Location gl(String string){
        String[] s = string.split("//");
        return new Location(Bukkit.getWorld(s[0]), Double.valueOf(s[1]), Double.valueOf(s[2]), Double.valueOf(s[3]), Float.valueOf(s[4]), Float.valueOf(s[5]));
    }

    public String fl(Location location){
        return location.getWorld().getName()+"//"+location.getX()+"//"+location.getY()+"//"+location.getZ()+"//"+location.getYaw()+"//"+location.getPitch();
    }

    public void addMoney(String team, int amount){
        if (money.containsKey(team)) money.put(team, money.get(team)+amount);
        else money.put(team, amount);
    }

    public int getMoney(String team){
        if (!money.containsKey(team)) return 0;
        return money.get(team);
    }

    public static ChatColor getColor(Clan clan){
        if (clan == null) return ChatColor.getByChar('7');
        String c = clan.getColorTag();
        ChatColor ch = ChatColor.getByChar(c.substring(c.lastIndexOf('§') +1, c.lastIndexOf('§')+3));
        return ChatColor.getByChar('7');
    }

    @EventHandler
    public void changeWorlds(PlayerChangedWorldEvent event){
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        if (event.getPlayer().getWorld().getName().equals(war.getWorld().getName())){
            ess.getUser(event.getPlayer()).setGodModeEnabled(false);
            event.getPlayer().setFlying(false);

            event.getPlayer().setAllowFlight(false);
        }
    }

}
