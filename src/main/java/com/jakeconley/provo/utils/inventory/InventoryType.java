package com.jakeconley.provo.utils.inventory;

public enum InventoryType
{
    PLAYER(4 * 9), CHEST(3 * 9), DOUBLECHEST(6 * 9);
    
    public static final InventoryType MAX = InventoryType.DOUBLECHEST;//largest type
    
    private final int Size;
    public int getCapacity(){ return Size; }
    
    InventoryType(int size){ Size = size; }
}