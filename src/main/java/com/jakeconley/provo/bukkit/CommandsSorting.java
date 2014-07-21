package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.Provo;
import com.jakeconley.provo.backend.ProvoFormatException;
import com.jakeconley.provo.backend.SortingPreferencesBackend;
import com.jakeconley.provo.functions.sorting.PreferencesClass;
import com.jakeconley.provo.functions.sorting.Sorting;
import com.jakeconley.provo.functions.sorting.SortingResult;
import com.jakeconley.provo.utils.Utils;
import java.util.HashMap;
import java.util.LinkedList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsSorting implements CommandExecutor
{
    private final HashMap<String, String> CurrentClasses = new HashMap<>();// UUID to pclass
    
    private final Provo plugin;
    private final SortingPreferencesBackend backend;
    public CommandsSorting(Provo _plugin)
    {
        plugin = _plugin;
        backend = plugin.getSortingPreferencesBackend();
    }
    
        
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player player = null;
        String player_uuid = null;
        if(sender instanceof Player)
        {
            player = (Player) sender;
            player_uuid = player.getUniqueId().toString();
        }
        // Start

if(label.equalsIgnoreCase("sort"))
{
    if(player == null){ Messages.Player(sender); return true; }

    String pclassname = CurrentClasses.get(player_uuid);            
    if(pclassname == null)
    {
        if(args.length < 1)
        {
            player.sendMessage(ChatColor.YELLOW + "You must use /sort <class name> to pick a class first!");
            player.sendMessage(ChatColor.YELLOW + "" +  ChatColor.ITALIC + "Then" + ChatColor.RESET + ChatColor.YELLOW + " you may use /sort alone.");
            return true;
        }

        pclassname = args[0];
    }

    try
    {
        PreferencesClass pclass = plugin.getSortingPreferencesBackend().FetchPlayerPreferencesClass(player.getUniqueId().toString(), pclassname);
        if(pclass == null)
        {
            player.sendMessage(ChatColor.YELLOW + "Couldn't find class \"" + pclassname + "\"!");
            if(args.length < 1) player.sendMessage(ChatColor.YELLOW + "Use /sort <classname> to set a new class name.");
        }
        
        CurrentClasses.put(player_uuid, pclassname);
        
        HashMap<String, LinkedList<Material>> igroups = backend.FetchItemGroups(player_uuid);
        SortingResult res = Sorting.SortInventory(player.getInventory(), pclass, igroups);
        player.getInventory().setArmorContents(res.ArmorContents);
        player.getInventory().setContents(res.Contents);
        
        player.sendMessage(ChatColor.GREEN + "Successfully sorted inventory.");
        return true;
    }
    catch(ProvoFormatException e){ return true; }
    catch(Exception e){ Messages.ReportError(sender, e); return true; }
}

        // End
        Messages.Usage(sender, cmd.getUsage());
        return true;
    }
}