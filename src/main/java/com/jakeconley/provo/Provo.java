package com.jakeconley.provo;

import com.jakeconley.provo.backend.SortingPreferencesBackend;
import com.jakeconley.provo.bukkit.*;
import com.jakeconley.provo.notifications.Notification;
import com.jakeconley.provo.notifications.NotificationsBackend;
import com.jakeconley.provo.utils.Utils;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Provo extends JavaPlugin implements Listener
{
    public static String COMMAND_TRADEMARK = org.bukkit.ChatColor.GREEN + "-- Provo by HAPPYGOPUCKEY --";
    
    public static boolean Debug = true;
    
    private final SortingPreferencesBackend SortingPreferencesBackend = new SortingPreferencesBackend();
    private final NotificationsBackend NotificationsBackend = new NotificationsBackend();
    public SortingPreferencesBackend getSortingPreferencesBackend(){ return new SortingPreferencesBackend(); }
    public NotificationsBackend getNotificationsBackend(){ return new NotificationsBackend(); }
    
    private final HashMap<Player, FunctionStatus> PlayerStatuses = new HashMap<>();
    public FunctionStatus getPlayerStatus(Player p){ return PlayerStatuses.get(p); }
    public void setPlayerStatus(Player p, FunctionStatus value){ PlayerStatuses.put(p, value); }
    
    // Automatically put IDLE on each join
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent event){ PlayerStatuses.put(event.getPlayer(), FunctionStatus.IDLE); }
    
    private Settings Settings;
    public Settings getSettings(){ return Settings; }
    
    private final CommandsGeneral GeneralCommands   = new CommandsGeneral(this);
    private final CommandsSorting SortingCommands   = new CommandsSorting(this);
    private final CommandsNotes NotesCommands	    = new CommandsNotes(this);
    private final CommandsMath MathCommands         = new CommandsMath(this);
    private final CommandsPlugin PluginCommands     = new CommandsPlugin(this);
    public CommandsSorting getSortingCommandExecutor(){ return SortingCommands; }
    
    @Override
    public void onEnable()
    {
        Settings = new Settings();
        Settings.LoadFile(true);
        
        getCommand("sort").setExecutor(SortingCommands);
        getCommand("sorting").setExecutor(SortingCommands);
        getCommand("sortinginfo").setExecutor(SortingCommands);
        getCommand("view-notifications").setExecutor(GeneralCommands);
        getCommand("math").setExecutor(MathCommands);
	getCommand("remind").setExecutor(NotesCommands);
	getCommand("del-reminders").setExecutor(NotesCommands);
        getCommand("recipe").setExecutor(GeneralCommands);
        getCommand("unenchant").setExecutor(GeneralCommands);
        
	// Initialization of player statuses
        for(Player p : getServer().getOnlinePlayers()){ PlayerStatuses.put(p, FunctionStatus.IDLE); }
        
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ListenerFunctions(this), this);
        getServer().getPluginManager().registerEvents(new ListenerNotifications(this), this);
        
        Utils.Info("Version " + this.getDescription().getVersion() + " enabled.");
    }
    
    @Override
    public void onDisable()
    {
        Utils.Info("Plugin disabled.");
    }
    
    public void SendNotification(Player p, Notification n)
    {
        if(p.isOnline())
        {
            for(String line : n.getText()) p.sendMessage(line);
            return;
        }
        
        try
        {
            NotificationsBackend.WriteNotification(p.getUniqueId().toString(), n);
        }
        catch(Exception e)
        {
            Utils.LogException("sending a notification to player " + p.getName(), e);
        }
    }
}
