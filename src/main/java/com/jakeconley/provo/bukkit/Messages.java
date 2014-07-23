package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messages
{
    public static void Player(CommandSender s){ s.sendMessage("You can only do that from in-game."); }
    public static void ReportError(CommandSender cs, Exception e)
    {
        cs.sendMessage(ChatColor.RED + "There was an internal plugin error, please notify an admin.");
        if(e != null) Utils.LogException(null, e);
    }
    public static void Usage(CommandSender cs, String message){ cs.sendMessage(ChatColor.YELLOW + "Usage: " + message); }
}
