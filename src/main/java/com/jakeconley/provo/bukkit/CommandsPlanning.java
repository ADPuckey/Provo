package com.jakeconley.provo.bukkit;
 
import com.jakeconley.provo.FunctionStatus;
import com.jakeconley.provo.Provo;
import com.jakeconley.provo.functions.planning.MeasuringState;
import com.jakeconley.provo.utils.Comparators;
import com.jakeconley.provo.utils.CoordinateAxis;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
 
public class CommandsPlanning implements CommandExecutor
{
    private final Map<Player, MeasuringState> MeasuringStates = new HashMap<>();
    public MeasuringState getMeasuringState(Player p){ return MeasuringStates.get(p); }
    public void removeMeasuringState(Player p){ MeasuringStates.remove(p); }
    
    private final Provo plugin;
    public CommandsPlanning(Provo parent){ plugin = parent; }
 
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

if(label.equalsIgnoreCase("measure"))
{
    if(player == null){ Messages.Player(sender); return true; }
    
    plugin.setPlayerStatus(player, FunctionStatus.MEASURING_BLOCK);
    MeasuringStates.put(player, new MeasuringState(player.getLocation().getWorld()));
    player.sendMessage(ChatColor.YELLOW + "Click on a corner of the area you'd like to measure.");
    return true;
}

        // End
        return true;
    }
}