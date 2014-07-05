package com.jakeconley.provo.functions.sorting;

import com.jakeconley.provo.utils.inventory.InventoryType;
import org.bukkit.Material;

public class PreferencesRule
{
    private int Priority;
    private InventoryType TargetType;
    private PreferencesRuleArea Area;
    private String ItemGroupName;
    
    public boolean MatchesMaterial(Material m)
    {
        if(ItemGroupName.equals("blocks_general")) return m.isBlock();
        if(ItemGroupName.equals("items_general")) return !(m.isBlock());
        if(ItemGroupName.equals("edible")) return m.isEdible();
        if(ItemGroupName.equals("flammable")) return m.isFlammable();
        if(ItemGroupName.equals("burnable")) return m.isBurnable();
        
        return false;
    }
}