package com.jakeconley.provo.utils.inventory;

public class InventoryRangeWraparound extends InventoryRange
{
    public InventoryRangeWraparound(InventoryCoords start, InventoryCoords end)
    {
        super(start, end);
        this.Type = Type.WRAPAROUND;
    }
    
    @Override
    public boolean NextIndex()
    {
        if(super.NextIndex()) return true;
        
        int newindex = CurrentIndex;
        
        if(End.getDisplayIndex() > Start.getDisplayIndex())
        {
            newindex++;
            if(newindex > End.getDisplayIndex()) return false;
            CurrentIndex = newindex;
            return true;
        }
        else if(Start.getDisplayIndex() > End.getDisplayIndex())
        {
            newindex--;
            if(newindex < End.getDisplayIndex()) return false;
            CurrentIndex = newindex;
            return true;
        }
        else return false;
    }
    
    @Override
    public boolean Contains(InventoryCoords v)
    {        
        int big;
        int mid = v.getDisplayIndex();
        int lil;

        if(Start.getDisplayIndex() < End.getDisplayIndex()){ lil = Start.getDisplayIndex(); big = End.getDisplayIndex(); }
        else{ lil = End.getDisplayIndex(); big = Start.getDisplayIndex(); }
        return (lil <= mid && mid <= big);
    }
}
