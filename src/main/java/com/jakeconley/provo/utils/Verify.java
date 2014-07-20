package com.jakeconley.provo.utils;

import com.jakeconley.provo.Provo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Verify
{
    // Artifact of times when I had to integrate this with PEX API, now just returns simple native API
    public static boolean Permission(Provo plugin, CommandSender s, String permission, boolean message)
    {
        Player p = null;
        if(s instanceof Player) p = (Player) s;
        else return true;
        
        if(p.hasPermission(permission)) return true;
        
        s.sendMessage(ChatColor.YELLOW + "You don't have permission to do that.");
        return false;
    }
    
    public static boolean IsPlayer(CommandSender s)
    {
        if(s instanceof Player) return true;
        s.sendMessage("You can only do that from in-game.");
        return false;
    }    
    public static boolean ArgsLength(Command c, CommandSender cs, String[] args, int arg_len, String message)
    {
        if(args.length >= arg_len) return true;
        if(message == null) cs.sendMessage(ChatColor.RED + "Usage: " + c.getUsage());
        else cs.sendMessage(ChatColor.RED + "Usage: " + message);
        return false;
    }
    
    public static void ReportError(CommandSender cs, String msg)
    {
        cs.sendMessage(ChatColor.RED + "There was an internal plugin error, please notify an admin.");
        Utils.Severe(msg);
    }
}
