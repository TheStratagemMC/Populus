package com.thestratagemmc.populus.cmd.clan;

import com.thestratagemmc.populus.cmd.SubExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Axel on 2/26/2016.
 */
public class RequestCommand extends SubExecutor {
    @Override
    public boolean hasPermission(CommandSender sender, boolean verbose) {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

    }

    @Override
    public String getDescription() {
        return "Request to join a clan.";
    }
}
