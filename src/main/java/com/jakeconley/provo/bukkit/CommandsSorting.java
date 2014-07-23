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
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
            player.sendMessage(ChatColor.YELLOW + "Use /sortinghelp for more information.");
            return true;
        }
    }
    if(args.length >= 1) pclassname = args[0];

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
        
        Utils.Debug("PRESORT:"); List<ItemStack> PreSort = Sorting.CollapseInventory(player.getInventory().getContents(), player.getInventory().getArmorContents(), null);
        SortingResult res = Sorting.SortInventory(player.getInventory(), pclass, igroups);
        Utils.Debug("POSTSORT:"); List<ItemStack> PostSort = Sorting.CollapseInventory(res.Contents, res.ArmorContents, null);
        
        if(PreSort.equals(PostSort))
        {
            Utils.Debug("final contents");
            for(int i = 0; i < res.Contents.length; i++){ Utils.Debug("  " + i + ": " + (res.Contents[i] != null ? res.Contents[i].toString() : "null")); }
            player.getInventory().clear();
            player.getInventory().setArmorContents(res.ArmorContents);
            player.getInventory().setContents(res.Contents);        
            player.sendMessage(ChatColor.GREEN + "Successfully sorted inventory.");
        }
        else
        {
            player.sendMessage(ChatColor.RED + "Sort failed!");
        }
        return true;
    }
    catch(ProvoFormatException e){ return true; }
    catch(Exception e){ Messages.ReportError(sender, e); return true; }
}
if(label.equalsIgnoreCase("sortinghelp"))
{
    int page = 1;
    final int PAGE_MAX = 2;
    if(args.length > 0)
    {
        try
        {
            int parsed = Integer.parseInt(args[0]);
            if(0 < parsed && parsed < PAGE_MAX) page = parsed;
        }
        catch(NumberFormatException e)
        {
            Messages.Usage(sender, null);
            return true;
        }
    }
    
    sender.sendMessage(Provo.COMMAND_TRADEMARK);
    sender.sendMessage(ChatColor.GREEN + "Sorting help - page " + page + "/" + PAGE_MAX);
    
    switch(page)
    {
        case 1:
            sender.sendMessage("Sorting is a powerful feature in provo that can organize your inventory or chests in one simple command.");
            sender.sendMessage("Sorting is done via the /sort command.  In order to sort, one needs to first understand classes.");
            sender.sendMessage("A class is a set of rules that tells the plugin exactly how you want your inventory sorted.");
            sender.sendMessage("By default, you are given a class named \"base\".");
            sender.sendMessage(ChatColor.YELLOW + "To learn more, type " + ChatColor.BOLD + "/sortinghelp 2" + ChatColor.RESET + ChatColor.YELLOW + ".");
            return true;
    }
}
if(label.equalsIgnoreCase("sorting"))
{
    if(args.length == 0){ sender.sendMessage(ChatColor.YELLOW + cmd.getUsage()); return true; }
    
    if(args[0].equalsIgnoreCase("help"))
    {
        sender.sendMessage(Provo.COMMAND_TRADEMARK);
        sender.sendMessage(ChatColor.GREEN + "Sorting commands list:");
        sender.sendMessage(ChatColor.DARK_GREEN + "Use /sorting info for more detailed information.");
        sender.sendMessage(ChatColor.DARK_GREEN + "/sort (class) : " + ChatColor.YELLOW + "Sorts your inventory!");
        sender.sendMessage(ChatColor.DARK_GREEN + "/sorting help: " + ChatColor.YELLOW + "Shows you this message!");
        sender.sendMessage(ChatColor.DARK_GREEN + "/sorting info: " + ChatColor.YELLOW + "Shows you more (textual) information.");
        sender.sendMessage(ChatColor.DARK_GREEN + "/sorting list-classes: " + ChatColor.YELLOW + "Shows you a list of your preferences classes.");
        sender.sendMessage(ChatColor.DARK_GREEN + "/sorting view-class <name>: " + ChatColor.YELLOW + "Shows you information about one of your classes.");
        sender.sendMessage(ChatColor.DARK_GREEN + "/sorting add-class: " + ChatColor.YELLOW + "Create a class.  Use /sorting help add-class for more.");
        sender.sendMessage(ChatColor.DARK_GREEN + "/sorting del-class <name>: " + ChatColor.YELLOW + "Delete a class.");
        sender.sendMessage(ChatColor.DARK_GREEN + "/sorting add-rule: " + ChatColor.YELLOW + "Add a rule to a class.  Use /sorting help add-rule for more.");
        sender.sendMessage(ChatColor.DARK_GREEN + "/sorting del-rule <index>: " + ChatColor.YELLOW + "Delete a rule numbered <index>.");
        return true;
    }
    if(args[0].equalsIgnoreCase("list-classes"))
    {
        if(player == null){ Messages.Player(sender); return true; }
        try
        {
            Set<String> res = backend.FetchPlayerPreferencesClasses(player.getUniqueId().toString());
            List<PreferencesClass> classes = new LinkedList<>();
            for(String s : res){ classes.add(backend.FetchPlayerPreferencesClass(player_uuid, s)); }
            //TODO: Maybe validate here?
            player.sendMessage(ChatColor.DARK_GREEN + "Your classes:");
            for(PreferencesClass pclass : classes)
            {
                String msg = ChatColor.DARK_GREEN + "[" + pclass.getTargetType().toString() + "] " + ChatColor.AQUA + pclass.getName();
                if(pclass.getInheritance() != null) msg += ChatColor.DARK_GREEN + " (inherits \"" + pclass.getInheritance() + "\")";
                player.sendMessage(msg);
            }
        }
        catch(ProvoFormatException e){ return true; }
        catch(Exception e){ Messages.ReportError(sender, e); return true; }
        
        return true;
    }
}

        // End
        Messages.Usage(sender, cmd.getUsage());
        return true;
    }
}