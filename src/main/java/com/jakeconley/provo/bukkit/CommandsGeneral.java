package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.Provo;
import com.jakeconley.provo.backend.ProvoFormatException;
import com.jakeconley.provo.features.math.MathSyntaxException;
import com.jakeconley.provo.features.math.Maths;
import com.jakeconley.provo.notifications.Notification;
import com.jakeconley.provo.utils.Utils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class CommandsGeneral implements CommandExecutor
{
    private static final char [] RECIPE_CHARS = { '@', '#', '$', '%', '&', 'A', 'O', 'S', 'H'};
    private static final char UNICODE_NULL = '\u0000';
    
    private final Provo plugin;
    public CommandsGeneral(Provo _plugin){ this.plugin = _plugin; }
    
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

if(label.equalsIgnoreCase("view-notifications"))
{
    if(player == null){ Messages.Player(sender); return true; }
    
    try
    {
        List<Notification> notifications = plugin.getNotificationsBackend().GetNotifications(player_uuid);
        
        if(notifications.size() > 0)
        {
            player.sendMessage(ChatColor.AQUA + "You have " + notifications.size() + " notification(s):");
            for(Notification n : notifications)
            {
                for(String line : n.toPlayerFriendlyStringList()) player.sendMessage(line);
            }
            
            plugin.getNotificationsBackend().ClearNotifications(player_uuid);
        }
        else player.sendMessage(ChatColor.YELLOW + "You don't have any notifications!");
    }
    catch(ProvoFormatException e){ Messages.ReportProvoFormatException(sender, e); }
    catch(Exception e){ Messages.ReportException(sender, e); }
    return true;
}
if(label.equalsIgnoreCase("recipe"))
{
    if(!Verify.ArgsLength(cmd, sender, args, 1, cmd.getUsage())) return true;
    
    int page = 0;
    StringBuilder sb_itemname = new StringBuilder();
    
    // Syntactic variable argument adjustment for the last argument being an optional page number
    int last_string_index;
    try
    {
        page = Integer.parseInt(args[args.length - 1]) - 1;
        last_string_index = args.length - 2;
    }
    catch(NumberFormatException e){ last_string_index = args.length - 1; }
    
    //Build string
    for(int i = 0; i <= last_string_index; i++)
    {
        if(i != 0) sb_itemname.append(' ');
        sb_itemname.append(args[i]);
    }
    
    Material m = Utils.GetMaterial(sb_itemname.toString());
    if(m == null){ sender.sendMessage(ChatColor.YELLOW + "Unknown item \"" + sb_itemname.toString() + "\"."); return true; }    
    ItemStack stack = new ItemStack(m);
    
    List<Recipe> recipes = plugin.getServer().getRecipesFor(stack);
    if(recipes == null || recipes.isEmpty()){ sender.sendMessage(ChatColor.YELLOW + "Couldn't find any recipes for that item."); return true; }    
    if(page >= recipes.size()){ sender.sendMessage(ChatColor.YELLOW + "There are only " + recipes.size() + " recipes!"); return true; }
    
    sender.sendMessage(ChatColor.GREEN + "Recipe " + (page + 1) + "/" + recipes.size());
    Recipe recipe = recipes.get(page);
    if(recipe instanceof ShapelessRecipe)
    {
        ShapelessRecipe casted = (ShapelessRecipe) recipe;
        sender.sendMessage(ChatColor.YELLOW + "This recipe is shapeless.  Ingredients:");
        for(ItemStack is : casted.getIngredientList()) sender.sendMessage(ChatColor.YELLOW + is.getType().toString() + ChatColor.RESET + "x" + is.getAmount());
    }
    else if(recipe instanceof ShapedRecipe)
    {
        ShapedRecipe casted = (ShapedRecipe) recipe;
        
        Map<ItemStack, Character> stack_to_new = new HashMap<>();
        Map<Character, Character> old_to_new = new HashMap<>();
        
        int recipechars_cur_index = 0;
        for(Map.Entry<Character, ItemStack> entry : casted.getIngredientMap().entrySet())
        {
            if(entry.getValue() == null) continue;
            char newer = stack_to_new.getOrDefault(entry.getValue(), UNICODE_NULL);
            if(newer == UNICODE_NULL)
            {
                newer = RECIPE_CHARS[recipechars_cur_index];
                stack_to_new.put(entry.getValue(), newer);
                recipechars_cur_index++;
            }
            
            old_to_new.put(entry.getKey(), newer);
        }
        
        List<String> newdiagram = new LinkedList<>();
        for(String s : casted.getShape())
        {
            StringBuilder newline = new StringBuilder();
            for(char c : s.toCharArray())
            {
                if(casted.getIngredientMap().get(c) == null) newline.append("[ ]");
                else newline.append('[').append(old_to_new.get(c)).append(']');
            }
            newdiagram.add(newline.toString());
        }        
        
        sender.sendMessage(ChatColor.YELLOW + "This recipe is shaped.  Below is a diagram:");
        for(String line : newdiagram) sender.sendMessage(line);
        sender.sendMessage(ChatColor.YELLOW + "Key:");
        for(Map.Entry<ItemStack, Character> entry : stack_to_new.entrySet())
            sender.sendMessage(ChatColor.AQUA + new String() + entry.getValue() + ": " + ChatColor.YELLOW + entry.getKey().getType().toString() + ChatColor.RESET + "x" + entry.getKey().getAmount());
    }
    else if(recipe instanceof FurnaceRecipe)
    {
        sender.sendMessage(ChatColor.YELLOW + "This item can be obtained by smelting " + ((FurnaceRecipe) recipe).getInput().getType().toString() + " in a furnace.");
        return true;
    }
    else
    {
        sender.sendMessage(ChatColor.RED + "Unknown recipe origin! :(");
        return true;
    }
    
}
if(label.equalsIgnoreCase("unenchant"))
{
    if(player == null){ Messages.Player(sender); return true; }
    
    ItemStack is = player.getInventory().getItemInHand();
    Map<Enchantment, Integer> enchantments = is.getEnchantments();
    
    if(enchantments.isEmpty()){ player.sendMessage(ChatColor.YELLOW + "That item doesn't contain any enchantments!"); return true; }
    
    for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) is.removeEnchantment(entry.getKey());
    player.sendMessage(ChatColor.YELLOW + "Successfully removed enchantments on that item.");
    return true;
}

 if(label.equalsIgnoreCase("math"))
{
    if(args.length == 0)
    {
        sender.sendMessage(ChatColor.YELLOW + "Type in a math query to execute!  For more info, see /math help.");
        return true;
    }
    
    if(args.length == 1 && args[0].equalsIgnoreCase("help"))
    {
        sender.sendMessage(Provo.COMMAND_TRADEMARK);
        sender.sendMessage("/math supports standard four-function operators (+ for addition, - for subtraction, * for multiplication, / for division)");
        sender.sendMessage("Also provided is an exponentiation operator (^) as well as a \"sqrt\" keyword.  Use sqrt before a number to return its square root.");
        sender.sendMessage("The plugin will try to make a \"-\" in front of a number a negative sign; to force it to be a subtraction operator simply put a space between the - and the number.");
        sender.sendMessage("The standard PEMDAS order of operations is followed.");
        sender.sendMessage(ChatColor.YELLOW + "Type /math <query> to calculate something!");
        return true;
    }
    
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < args.length; i++)
    {
        if(i != 0) sb.append(" ");
        sb.append(args[i]);
    }
    
    try{ sender.sendMessage(Double.toString(Maths.Calculate(sb.toString()))); }
    catch(MathSyntaxException e){ Messages.ReportMathSyntaxException(sender, e); }
    catch(Exception e){ Messages.ReportException(sender, e); }
    return true;
}
        // End
        return true;
    }
}