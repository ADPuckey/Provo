package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.Provo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SortingCommands implements CommandExecutor
{
    private Provo plugin;
    public SortingCommands(Provo _plugin){ plugin = _plugin; }
        
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(label.equalsIgnoreCase("sort"))
        {
            
        }
        return true;
    }
}