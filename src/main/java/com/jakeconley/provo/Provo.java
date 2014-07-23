package com.jakeconley.provo;

import com.jakeconley.provo.backend.SortingPreferencesBackend;
import com.jakeconley.provo.bukkit.*;
import com.jakeconley.provo.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

public class Provo extends JavaPlugin
{
    public static String COMMAND_TRADEMARK = org.bukkit.ChatColor.GREEN + "-- Provo by HAPPYGOPUCKEY --";
    
    public static boolean Debug = true;
    
    private SortingPreferencesBackend SortingPreferencesBackend = new SortingPreferencesBackend();
    public SortingPreferencesBackend getSortingPreferencesBackend(){ return new SortingPreferencesBackend(); }
    
    private CommandsGeneral _GeneralCommands = new CommandsGeneral(this);
    private CommandsSorting _SortingCommands = new CommandsSorting(this);
    
    @Override
    public void onEnable()
    {
        getCommand("sort").setExecutor(_SortingCommands);
        getCommand("sorting").setExecutor(_SortingCommands);
        getCommand("sortinghelp").setExecutor(_SortingCommands);
        
        Utils.Info("Version " + this.getDescription().getVersion() + " enabled.");
    }
    
    @Override
    public void onDisable()
    {
        Utils.Info("Plugin disabled.");
    }
}
