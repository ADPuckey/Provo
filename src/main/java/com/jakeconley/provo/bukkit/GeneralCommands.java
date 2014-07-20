package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.Provo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GeneralCommands implements CommandExecutor
{
    private Provo plugin;
    public GeneralCommands(Provo _plugin){ this.plugin = _plugin; }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        return true;
    }
}