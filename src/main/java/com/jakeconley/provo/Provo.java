package com.jakeconley.provo;

import com.jakeconley.provo.backend.SortingPreferencesBackend;
import com.jakeconley.provo.bukkit.*;
import com.jakeconley.provo.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

public class Provo extends JavaPlugin
{
    public static boolean Debug = true;
    
    private SortingPreferencesBackend SortingPreferencesBackend = new SortingPreferencesBackend();
    public SortingPreferencesBackend getSortingPreferencesBackend(){ return new SortingPreferencesBackend(); }
    
    private GeneralCommands _GeneralCommands = new GeneralCommands(this);
    private SortingCommands _SortingCommands = new SortingCommands(this);
    
    @Override
    public void onEnable()
    {
        getCommand("sort").setExecutor(_SortingCommands);
        getCommand("sorting").setExecutor(_SortingCommands);
        
        Utils.Info("Version " + this.getDescription().getVersion() + " enabled.");
    }
    
    @Override
    public void onDisable()
    {
        Utils.Info("Plugin disabled.");
    }
}
