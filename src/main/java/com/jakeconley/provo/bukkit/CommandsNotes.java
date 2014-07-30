package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.Provo;
import com.jakeconley.provo.backend.ProvoFormatException;
import com.jakeconley.provo.functions.mail.Mail;
import com.jakeconley.provo.functions.mail.MailResult;
import com.jakeconley.provo.notifications.Notification;
import com.jakeconley.provo.utils.Utils;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsNotes implements CommandExecutor
{
    private final String NOTIFICATION_REMINDER_ID = "provo_reminder";
    private final String NOTIFICATION_MAIL_ID = "provo_mail";
    
    private final Provo plugin;
    public CommandsNotes(Provo parent){ plugin = parent; }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
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
            n.removeTimestamp();
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

if(label.equalsIgnoreCase("mail"))
{
    if(!Verify.ArgsLength(cmd, sender, args, 1, cmd.getUsage())) return true;
    
    if(args[0].equalsIgnoreCase("help"))
    {
        sender.sendMessage(Provo.COMMAND_TRADEMARK);
        sender.sendMessage(ChatColor.GREEN + "Mail help:");
        Utils.ShowCommand(sender, "/mail check", "Checks your mail.");
        Utils.ShowCommand(sender, "/mail send <playername> <message...>", "Sends a message.");
        return true;
    }
    if(args[0].equalsIgnoreCase("send"))
    {
        if(player == null){ Messages.Player(sender); return true; }
        if(!Verify.ArgsLength(cmd, sender, args, 3, "/mail send <playername> <message...>")) return true;
        if(!Verify.Permission(plugin, sender, "provo.mail.send", true)) return true;
        
        final String target_name = args[1];
        
        final StringBuilder sb = new StringBuilder();
        for(int i = 2; i < args.length; i++)
        {
            if(i != 2) sb.append(' ');
            sb.append(args[i]);
        }
        
        // Asynchronous UUID resolution
        plugin.getServer().getScheduler().runTask(plugin, new Runnable(){ @Override public void run()
        {
            Player player = (Player) sender;
            
            Player recipient = plugin.getServer().getPlayer(target_name);
            UUID recipient_uuid;
            if(recipient == null)
            {
                recipient_uuid = Utils.ResolveUUID(target_name);
                if(recipient_uuid == null)
                {
                    player.sendMessage(ChatColor.RED + "Couldn't find a UUID for player \"" + args[1] + "\"!");
                    return;
                }
                recipient = plugin.getServer().getPlayer(recipient_uuid);
            }
            else recipient_uuid = recipient.getUniqueId();

            

            Mail mail = new Mail(player.getUniqueId(), sb.toString());
            try
            {
                plugin.getMailBackend().SendMail(recipient_uuid.toString(), mail);
                player.sendMessage(ChatColor.GREEN + "Sent mail to " + (recipient != null ? "player " + recipient.getName() : "ID " + recipient_uuid.toString()) + ".");
            }
            catch(Exception e){ Messages.ReportException(sender, e); return; }

            plugin.SendNotification(recipient_uuid, NOTIFICATION_MAIL_ID, "Mail", Arrays.asList("You've got mail!  Use /mail check to view."), Notification.Importance.DISPLAY_ON_JOIN);
        }});

        return true;
    }
    if(args[0].equalsIgnoreCase("check"))
    {
        if(player == null){ Messages.Player(sender); return true; }
        
        // Asynchronous UUID resolution
        plugin.getServer().getScheduler().runTask(plugin, new Runnable(){ @Override public void run()
        {
            Player player = (Player) sender;
            String player_uuid = player.getUniqueId().toString();
            
            try
            {
                MailResult res = plugin.getMailBackend().LoadMail(player_uuid);

                if(res.getMails().isEmpty())
                {
                    player.sendMessage(ChatColor.YELLOW + "You don't have any mail.");
                    return;
                }
                else
                {
                    player.sendMessage(ChatColor.GREEN + "You have " + res.getMails().size() + " message(s):");
                    for(String s : res.MakePlayerStrings()) player.sendMessage(s);
                }

                plugin.getNotificationsBackend().DeleteNotification(player_uuid, NOTIFICATION_MAIL_ID);
                plugin.getMailBackend().ClearMail(player_uuid);
            }
            catch(ProvoFormatException e){ Messages.ReportProvoFormatException(sender, e); return;}
            catch(Exception e){ Messages.ReportException(sender, e); return; }
        }});
        
        return true;
    }
    
    Messages.Usage(sender, cmd.getUsage());
    return true;
}

	// End
	return true;
    }
}
