package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.Provo;
import com.jakeconley.provo.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Verify
{
    // Artifact of times when I had to integrate this with PEX API, now just returns simple native API
    public static boolean Permission(Provo plugin, CommandSender s, String permission, boolean message)
    {
        Player p;
        if(s instanceof Player) p = (Player) s;
        else return true;
        
        if(p.hasPermission(permission)) return true;
        
        s.sendMessage(ChatColor.YELLOW + "You don't have permission to do that.");
        return false;
    }   
    
    public static boolean ArgsLength(Command c, CommandSender cs, String[] args, int arg_len, String message)
    {
        if(args.length >= arg_len) return true;
        if(message == null) Messages.Usage(cs, c.getUsage());
        else cs.sendMessage(ChatColor.YELLOW + "Usage: " + message);
        return false;
    }
}
