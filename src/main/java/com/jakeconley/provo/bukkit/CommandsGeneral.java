package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.Provo;
import com.jakeconley.provo.backend.ProvoFormatException;
import com.jakeconley.provo.notifications.Notification;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsGeneral implements CommandExecutor
{
    private Provo plugin;
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
            player.sendMessage(ChatColor.AQUA + "You have " + notifications.size() + " notifications:");
            for(Notification n : notifications){ player.sendMessage(n.toPlayerFriendlyString()); }
            
            plugin.getNotificationsBackend().ClearNotifications(player_uuid);
        }
        else player.sendMessage(ChatColor.YELLOW + "You don't have any notifications!");
    }
    catch(ProvoFormatException e){ Messages.ReportProvoFormatException(sender, e); }
    catch(Exception e){ Messages.ReportException(sender, e); }
    return true;
}

        // End
        return true;
    }
}