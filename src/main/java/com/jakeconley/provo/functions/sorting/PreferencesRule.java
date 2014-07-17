package com.jakeconley.provo.functions.sorting;

import com.jakeconley.provo.utils.inventory.InventoryRange;
import com.jakeconley.provo.utils.inventory.InventoryType;
import org.bukkit.Material;

public class PreferencesRule
{
    private int Priority;
    private InventoryRange TargetArea;
    private String ItemGroupName;
    
    public int getPriority(){ return this.Priority; }
    public InventoryRange getTargetArea(){ return this.TargetArea; }
    public String getItemGroupName(){ return this.ItemGroupName; }
    public void setPriority(int value){ this.Priority = value; }
    public void setTargetArea(InventoryRange value){ this.TargetArea = value; }
    public void setItemGroupName(String value){ this.ItemGroupName = value; }
    
    public PreferencesRule(int _Priority, InventoryRange _TargetArea, String _ItemGroupName)
    {
        Priority = _Priority;
        TargetArea = _TargetArea;
        ItemGroupName = _ItemGroupName;;
    }

    
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