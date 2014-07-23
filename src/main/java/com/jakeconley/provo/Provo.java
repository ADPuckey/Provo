package com.jakeconley.provo;

import com.jakeconley.provo.backend.SortingPreferencesBackend;
import com.jakeconley.provo.bukkit.*;
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
    
    private SortingPreferencesBackend SortingPreferencesBackend = new SortingPreferencesBackend();
    public SortingPreferencesBackend getSortingPreferencesBackend(){ return new SortingPreferencesBackend(); }
    
    private final HashMap<Player, FunctionStatus> PlayerStatuses = new HashMap<>();
    public FunctionStatus getPlayerStatus(Player p){ return PlayerStatuses.get(p); }
    public void setPlayerStatus(Player p, FunctionStatus value){ PlayerStatuses.put(p, value); }
    
    // Automatically put IDLE on each join
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent event){ PlayerStatuses.put(event.getPlayer(), FunctionStatus.IDLE); }
    
    private CommandsGeneral _GeneralCommands = new CommandsGeneral(this);
    private CommandsSorting _SortingCommands = new CommandsSorting(this);
    
    @Override
    public void onEnable()
    {
        getCommand("sort").setExecutor(_SortingCommands);
        getCommand("sorting").setExecutor(_SortingCommands);
        getCommand("sortinginfo").setExecutor(_SortingCommands);
        
        Utils.Info("Version " + this.getDescription().getVersion() + " enabled.");
    }
    
    @Override
    public void onDisable()
    {
        Utils.Info("Plugin disabled.");
    }
}
