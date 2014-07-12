package com.jakeconley.provo.utils.inventory;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils
{
    public static final int ROW_LENGTH = 9;
    
    public static List<ItemStack> CollapseInventory(ItemStack[] inventory)
    {
        List<ItemStack> l = new LinkedList<>();
        for(ItemStack is : inventory) if(is != null) l.add(is);
        return l;
    }
}
