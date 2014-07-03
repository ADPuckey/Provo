package com.jakeconley.provo.functions.sorting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Sorting
{
    public static SortingState SortInventory(Inventory i)
    {
        HashMap<PreferencesRule,LinkedList<ItemStack>> matches = new HashMap<>();
        
        return SortingState.SUCCESSFUL;
    }
}
