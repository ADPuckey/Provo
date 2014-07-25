package com.jakeconley.provo.utils.inventory;

public enum InventoryType
{
    PLAYER, CHEST, DOUBLECHEST;
    
    public static final InventoryType MAX = InventoryType.DOUBLECHEST;//largest type
}