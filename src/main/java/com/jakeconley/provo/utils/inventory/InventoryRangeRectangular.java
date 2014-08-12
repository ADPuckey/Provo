package com.jakeconley.provo.utils.inventory;

import com.jakeconley.provo.utils.RelativeDirection;

public class InventoryRangeRectangular extends InventoryRange
{
    private final boolean StartIsLeft;
    private final boolean StartIsTop;
    
    public InventoryRangeRectangular(InventoryCoords start, InventoryCoords end)
    {
        super(start, end);
        this.Type = Type.RECTANGULAR;
        
        if(start.getRowNumber() < end.getRowNumber()) StartIsTop = true;
        else StartIsTop = false;
        if(start.getColumnNumber() < end.getColumnNumber()) StartIsLeft = true;
        else StartIsLeft = false;
    }
    
    private InventoryCoords CurrentCoords = null;
    @Override public int getCurrentIndex(){ return CurrentCoords.getGameIndex(); }
    @Override public void resetCurrentIndex(){ CurrentCoords = null; }
    
    @Override
    public boolean NextIndex()
    {
        if(CurrentCoords == null)
        {
            CurrentCoords = this.Start;
            return true;
        }
        
        InventoryCoords newcoords;
        
        newcoords = CurrentCoords.GetRelative((StartIsLeft ? RelativeDirection.RIGHT : RelativeDirection.LEFT), 1, InventoryType.MAX);
        if(ContainsHorizontal(newcoords)) 
        {
            CurrentCoords = newcoords;
            return true;
        }
        
        if(StartIsTop) newcoords = Start.GetRelative(RelativeDirection.DOWN, (CurrentCoords.getRowNumber() - Start.getRowNumber() + 1), InventoryType.MAX);
        else newcoords = Start.GetRelative(RelativeDirection.UP, (Start.getRowNumber() - CurrentCoords.getRowNumber() + 1), InventoryType.MAX);
        
        if(ContainsVertical(newcoords))
        {
            CurrentCoords = newcoords;
            return true;
        }
        
        return false;
    }
    
    private boolean ContainsVertical(InventoryCoords v)
    {
        if(StartIsTop) return (Start.getRowNumber() <= v.getRowNumber() && v.getRowNumber() <= End.getRowNumber());
        else return (End.getRowNumber() <= v.getRowNumber() && v.getRowNumber() <= Start.getRowNumber());
    }
    private boolean ContainsHorizontal(InventoryCoords v)
    {
        if(StartIsLeft) return (Start.getColumnNumber() <= v.getColumnNumber() && v.getColumnNumber() <= End.getColumnNumber());
        else return (End.getColumnNumber() <= v.getColumnNumber() && v.getColumnNumber() <= Start.getColumnNumber());
    }
    
    @Override
    public boolean Contains(InventoryCoords v)
    {
        if(!ContainsVertical(v)) return false;
        if(!ContainsHorizontal(v)) return false;

        return true;
    }
}
