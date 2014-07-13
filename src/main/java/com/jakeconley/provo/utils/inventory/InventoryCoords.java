package com.jakeconley.provo.utils.inventory;

import static com.jakeconley.provo.utils.inventory.InventoryUtils.ROW_LENGTH;

/**
 * Class for supporting InventoryTweaks-style coordinates.  E.g. D1 for the first hotbar space in a player's inventory
 * @author jake
 */
public class InventoryCoords
{
    private char RowLetter;
    private int RowNumber;//Zero-indexed
    private int ColumnNumber;//NOT zero-indexed
    
    public char getRowLetter(){ return RowLetter; }
    public int getRowNumber(){ return RowNumber; }
    public int getColumnNumber(){ return ColumnNumber; }
    public InventoryCoords(char _RowLetter, int _RowNumber, int _ColumnNumber)
    {
        this.RowLetter = _RowLetter;
        this.RowNumber = _RowNumber;
        this.ColumnNumber = _ColumnNumber;
    }
    
    
    /*
        Keep in mind, chest inventories are normal (index 0 is top right corner), but
        in player inventories index 0-8 are the hotbar and index 9 is the top right corner.
        See card https://trello.com/c/CllYsKLq for more
    */
    public static InventoryCoords FromIndex(int index, InventoryType type)
    {
        int row_i;
        int col_i;
        col_i = (index % ROW_LENGTH);
        row_i = (index - col_i) / ROW_LENGTH;
        col_i += 1;//un-zero-index this
        
        char row = '_';// for some shitty reason i get "variable might not have been initialized" errors i hate java
        if(type == InventoryType.PLAYER)
        {
            switch(row_i)
            {
                case 0: row = 'D'; break;
                case 1: row = 'A'; break;
                case 2: row = 'B'; break;
                case 3: row = 'C'; break;
                default: return null;
            }
        }
        else if(type == InventoryType.DOUBLECHEST || type == InventoryType.CHEST)
        {
            switch(row_i)
            {
                case 0: row = 'A'; break;
                case 1: row = 'B'; break;
                case 2: row = 'C'; break;
                default: if(type != InventoryType.DOUBLECHEST) return null;
            }
            switch(row_i)
            {
                case 3: row = 'D';
                case 4: row = 'E';
                case 5: row = 'F';
                default: return null;
            }
        }
        
        return new InventoryCoords(row, row_i, col_i);
    }
    public static InventoryCoords FromString(String s, InventoryType type)
    {
        if(s.length() != 2) return null;
        char[] split = s.toUpperCase().toCharArray();
        
        int row = 0;//zero-indexed row
        int col = 0;//non-zero-indexed column to be parsed later
        if(split[0] == 'A')
        {
            if(type == InventoryType.PLAYER) row = 1;// Correction
            else row = 0;
        }
        else if(split[0] == 'B')
        {
            if(type == InventoryType.PLAYER) row = 2;
            else row = 1;
        }
        else if(split[0] == 'C')
        {
            if(type == InventoryType.PLAYER) row = 3;
            else row = 2;
        }
        else if(split[0] == 'D')
        {
            if(type == InventoryType.PLAYER) row = 0;
            else if(type == InventoryType.DOUBLECHEST) row = 3;
            else return null;// (Assumed) Single chests only have 3 rows
        }
        else if(split[0] == 'E')
        {
            if(type == InventoryType.DOUBLECHEST) row = 4;
            else return null;// Only DoubleChests have more than 4 rows
        }
        else if(split[0] == 'F')
        {
            if(type == InventoryType.DOUBLECHEST) row = 5;
            else return null;
        }
        else return null;//if not a-f, invalid string
        
        try{ col = Integer.parseInt(Character.toString(split[1])); }
        catch(NumberFormatException e){ return null;/* second char needs to be an int */ }        
        if(col < 1 || col > ROW_LENGTH) return null;//Out of range
        
        return new InventoryCoords(split[0], row, col);
    }
    
    public int getIndex()
    {        
        int base = (RowNumber > 0 ? (RowNumber * ROW_LENGTH) - 1 : 0);
        return base + ColumnNumber;      
    }
    @Override public String toString(){ return RowLetter + Integer.toString(ColumnNumber); }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.RowNumber;
        hash = 73 * hash + this.ColumnNumber;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final InventoryCoords other = (InventoryCoords) obj;
        
        if (this.RowNumber != other.RowNumber) return false;
        if (this.ColumnNumber != other.ColumnNumber) return false;
        return true;
    }
}