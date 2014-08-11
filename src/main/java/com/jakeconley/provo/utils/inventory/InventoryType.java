package com.jakeconley.provo.utils.inventory;

public enum InventoryType
{
    PLAYER(4 * 9, 4), CHEST(3 * 9, 3), DOUBLECHEST(6 * 9, 3);
    
    public static final InventoryType MAX = InventoryType.DOUBLECHEST;//largest type
    
    private final int Size;
    private final int Rows;
    public int getCapacity(){ return Size; }
    public int getRowCount(){ return Rows; }
    
    InventoryType(int size, int rows){ Size = size; Rows = rows; }
}