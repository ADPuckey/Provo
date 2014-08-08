package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.FunctionStatus;
import com.jakeconley.provo.Provo;
import com.jakeconley.provo.features.planning.Measuring;
import com.jakeconley.provo.features.planning.MeasuringOverflowException;
import com.jakeconley.provo.features.planning.MeasuringState;
import com.jakeconley.provo.features.sorting.PreferencesClass;
import com.jakeconley.provo.features.sorting.Sorting;
import com.jakeconley.provo.utils.inventory.InventoryType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

public class ListenerFunctions implements Listener
{
    private final Provo plugin;
    public ListenerFunctions(Provo _plugin){ plugin = _plugin; }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void OnPlayerInteract(PlayerInteractEvent event)
    {
        if(!event.hasBlock()) return;
        
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

switch(plugin.getPlayerStatus(event.getPlayer()))
{
    case SORTING_CHEST:
        if(event.getAction() == Action.LEFT_CLICK_BLOCK) event.setCancelled(true);

        Inventory inventory;

        if(event.getClickedBlock().getType() == Material.CHEST){ inventory = ((Chest) event.getClickedBlock().getState()).getInventory(); }
        else return;

        PreferencesClass pclass = plugin.getSortingCommandExecutor().getVerifiedClass(event.getPlayer());

        if(inventory instanceof DoubleChestInventory && pclass.getTargetType() != InventoryType.DOUBLECHEST)
            event.getPlayer().sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Notice:" + ChatColor.RESET + ChatColor.YELLOW + " You're applying a " + pclass.getTargetType().toString() + " class to a double chest; only the first 3 rows can follow the class's rules.");
        else if(!(inventory instanceof DoubleChestInventory) && pclass.getTargetType() != InventoryType.CHEST)
        {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You can't apply a " + pclass.getTargetType().toString() + " rule to a single chest!");
            plugin.getSortingCommandExecutor().resetVerifiedClass(event.getPlayer());
            plugin.setPlayerStatus(event.getPlayer(), FunctionStatus.IDLE);
            return;
        }

        Sorting.FrontendExecute(event.getPlayer(), inventory, pclass, plugin.getSortingPreferencesBackend());

        plugin.getSortingCommandExecutor().resetVerifiedClass(event.getPlayer());
        plugin.setPlayerStatus(event.getPlayer(), FunctionStatus.IDLE);
        return;


    case MEASURING_BLOCK:
        if(event.getAction() == Action.LEFT_CLICK_BLOCK) event.setCancelled(true);
        MeasuringState measuringstate = plugin.getPlanningCommandExecutor().getMeasuringState(player);
        
        if(!block.getLocation().getWorld().equals(measuringstate.getWorld()))
        {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't measure between worlds!");
            plugin.getPlanningCommandExecutor().removeMeasuringState(player);
            plugin.setPlayerStatus(player, FunctionStatus.IDLE);
            return;
        }
    
        measuringstate.getClickedLocations().add(block.getLocation());
        if(measuringstate.getClickedLocations().size() == 2)
        {
            try{ Measuring.FrontendExecute(Measuring.Calculate(measuringstate, player), player); }
            catch(MeasuringOverflowException e){ player.sendMessage(ChatColor.RED + "Calculation overflow!"); }
            
            plugin.getPlanningCommandExecutor().removeMeasuringState(player);
            plugin.setPlayerStatus(player, FunctionStatus.IDLE);
        }
        else player.sendMessage(ChatColor.YELLOW + "Click on your other corner.");
        //return;
    //default: return;
}
    }
}
