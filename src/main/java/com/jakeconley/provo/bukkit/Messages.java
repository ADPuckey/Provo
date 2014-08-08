package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.backend.ProvoFormatException;
import com.jakeconley.provo.features.math.MathSyntaxException;
import com.jakeconley.provo.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messages
{
    public static void Player(CommandSender s){ s.sendMessage("You can only do that from in-game."); }
    public static void ReportException(CommandSender cs, Exception e)
    {
        cs.sendMessage(ChatColor.RED + "There was an internal plugin error, please notify an admin.");
        if(e != null) Utils.LogException(null, e);
    }
    public static void ReportProvoFormatException(CommandSender sender, ProvoFormatException e)
    {
        switch(e.getType())
        {
            case MUTUAL_INHERITANCE:
                sender.sendMessage(ChatColor.RED + "A mutual inheritance error occured.");
                if(e.getOrigin() == ProvoFormatException.Origin.PUBLIC) sender.sendMessage(ChatColor.YELLOW + "This is due to an invalid server configuration.");
                else sender.sendMessage(ChatColor.YELLOW + "This means you somehow created two classes that inherit each other, creating an infinite loop.");
                break;
            case BACKEND_FORMAT:
                sender.sendMessage(ChatColor.RED + "Formatting error!");
                break;
            default:
                sender.sendMessage(ChatColor.RED + "An unknown error occured.");
                Utils.Severe("Unspecified ProvoFormatException " + e.toString());
                break;
        }
        
        if(e.isFixed()) sender.sendMessage(ChatColor.YELLOW + "However, this should have automatically been fixed, so please try again.");
        else if(e.getOrigin() == ProvoFormatException.Origin.PUBLIC) sender.sendMessage(ChatColor.YELLOW + "The plugin couldn't fix this, so please contact an admin.");
        else sender.sendMessage(ChatColor.YELLOW + "The plugin couldn't fix this, so please delete any classes that may cause this error.");
    }
    public static void ReportMathSyntaxException(CommandSender sender, MathSyntaxException e)
    {
        switch(e.getType())
        {
            case UNEXPECTED_SYMBOL: sender.sendMessage(ChatColor.RED + "Error: Unexpected symbol \"" + e.getOffender() + "\"!"); return;
            case EXPECTED_NUMBER: sender.sendMessage(ChatColor.YELLOW + "Error: Expected a number near operator " + e.getOffender() + "."); return;
            case EXPECTED_OPERATOR: sender.sendMessage(ChatColor.YELLOW + "Error: Expected an operator! (You probably have two numbers back-to-back.  Use an operator like * - + or /.)"); return;
            case OPERATOR_OUT_OF_PLACE: sender.sendMessage(ChatColor.RED + "Error: Operator \"" + e.getOffender() + "\" is out of place!  Try adjusting your syntax."); return;
            default: sender.sendMessage(ChatColor.RED + "An unknown math error occured, please report this bug or contact an administrator.");
        }
    }
    public static void Usage(CommandSender cs, String message){ cs.sendMessage(ChatColor.YELLOW + "Usage: " + message); }
}
