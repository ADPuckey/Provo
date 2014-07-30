package com.jakeconley.provo.bukkit;
 
import com.jakeconley.provo.Provo;
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
 
        // End
        return true;
    }
}