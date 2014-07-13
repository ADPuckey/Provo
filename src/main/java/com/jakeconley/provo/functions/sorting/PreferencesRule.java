package com.jakeconley.provo.functions.sorting;

import com.jakeconley.provo.utils.inventory.InventoryType;
import com.jakeconley.provo.utils.inventory.InventoryRange;
import org.bukkit.Material;

public class PreferencesRule
{
    private int Priority;
    private InventoryType TargetType;
    private InventoryRange TargetArea;
    private String ItemGroupName;
    
    public boolean MatchesMaterial(Material m)
    {
        if(ItemGroupName.equals("blocks")) return m.isBlock();
        if(ItemGroupName.equals("items")) return !(m.isBlock());
        if(ItemGroupName.equals("edible")) return m.isEdible();
        if(ItemGroupName.equals("flammable")) return m.isFlammable();
        if(ItemGroupName.equals("burnable")) return m.isBurnable();
        
        return false;
    }
}