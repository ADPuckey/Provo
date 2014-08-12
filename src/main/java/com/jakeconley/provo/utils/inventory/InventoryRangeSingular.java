package com.jakeconley.provo.utils.inventory;

public class InventoryRangeSingular extends InventoryRange
{
    public InventoryRangeSingular(InventoryCoords v)
    {
        super(v, v);
        Type = Type.SINGULAR;
    }
    
    // No NextIndex() implementation needed, see super
    
    @Override
    public boolean Contains(InventoryCoords v){ return v.equals(Start) || v.equals(End); }
}
