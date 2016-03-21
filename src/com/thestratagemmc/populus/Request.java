package com.thestratagemmc.populus;

import java.util.UUID;

/**
 * Created by Axel on 2/26/2016.
 */
public class Request {
    public String clan;
    public UUID player;
    public String message = "";

    public Request(String clan, UUID player) {
        this.clan = clan;
        this.player = player;
    }

    public Request(String clan, UUID player, String message) {
        this.clan = clan;
        this.player = player;
        this.message = message;
    }

    public String toString(){
        return clan +"//"+player.toString()+"//"+message;
    }

    public static Request fromString(String string){
        String[] split = string.split("//");
        Request request = new Request(split[0], UUID.fromString(split[1]), split[2]);
        return request;
    }
}
