package com.jakeconley.provo.utils.inventory;

import static com.jakeconley.provo.utils.inventory.InventoryUtils.ROW_LENGTH;

/**
 * Enum of types of sortable inventories, mapped with their capacities.
 * @author jake
 * @deprecated Phasing out this enum in favor of flexible sorting and usage of the bukkit enums.  Keeping it here to transition from /sort to /sort-chest.
 */
public enum InventoryType
{
    PLAYER(4), CHEST(3), DOUBLECHEST(6);
    
    public static final InventoryType MAX = InventoryType.DOUBLECHEST;//largest type
    
    private final int Rows;
    public int getCapacity(){ return Rows * ROW_LENGTH; }
    public int getRowCount(){ return Rows; }
    
    InventoryType(int rows){ Rows = rows; }
}