package com.jakeconley.provo.utils.inventory;

import static com.jakeconley.provo.utils.inventory.InventoryUtils.ROW_LENGTH;

public enum InventoryType
{
    PLAYER(4), CHEST(3), DOUBLECHEST(3);
    
    public static final InventoryType MAX = InventoryType.DOUBLECHEST;//largest type
    
    private final int Rows;
    public int getCapacity(){ return Rows * ROW_LENGTH; }
    public int getRowCount(){ return Rows; }
    
    InventoryType(int rows){ Rows = rows; }
}