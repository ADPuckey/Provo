package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.Provo;
import com.jakeconley.provo.backend.ProvoFormatException;
import com.jakeconley.provo.notifications.Notification;
import com.jakeconley.provo.utils.Utils;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ListenerNotifications implements Listener
{
    private final Provo plugin;
    public ListenerNotifications(Provo _plugin){ plugin = _plugin; }
    
    // Show a player their notifications.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        try
        {
            boolean hasdisplayed = false;
            int morecount = 0;
            List<String> toDelete = new LinkedList<>();
            
            List<Notification> Notifications = plugin.getNotificationsBackend().GetNotifications(event.getPlayer().getUniqueId().toString());
            for(Notification n : Notifications)
            {
                Utils.Debug(n.toString());
                if(n.getImportance() == Notification.Importance.DISPLAY_ON_JOIN || n.getImportance() == Notification.Importance.IMPORTANT)
                {
                    for(String s : n.toPlayerFriendlyStringList()) event.getPlayer().sendMessage(s);
                    if(n.isAutoDelete()) toDelete.add(n.getId());
                    hasdisplayed = true;
                }
                else morecount++;
            }
            
            if(morecount > 0)
            {
                String msg = ChatColor.YELLOW + "You have " + Integer.toString(morecount);
                if(hasdisplayed) msg += " more";
                msg += " notification(s), use \"/view-notifications\" to view them.";
                event.getPlayer().sendMessage(msg);
            }
            
            plugin.getNotificationsBackend().DeleteNotificationList(event.getPlayer().getUniqueId().toString(), toDelete);
        }
        catch(ProvoFormatException e){ Messages.ReportProvoFormatException(event.getPlayer(), e); }
        catch(Exception e){ Messages.ReportException(event.getPlayer(), e); }
    }
}
