package com.jakeconley.provo.utils.inventory;

public class InventoryUtils
{
    public static final int ROW_LENGTH = 9;
    /**
     * Calculates a numeric index from a string slot designation, for example A1 returns 0 (the top right corner in chests)
     * @param s String designation
     * @param type Type of inventory
     * @return Numeric index of the slot, -1 if String s is invalid, -2 if out of range
     */
    public static int SlotIndexFromDesignation(String s, InventoryType type)
    {
        if(s.length() != 2) return -1;
        char[] split = s.toCharArray();
        
        int row = 0;//zero-indexed row
        int col = 0;//zero-indexed column
        
        /*
            Keep in mind, chest inventories are normal (index 0 is top right corner), but
            in player inventories index 0-8 are the hotbar and index 9 is the top right corner.
        */
        if(split[0] == 'a' || split[0] == 'A')
        {
            if(type == InventoryType.PLAYER) row = 1;// Correction
            else row = 0;
        }
        else if(split[0] == 'b' || split[0] == 'B')
        {
            if(type == InventoryType.PLAYER) row = 2;
            else row = 1;
        }
        else if(split[0] == 'c' || split[0] == 'C')
        {
            if(type == InventoryType.PLAYER) row = 3;
            else row = 2;
        }
        else if(split[0] == 'd' || split[0] == 'D')
        {
            if(type == InventoryType.PLAYER) row = 0;
            else if(type == InventoryType.DOUBLECHEST) row = 3;
            else return -2;// (Assumed) Single chests only have 3 rows
        }
        else if(split[0] == 'e' || split[0] == 'E')
        {
            if(type == InventoryType.DOUBLECHEST) row = 4;
            else return -2;// Only DoubleChests have more than 4 rows
        }
        else if(split[0] == 'f' || split[0] == 'F')
        {
            if(type == InventoryType.DOUBLECHEST) row = 5;
            else return -2;
        }
        else return -1;//if not a-f, invalid string
        
        try{ col = Integer.parseInt(Character.toString(split[1])); }
        catch(NumberFormatException e){ return -1;/* second char needs to be an int */ }
        
        col -= 1;//From nonzero index to zero index
        if(col < 0 || col > ROW_LENGTH) return -2;//Out of range
        
        int base = (row > 0 ? (row * ROW_LENGTH) - 1 : 0);
        return base + col;        
    }
}
