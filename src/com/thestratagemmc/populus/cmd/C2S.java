package com.thestratagemmc.populus.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * Created by axel on 10/24/15.
 */
public class C2S {

    Class<? extends Object> clazz;
    String executor;
    String description = "Default description.";

    public C2S(String executor, Class<? extends Object> clazz){
        this.clazz = clazz;
        this.executor = executor;
    }

    public C2S(String executor, Class<? extends Object> clazz, String description){
        this.clazz = clazz;
        this.executor = executor;
        this.description = description;
    }



    public SubExecutor getExecutor(){
        return new SubExecutor() {
            @Override
            public boolean hasPermission(CommandSender sender, boolean verbose) {
                return true;
            }

            @Override
            public void execute(CommandSender sender, String label, String[] args) {
                if (!(sender instanceof Player)) return;
                Player player = (Player)sender;
                try{
                    Method execute = clazz.getDeclaredMethod("execute", new Class[]{Player.class, String[].class});
                    execute.setAccessible(true);
                    execute.invoke(null, player, args);
                }catch(Exception e){
                    player.sendMessage(ChatColor.RED+ "Populus: "+ ChatColor.WHITE + "Error running simple clans command in compatibility mode: "+e.getMessage());
                }

            }

            @Override
            public String getDescription() {
                return description;
            }
        };
    }

    public static SubExecutor get(String name, Class clazz){
        return new C2S(name, clazz).getExecutor();
    }

    public static SubExecutor get(String name, Class clazz, String description){
        return new C2S(name, clazz, description).getExecutor();
    }
}
