package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.Provo;
import com.jakeconley.provo.backend.ProvoFormatException;
import com.jakeconley.provo.backend.SortingPreferencesBackend;
import com.jakeconley.provo.functions.sorting.PreferencesClass;
import com.jakeconley.provo.functions.sorting.PreferencesRule;
import com.jakeconley.provo.functions.sorting.Sorting;
import com.jakeconley.provo.functions.sorting.SortingResult;
import com.jakeconley.provo.utils.Utils;
import com.jakeconley.provo.utils.inventory.InventoryCoords;
import com.jakeconley.provo.utils.inventory.InventoryRange;
import com.jakeconley.provo.utils.inventory.InventoryType;
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
    
    private static void CLASSNOTFOUND(CommandSender sender, String name){ sender.sendMessage(ChatColor.YELLOW + "Couldn't find class \"" + name + "\"!"); }
    
        
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
    if(!Verify.Permission(plugin, sender, "provo.sorting.sort", true)) return true;

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
if(label.equalsIgnoreCase("sortinginfo"))
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
            sender.sendMessage(ChatColor.YELLOW + "To learn more, type " + ChatColor.BOLD + "/sortinginfo 2" + ChatColor.RESET + ChatColor.YELLOW + ".");
            return true;
    }
}
if(label.equalsIgnoreCase("sorting"))
{
    if(args.length == 0){ sender.sendMessage(ChatColor.YELLOW + cmd.getUsage()); return true; }
    
    if(args[0].equalsIgnoreCase("help"))
    {
        if(args.length > 1)
        {
            if(args[1].equalsIgnoreCase("add-class"))
            {
                sender.sendMessage(ChatColor.GREEN + "/sorting add-class <name>[:inherits] <type>" + ChatColor.WHITE + " - Create a sorting class!");
                sender.sendMessage(ChatColor.YELLOW + "<name>: " + ChatColor.WHITE + "What you want to name your class.");
                sender.sendMessage(ChatColor.YELLOW + "Inheritance: " + ChatColor.WHITE + "Optional; to make your class inherit another. Separate this from <name> with a colon (:).");
                sender.sendMessage(ChatColor.YELLOW + "<type>: " + ChatColor.WHITE + "PLAYER, CHEST, or DOUBLECHEST, depending on what you want it to sort.");
                sender.sendMessage(ChatColor.YELLOW + "Example: " + ChatColor.WHITE + "/sorting add-class building player");
                sender.sendMessage(ChatColor.YELLOW + "The above creates a class named \"building\", for sorting a PLAYER's inventory.");
                return true;
            }
            if(args[1].equalsIgnoreCase("add-rule"))
            {
                sender.sendMessage(ChatColor.GREEN + "/sorting add-rule <class>[:priority] <item/group> <area> (area type)" + ChatColor.WHITE + " - Add a rule to a sorting class!");
                sender.sendMessage(ChatColor.YELLOW + "<class>: " + ChatColor.WHITE + "The name of the class to add the rule to.");
                sender.sendMessage(ChatColor.YELLOW + "Priority: " + ChatColor.WHITE + "Optional, default 1, must be a number; rules with higher priority get sorted first. Separate this from <class> with a colon (:).");
                sender.sendMessage(ChatColor.YELLOW + "<item/group>: " + ChatColor.WHITE + "The name of the item (e.g. sand) or a group (use /sorting view-groups) to make the rule match.");
                sender.sendMessage(ChatColor.YELLOW + "<area>: " + ChatColor.WHITE + "The area that the rule matches, for example A1 is the top left corner.");
                sender.sendMessage(ChatColor.YELLOW + "(area type): " + ChatColor.WHITE + "The type of area, if you chose more than one coordinate.  LINEAR or RECTANGULAR.");
                sender.sendMessage(ChatColor.YELLOW + "Example: " + ChatColor.WHITE + "/sorting add-rule myclass cobblestone A1-A9 LINEAR");
                sender.sendMessage(ChatColor.YELLOW + "The above adds a rule to class \"myclass\", putting cobblestone in the top row (A1-A9)");
                sender.sendMessage(ChatColor.YELLOW + "For more about area or priority, use /sortinginfo (area/priority).");
                return true;
            }
            
            sender.sendMessage(ChatColor.YELLOW + "Unknown topic \"" + args[1] + "\".  Use just /sorting help for a list of commands.");
            return true;
        }
        else
        {
            sender.sendMessage(Provo.COMMAND_TRADEMARK);
            sender.sendMessage(ChatColor.GREEN + "Sorting commands list:");
            sender.sendMessage(ChatColor.GREEN + "Use /sortinginfo for more detailed information.");
            sender.sendMessage(ChatColor.DARK_GREEN + "/sort (class) : " + ChatColor.YELLOW + "Sorts your inventory!");
            sender.sendMessage(ChatColor.DARK_GREEN + "/sortinginfo: " + ChatColor.YELLOW + "Shows you more (textual) information.");
            sender.sendMessage(ChatColor.DARK_GREEN + "/sorting help: " + ChatColor.YELLOW + "Shows you this message!");
            sender.sendMessage(ChatColor.DARK_GREEN + "/sorting list-groups: " + ChatColor.YELLOW + "Shows you all of the item groups you have.");
            sender.sendMessage(ChatColor.DARK_GREEN + "/sorting view-group <name>: " + ChatColor.YELLOW + "Show the items in a particular group.");
            sender.sendMessage(ChatColor.DARK_GREEN + "/sorting list-classes: " + ChatColor.YELLOW + "Shows you a list of your preferences classes.");
            sender.sendMessage(ChatColor.DARK_GREEN + "/sorting view-class <name>: " + ChatColor.YELLOW + "Shows you information about one of your classes.");
            sender.sendMessage(ChatColor.DARK_GREEN + "/sorting add-class: " + ChatColor.YELLOW + "Create a class.  Use /sorting help add-class for more.");
            sender.sendMessage(ChatColor.DARK_GREEN + "/sorting del-class <name>: " + ChatColor.YELLOW + "Delete a class.");
            sender.sendMessage(ChatColor.DARK_GREEN + "/sorting add-rule: " + ChatColor.YELLOW + "Add a rule to a class.  Use /sorting help add-rule for more.");
            sender.sendMessage(ChatColor.DARK_GREEN + "/sorting del-rule <class> <rule number>: " + ChatColor.YELLOW + "Delete a rule numbered <index>.");
        }
        return true;
    }
    /*
if(player == null){ Messages.Player(sender); return true; }
        try
        {
        }
        catch(ProvoFormatException e){ Messages.ReportError(sender, null); return true; }
        catch(Exception e){ Messages.ReportError(sender, e); return true; }
        return true;
    */
    if(args[0].equalsIgnoreCase("list-classes"))
    {
        if(player == null){ Messages.Player(sender); return true; }
        try
        {
            Set<String> res = backend.FetchPlayerPreferencesClasses(player.getUniqueId().toString());
            List<PreferencesClass> classes = new LinkedList<>();
            for(String s : res){ classes.add(backend.FetchPlayerPreferencesClass(player_uuid, s)); }
            //TODO: Maybe validate here?
            player.sendMessage(ChatColor.GREEN + "Your classes:");
            for(PreferencesClass pclass : classes)
            {
                String msg = ChatColor.DARK_GREEN + "[" + pclass.getTargetType().toString() + "] " + ChatColor.AQUA + pclass.getName();
                if(pclass.getInheritance() != null) msg += ChatColor.DARK_GREEN + " (inherits \"" + pclass.getInheritance() + "\")";
                player.sendMessage(msg);
            }
        }
        catch(ProvoFormatException e){ Messages.ReportError(sender, null); return true; }
        catch(Exception e){ Messages.ReportError(sender, e); return true; }
        
        return true;
    }
    if(args[0].equalsIgnoreCase("view-class"))
    {
        if(player == null){ Messages.Player(sender); return true; }
        try
        {
            if(!Verify.ArgsLength(cmd, sender, args, 2, "/sorting view-class <classname>")) return true;

            PreferencesClass pclass = backend.FetchPlayerPreferencesClass(player_uuid, args[1]);
            if(pclass == null)
            {
                CLASSNOTFOUND(sender, args[1]);
                return true;
            }
            
            String title = ChatColor.GREEN + "Class " + ChatColor.AQUA + pclass.getName() + ChatColor.GREEN;
            if(pclass.getInheritance() != null) title += " (inherits " + pclass.getInheritance() + ")";
            title += " [" + pclass.getTargetType().toString() + "]";
            player.sendMessage(title);
            
            if(pclass.getRules().isEmpty()) player.sendMessage(ChatColor.DARK_GREEN + "No rules added yet!  Use /sorting add-rule to get started!");
            else player.sendMessage(ChatColor.DARK_GREEN + "Rules:");
            
            
            int native_rule_count = 1;
            for(PreferencesRule rule : pclass.getRules())
            {
                if(rule.isInherited()) sender.sendMessage(ChatColor.BLUE + "--: " + ChatColor.AQUA + rule.toString() + ChatColor.BLUE + " -- from " + rule.getInheritedFrom());
                else
                {
                    sender.sendMessage(ChatColor.BLUE + "#" + Integer.toString(native_rule_count) + ": " + ChatColor.AQUA + rule.toString());
                    native_rule_count++;
                }
            }
            
            return true;
        }
        catch(ProvoFormatException e){ Messages.ReportError(sender, null); return true; }
        catch(Exception e){ Messages.ReportError(sender, e); return true; }
    }
    
    if(args[0].equalsIgnoreCase("list-groups"))
    {
        //TODO: more detailed error reporting and index viewing once i add customizable groups
        try
        {
            HashMap<String,LinkedList<Material>> groups = backend.FetchItemGroups(player_uuid);
            if(groups.isEmpty())
            {
                sender.sendMessage(ChatColor.YELLOW + "There are no item groups!");
                return true;
            }
            
            sender.sendMessage(ChatColor.GREEN + "Item groups:");
            for(String s : groups.keySet()){ sender.sendMessage(ChatColor.DARK_GREEN + s); }
        }
        catch(ProvoFormatException e){ Messages.ReportError(sender, null); return true; }
        catch(Exception e){ Messages.ReportError(sender, e); return true; }
        return true;
    }
    if(args[0].equalsIgnoreCase("view-group"))
    {
        final String USAGE = "/sorting view-group <name>";
        if(!Verify.ArgsLength(cmd, sender, args, 2, USAGE)) return true;

        try
        {
            HashMap<String,LinkedList<Material>> groups = backend.FetchItemGroups(player_uuid);
            LinkedList<Material> group = groups.get(args[1]);
            if(group == null)
            {
                sender.sendMessage(ChatColor.YELLOW + "Couldn't find group \"" + args[1] + "\"!");
                return true;
            }
            
            sender.sendMessage(ChatColor.GREEN + "Group " + args[1]);
            for(Material m : group){ sender.sendMessage(ChatColor.DARK_GREEN + "- " + m.toString()); }
        }
        catch(ProvoFormatException e){ Messages.ReportError(sender, null); return true; }
        catch(Exception e){ Messages.ReportError(sender, e); return true; }
        return true;
    }
    
    if(args[0].equalsIgnoreCase("add-class"))
    {
        final String USAGE = "/sorting add-class <name>[:inherits] <type>. Use /sorting help add-class for more information.";

        if(player == null){ Messages.Player(sender); return true; }
        if(!Verify.ArgsLength(cmd, sender, args, 3, USAGE)){ return true; }        
        if(!Verify.Permission(plugin, sender, "provo.sorting.createclass", true)) return true;
        //TODO: Limits
        
        try
        {
            PreferencesClass class_check = backend.FetchPlayerPreferencesClass(player_uuid, args[1]);
            if(class_check != null)
            {
                sender.sendMessage(ChatColor.YELLOW + "You already have a class named " + class_check.getName() + "!  Use /sorting del-class to remove.");
                return true;
            }
            
            String name;
            String inherits = null;
            InventoryType type;
            
            if(args[1].contains(":"))
            {
                String[] split = args[1].split(":");
                if(split.length != 2){ Messages.Usage(sender, USAGE); return true; }
                name = split[0];
                inherits = split[1];
                
                PreferencesClass inheritance_check = backend.FetchPlayerPreferencesClass(player_uuid, inherits);
                if(inheritance_check == null)
                {
                    sender.sendMessage(ChatColor.YELLOW + "You don't have a class named \"" + inherits + "\" to inherit!");
                    return true;
                }
            }
            else name = args[1];
            
            try{ type = InventoryType.valueOf(args[2].toUpperCase()); }
            catch(IllegalArgumentException e)
            {
                sender.sendMessage(ChatColor.YELLOW + "Invalid type!  Use PLAYER, CHEST, or DOUBLECHEST.");
                Messages.Usage(sender, USAGE);
                return true;
            }
            
            PreferencesClass res = new PreferencesClass(name, type, new LinkedList<PreferencesRule>());
            if(inherits != null) res.setInheritance(inherits);
            
            backend.WritePreferencesClass(player_uuid, res);
            sender.sendMessage(ChatColor.GREEN + "Successfully created class \"" + name + "\".");
        }
        catch(ProvoFormatException e){ Messages.ReportError(sender, null); return true; }
        catch(Exception e){ Messages.ReportError(sender, e); return true; }
        return true;
    }
    if(args[0].equalsIgnoreCase("del-class"))
    {
        final String USAGE = "/sorting del-class <name>";
        if(player == null){ Messages.Player(sender); return true; }
        if(!Verify.ArgsLength(cmd, sender, args, 2, USAGE)){ return true; }        
        if(!Verify.Permission(plugin, sender, "provo.sorting.createclass", true)) return true;

        try
        {
            PreferencesClass pclass = backend.FetchPlayerPreferencesClass(player_uuid, args[1]);
            if(pclass == null)
            {
                CLASSNOTFOUND(sender, args[1]);
                return true;
            }
            
            backend.DeletePreferencesClass(player_uuid, args[1]);
            sender.sendMessage(ChatColor.GREEN + "Successfully deleted class \"" + args[1] + "\".");
        }
        catch(ProvoFormatException e){ Messages.ReportError(sender, null); return true; }
        catch(Exception e){ Messages.ReportError(sender, e); return true; }
        return true;
    }
    
    if(args[0].equalsIgnoreCase("add-rule"))
    {
        final String USAGE = "/sorting add-rule <class>[:priority] <item/group> <area> (area type).  Use /sorting help add-rule for more information";
        if(player == null){ Messages.Player(sender); return true; }
        if(!Verify.ArgsLength(cmd, sender, args, 4, USAGE)){ return true; }        
        if(!Verify.Permission(plugin, sender, "provo.sorting.editclass", true)) return true;
        try
        {
            String pclass_str = args[1];
            int priority = 1;
            if(args[1].contains(":"))
            {
                String[] split = args[1].split(":");
                if(split.length != 2){ Messages.Usage(sender, USAGE); return true; }
                
                pclass_str = split[0];
                try{ priority = Integer.parseInt(split[1]); }
                catch(NumberFormatException e){ sender.sendMessage(ChatColor.YELLOW + "Priority has to be a number!"); Messages.Usage(sender, USAGE); return true; }
            }
            
            PreferencesClass pclass = backend.FetchPlayerPreferencesClass(player_uuid, pclass_str);
            if(pclass == null){ CLASSNOTFOUND(sender, pclass_str); return true; }
            
            //item/group verification
            String item = args[2];
            if(!backend.ItemGroupExists(player_uuid, item) && Utils.GetMaterial(item) == null)
            {
                sender.sendMessage(ChatColor.YELLOW + "Unknown item or group \"" + item + "\".");
                return true;
            }
            
            // Rangetype parsing and verification
            InventoryRange.Type rangetype = InventoryRange.Type.SINGULAR;
            if(args.length == 5)
            {
                try{ rangetype = InventoryRange.Type.valueOf(args[4].toUpperCase()); }
                catch(IllegalArgumentException e)
                {
                    sender.sendMessage(ChatColor.YELLOW + "Invalid range type \"" + args[4] + "\".  Use SINGULAR, LINEAR, or RECTANGULAR.");
                    return true;
                }
            }
            
            String start_str;   InventoryCoords start;
            String end_str;     InventoryCoords end;
            
            // Hella long range bounds parsing and verification code
            if(args[3].contains("-"))
            {
                String[] rangebounds = args[3].split("-");
                if(rangebounds.length == 2)
                {
                    start_str = rangebounds[0];
                    end_str = rangebounds[1];
                }
                else
                {
                    sender.sendMessage(ChatColor.YELLOW + "Invalid area \"" + args[3] + "\"! Too many dashes (-)!");
                    sender.sendMessage(ChatColor.YELLOW + "For example, you could use \"A1-B2 LINEAR\".");
                    Messages.Usage(sender, USAGE);
                    return true;
                }
            }
            else if(rangetype == InventoryRange.Type.SINGULAR)
            {
                start_str = args[3];
                end_str = args[3];
            }
            else
            {
                sender.sendMessage(ChatColor.YELLOW + "For area type " + rangetype + " you need to specify a start and an end.");
                sender.sendMessage(ChatColor.YELLOW + "For example, you could use \"A1-B2 " + rangetype.toString() + "\".");
                return true;
            }
            
            // If the user put in a range, two different values, but not a range type other than singular
            if(!start_str.equalsIgnoreCase(end_str) && rangetype == InventoryRange.Type.SINGULAR)
            {
                sender.sendMessage(ChatColor.YELLOW + "Since you have a start and an end, you need to specify a type of area.");
                sender.sendMessage(ChatColor.YELLOW + "For a rectangle, use \"" + args[3] + " RECTANGULAR\".  For a line that wraps around, use LINEAR instead of RECTANGULAR.");
                return true;
            }
            
            start = InventoryCoords.FromString(start_str, pclass.getTargetType());
            end = InventoryCoords.FromString(end_str, pclass.getTargetType());            
            if(start == null){ sender.sendMessage(ChatColor.YELLOW + "Invalid coordinate \"" + start_str + "\"!  An example coordinate is A1."); return true; }
            if(end == null){ sender.sendMessage(ChatColor.YELLOW + "Invalid coordinate \"" + end_str + "\"!  An example coordinate is A1."); return true; }
            
            InventoryRange area = new InventoryRange(start, end, rangetype);
            PreferencesRule rule = new PreferencesRule(priority, area, item);
            backend.WritePreferencesRule(player_uuid, pclass_str, rule);
            sender.sendMessage(ChatColor.GREEN + "Successfully added rule " + ChatColor.AQUA + rule.toString() + ChatColor.GREEN + " to class \"" + pclass_str + "\".");
        }
        catch(ProvoFormatException e){ Messages.ReportError(sender, null); return true; }
        catch(Exception e){ Messages.ReportError(sender, e); return true; }
        return true;
    }
    if(args[0].equalsIgnoreCase("del-rule"))
    {
        final String USAGE = "/sorting del-rule <class> <rulenumber>";
        final String USAGE_NUMBER = "Use /sorting view-class <class> to get a rule number.";
        if(player == null){ Messages.Player(sender); return true; }
        if(args.length != 3)//i don't like to be strict about args length normally but
        {
            Messages.Usage(sender, USAGE);
            sender.sendMessage(ChatColor.YELLOW + USAGE_NUMBER);
            return true;
        }
        try
        {
            PreferencesClass pclass = backend.FetchPlayerPreferencesClass(player_uuid, args[1]);
            if(pclass == null){ CLASSNOTFOUND(sender, args[1]); return true; }
            
            int index_proper;
            try{ index_proper = (Integer.parseInt(args[2]) - 1); }
            catch(NumberFormatException e)
            {
                sender.sendMessage(ChatColor.YELLOW + "<rulenumber> must be a number! ");
                Messages.Usage(sender, USAGE + " " + USAGE_NUMBER);
                return true;
            }
            
            if(pclass.getRules().isEmpty())
            {
                sender.sendMessage(ChatColor.YELLOW + "Class \"" + args[1] + "\" doesn't have any rules yet!");
                return true;
            }
            // Until we have a proper method, we have to calculate this maximum ourselves.
            // See https://trello.com/c/GRJGaJ22
            int index_max = -1;
            for(PreferencesRule rule : pclass.getRules()){ if(!rule.isInherited()) index_max++; }
            
            if(index_proper > index_max)
            {
                sender.sendMessage(ChatColor.YELLOW + "There are only " + (index_max + 1) + " rules in class \"" + args[1] + "\"!");
                sender.sendMessage(ChatColor.YELLOW + USAGE_NUMBER);
                return true;
            }
            
            backend.DeletePreferencesRule(player_uuid, pclass.getName(), index_proper);
            sender.sendMessage(ChatColor.GREEN + "Successfully deleted rule " + ChatColor.AQUA + "#" + args[2] + ChatColor.GREEN + " from class \"" + args[1] + "\".");
            sender.sendMessage(ChatColor.GREEN + "Note that now other rule's indices may have changed.  Use /sorting view-class if you plan on deleting more rules.");
        }
        catch(ProvoFormatException e){ Messages.ReportError(sender, null); return true; }
        catch(Exception e){ Messages.ReportError(sender, e); return true; }
        return true;
    }

    sender.sendMessage(ChatColor.YELLOW + "Unknown command \"" + args[0] + "\", use /sorting help for a list of commands.");
    return true;
}

        // End
        return true;
    }
}