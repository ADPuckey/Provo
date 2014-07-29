package com.jakeconley.provo.bukkit;

import com.jakeconley.provo.FunctionStatus;
import com.jakeconley.provo.Provo;
import com.jakeconley.provo.functions.sorting.PreferencesClass;
import com.jakeconley.provo.functions.sorting.Sorting;
import com.jakeconley.provo.utils.inventory.InventoryType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
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
    public void OnPlayerInteract(PlayerInteractEvent e)
    {
        if(!e.hasBlock()) return;

        switch(plugin.getPlayerStatus(e.getPlayer()))
        {
            case SORTING_CHEST:
                if(e.getAction() == Action.LEFT_CLICK_BLOCK) e.setCancelled(true);
                
                Inventory inventory;
                
                if(e.getClickedBlock().getType() == Material.CHEST){ inventory = ((Chest) e.getClickedBlock().getState()).getInventory(); }
                else return;
                
                PreferencesClass pclass = plugin.getSortingCommandExecutor().getVerifiedClass(e.getPlayer());
                
                if(inventory instanceof DoubleChestInventory && pclass.getTargetType() != InventoryType.DOUBLECHEST)
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Notice:" + ChatColor.RESET + ChatColor.YELLOW + " You're applying a " + pclass.getTargetType().toString() + " class to a double chest; only the first 3 rows can follow the class's rules.");
                else if(!(inventory instanceof DoubleChestInventory) && pclass.getTargetType() != InventoryType.CHEST)
                {
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "You can't apply a " + pclass.getTargetType().toString() + " rule to a single chest!");
                    plugin.getSortingCommandExecutor().resetVerifiedClass(e.getPlayer());
                    plugin.setPlayerStatus(e.getPlayer(), FunctionStatus.IDLE);
                    return;
                }
                
                Sorting.FrontendExecute(e.getPlayer(), inventory, pclass, plugin.getSortingPreferencesBackend());
                
                plugin.getSortingCommandExecutor().resetVerifiedClass(e.getPlayer());
                plugin.setPlayerStatus(e.getPlayer(), FunctionStatus.IDLE);
                //return;
            //default: return;
        }
    }
}
