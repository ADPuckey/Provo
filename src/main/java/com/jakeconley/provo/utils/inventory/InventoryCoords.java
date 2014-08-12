package com.jakeconley.provo.utils.inventory;

import com.jakeconley.provo.utils.RelativeDirection;
import static com.jakeconley.provo.utils.inventory.InventoryUtils.ROW_LENGTH;

/**
 * Class for supporting InventoryTweaks-style coordinates.  E.g. D1 for the first hotbar space in a player's inventory
 * @author jake
 */
public class InventoryCoords
{
    // This is some of the ugliest code i've ever written, soz, but it should get the job done

    /*
        Thanks to the minecraft inventory system we have to have these "Actual" properties
        Actual properties are how it appears
        GameIndex is how it is in the game, ActualIndex is how it appears
        * RowLetter is ACTUAL
        * GameIndex is NOT ACTUAL
        * RowNumber is ACTUAL
        * ColumnNumber is BOTH
        * ActualIndex is obviously ACTUAL
    
        -- THESE PROPERTIES ARE NEEDED FOR THE RANGE SYSTEM
    */
    private final char RowLetter;
    private final int GameIndex;
    private final int RowNumber;//Zero-indexed
    private final int ColumnNumber;//NOT zero-indexed
    private final int ActualIndex;
    
    public char getRowLetter(){ return RowLetter; }
    public int getRowNumber(){ return RowNumber; }
    public int getColumnNumber(){ return ColumnNumber; }
    public int getGameIndex(){ return GameIndex; }
    public int getDisplayIndex(){ return ActualIndex; }
    
    public InventoryCoords(char _RowLetter, int _RowNumber, int _ColumnNumber, int _Index, int _ActualIndex)
    {
        this.RowLetter = _RowLetter;
        this.RowNumber = _RowNumber;
        this.ColumnNumber = _ColumnNumber;
        this.GameIndex = _Index;
        this.ActualIndex = _ActualIndex;
    }
    
    private static int CalculateIndex(int row_i, int col_i)
    {        
        int base = row_i * ROW_LENGTH;
        return base + col_i - 1;
    }
    
    public InventoryCoords GetRelative(RelativeDirection direction, int count, InventoryType type)
    {
        //ugh switch statement scope...
        char newletter;
        int newrow;
        int newcol;
        switch(direction)
        {
            case UP:
                newrow = RowNumber - count;
                if(newrow < 0) return null;// Range checking

                newletter = (char) (RowLetter - 1);// hacky ass char conversion lol efficient tho
                if(newletter < '\u0041') return null;
                
                return FromIntCoords(newletter, newrow, ColumnNumber, type);
            case DOWN:
                newrow = RowNumber + count;
                if(newrow >= type.getRowCount()) return null;
                
                newletter = (char) (RowLetter + 1);
                if(newletter >= ((char) ('\u0041' + type.getRowCount()))) return null;
                
                return FromIntCoords(newletter, newrow, ColumnNumber, type);
            case LEFT:
                newcol = ColumnNumber - count;
                if(newcol < 1) return null;
                
                return FromIntCoords(RowLetter, RowNumber, newcol, type);
            case RIGHT:
                newcol = ColumnNumber + count;
                if(newcol > ROW_LENGTH) return null;
                
                return FromIntCoords(RowLetter, RowNumber, newcol, type);
                
            default: return null;
        }
    }
    
    
    /*
        Keep in mind, chest inventories are normal (index 0 is top left corner), but
        in player inventories index 0-8 are the hotbar and index 9 is the top left corner.
        See card https://trello.com/c/CllYsKLq for more
    */
    public static InventoryCoords FromIndex(int index, InventoryType type)
    {
        int row_i;
        int col_i;
        col_i = (index % ROW_LENGTH);
        row_i = (index - col_i) / ROW_LENGTH;
        col_i += 1;//un-zero-index this
        
        
        int row_actual = row_i;// Corrective player inventory stuff
        
        char row = '_';// for some shitty reason i get "variable might not have been initialized" errors i hate java
        if(type == InventoryType.PLAYER)
        {
            switch(row_i)
            {
                // Gotta do the corrective stuff with row_actual
                case 0: row = 'D'; row_actual = 3; break;
                case 1: row = 'A'; row_actual = 0; break;
                case 2: row = 'B'; row_actual = 1; break;
                case 3: row = 'C'; row_actual = 2; break;
                default: return null;
            }
        }
        else
        {
            // A little inconsistent and hacky but eh
            boolean matched = false;//who needs goto statements anyways
            switch(row_i)
            {
                case 0: row = 'A'; matched = true; break;
                case 1: row = 'B'; matched = true; break;
                case 2: row = 'C'; matched = true; break;
            }
            if(!matched && type == InventoryType.DOUBLECHEST)
            {
                switch(row_i)
                {
                    case 3: row = 'D'; break;
                    case 4: row = 'E'; break;
                    case 5: row = 'F'; break;
                    default: return null;
                }
            }
            else if(!matched) return null;
        }
        
        int index_actual = CalculateIndex(row_actual, col_i);
        return new InventoryCoords(row, row_actual, col_i, index, index_actual);
    }
    public static InventoryCoords FromIntCoords(char row_char, int row_actual, int col_i, InventoryType type)
    {
        int row_i;
        
        // Corrections for PLAYER inventories
        if(type == InventoryType.PLAYER)
        {
            switch(row_actual)
            {
                case 0: row_i = 1; break;
                case 1: row_i = 2; break;
                case 2: row_i = 3; break;
                case 3: row_i = 0; break;
                default: return null;
            }
        }        
        else row_i = row_actual;
        
        int index = CalculateIndex(row_i, col_i);
        int index_actual = CalculateIndex(row_actual, col_i);
        
        return new InventoryCoords(row_char, row_actual, col_i, index, index_actual);
    }
    public static InventoryCoords FromString(String s, InventoryType type)
    {
        if(s.length() != 2) return null;
        char[] split = s.toUpperCase().toCharArray();
        
        int col_i = 0;//non-zero-indexed column to be parsed later
        int row_actual = 0;
        if(split[0] == 'A')
        {
            row_actual = 0;
        }
        else if(split[0] == 'B')
        {
            row_actual = 1;
        }
        else if(split[0] == 'C')
        {
            row_actual = 2;
        }
        else if(split[0] == 'D')
        {
            row_actual = 3;
            // Below code not needed cause minimum inventory size has 3 rows...
            if(type.getRowCount() < 4) return null;
        }
        else if(split[0] == 'E')
        {
            row_actual = 4;
            if(type.getRowCount() < 5) return null;
        }
        else if(split[0] == 'F')
        {
            row_actual = 5;
            if(type.getRowCount() < 5) return null;
        }
        else return null;//if not a-f, invalid string
        
        try{ col_i = Integer.parseInt(Character.toString(split[1])); }
        catch(NumberFormatException e){ return null;/* second char needs to be an int */ }        
        if(col_i < 1 || col_i > ROW_LENGTH) return null;//Out of range
        
        return FromIntCoords(split[0], row_actual, col_i, type);
    }
    
    @Override public String toString(){ return RowLetter + Integer.toString(ColumnNumber); }

    // Netbeans-generated
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.GameIndex;
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InventoryCoords other = (InventoryCoords) obj;
        if (this.GameIndex != other.GameIndex) {
            return false;
        }
        return true;
    }

}