package com.jakeconley.provo.functions.sorting;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Sorting
{
    public static SortingState SortInventory(Inventory i)
    {
        if(i instanceof PlayerInventory)
        {
            PlayerInventory pi = (PlayerInventory) i;
            
            // Represents the strongest of each armor type
            ItemStack sHelmet = null;
            ItemStack sChestplate = null;
            ItemStack sLeggings = null;
            ItemStack sBoots = null;
        }
        
        return SortingState.SUCCESSFUL;
    }
}