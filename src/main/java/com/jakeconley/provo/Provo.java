package com.jakeconley.provo;

import com.jakeconley.provo.backend.SortingPreferencesBackend;
import com.jakeconley.provo.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

public class Provo extends JavaPlugin
{
    public static boolean Debug = true;
    
    private SortingPreferencesBackend SortingPreferencesBackend = new SortingPreferencesBackend();
    public SortingPreferencesBackend getSortingPreferencesBackend(){ return new SortingPreferencesBackend(); }
    
    @Override
    public void onEnable()
    {
        Utils.Info("Version " + this.getDescription().getVersion() + " enabled.");
    }
    
    @Override
    public void onDisable()
    {
        Utils.Info("Plugin disabled.");
    }
}
