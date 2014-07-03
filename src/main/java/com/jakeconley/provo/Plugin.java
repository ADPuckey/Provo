package com.jakeconley.provo;

import com.jakeconley.provo.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin
{
    public static boolean Debug = false;
    
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
