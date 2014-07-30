package com.jakeconley.provo.bukkit;
 
import com.jakeconley.provo.Provo;
import com.jakeconley.provo.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
 
public class CommandsPlugin implements CommandExecutor
{
    private final Provo plugin;
    public CommandsPlugin(Provo parent){ plugin = parent; }
 
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
 if(label.equalsIgnoreCase("provo"))
 {
     sender.sendMessage(Provo.COMMAND_TRADEMARK);
     sender.sendMessage(ChatColor.GREEN + "List of features:");
     sender.sendMessage(ChatColor.YELLOW + "Things in <> are required, things in [] are optional, things in () depend.  Replace them as necessary.");
     Utils.ShowCommand(sender, "/sort", "Sort an inventory!  Use /sorting help and /sortinginfo for more.");
     Utils.ShowCommand(sender, "/math <math>", "Do some math. Use /math help for more info.");
     Utils.ShowCommand(sender, "/measure", "Measure distance, area, perimiter, etc between two blocks.");
     Utils.ShowCommand(sender, "/mail send <recipient> <message...>", "Send mail to a player!");
     Utils.ShowCommand(sender, "/remind <message>...", "Reminds you to do something next time you log in.");
     Utils.ShowCommand(sender, "/recipe <item>", "Show the crafting recipe for a particular item.");
     Utils.ShowCommand(sender, "/unenchant", "Removes ALL enchantments on the item in your hand.");
     return true;
 }
        // End
        return true;
    }
}