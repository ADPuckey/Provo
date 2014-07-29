package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.Provo;
import com.jakeconley.provo.backend.ProvoFormatException;
import com.jakeconley.provo.notifications.Notification;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsNotes implements CommandExecutor
{
    private final String NOTIFICATION_REMINDER_ID = "provo_reminder";
    
    private final Provo plugin;
    public CommandsNotes(Provo parent){ plugin = parent; }

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

if(label.equalsIgnoreCase("remind"))
{
    if(player == null){ Messages.Player(sender); return true; }
    if(!Verify.ArgsLength(cmd, sender, args, 1, cmd.getUsage())) return true;
    
    StringBuilder sb_message = new StringBuilder();
    for(int i = 0; i < args.length; i++)
    {
	if(i != 0) sb_message.append(' ');
	sb_message.append(args[i]);
    }
    
    try
    {
	if(plugin.getNotificationsBackend().NotificationExists(player_uuid, NOTIFICATION_REMINDER_ID))
	{
	    Notification n = plugin.getNotificationsBackend().GetNotification(player_uuid, NOTIFICATION_REMINDER_ID);
	    n.getText().add(0, sb_message.toString());
	    plugin.getNotificationsBackend().WriteNotification(player_uuid, n);
	    
	    sender.sendMessage(ChatColor.GREEN + "Successfully added reminder.");
	    return true;
	}
	else
	{
	    List<String> text = new LinkedList<>();
	    text.add(sb_message.toString());
	    text.add(ChatColor.YELLOW + "Use /del-reminders to acknowledge and delete these reminders.");
	    
	    Notification n = new Notification(NOTIFICATION_REMINDER_ID, "Reminder", text, Notification.Importance.IMPORTANT);
	    n.setAutoDelete(false);	    
	    plugin.getNotificationsBackend().WriteNotification(player_uuid, n);
	    
	    sender.sendMessage(ChatColor.GREEN + "Successfully created reminder.");
	    return true;
	}
    }
    catch(ProvoFormatException e){ Messages.ReportProvoFormatException(sender, e); return true; }
    catch(Exception e){ Messages.ReportException(sender, e); return true;}
}
if(label.equalsIgnoreCase("del-reminders"))
{
    try
    {
	plugin.getNotificationsBackend().DeleteNotification(player_uuid, NOTIFICATION_REMINDER_ID);
	sender.sendMessage(ChatColor.YELLOW + "Successfully deleted reminders.");
	return true;
    }
    catch(Exception e){ Messages.ReportException(sender, e); return true; }
}

	// End
	return true;
    }
}
