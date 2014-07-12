package com.jakeconley.provo.utils.inventory;

public class InventoryRange
{
    public enum Type{ SINGULAR, LINEAR, RECTANGULAR }
    
    private Type Type;
    private InventoryCoords Start = null;
    private InventoryCoords End = null;
    
    public Type getType(){ return this.Type; }
    public InventoryCoords getStart(){ return this.Start; }
    public InventoryCoords getEnd(){ return this.End; }
    
    public InventoryRange(InventoryCoords _Start, InventoryCoords _End)
    {
        this.Start = _Start;
        this.End = _End;
    }
    
    public boolean Contains(InventoryCoords v, Type type)
    {
        switch(type)
        {
            case SINGULAR: return v.equals(Start) || v.equals(End);
            case LINEAR: return (v.getIndex() >= Start.getIndex() && v.getIndex() <= End.getIndex());
            case RECTANGULAR:
                if(v.getRowNumber() < Start.getRowNumber() || v.getRowNumber() > End.getRowNumber()) return false;
                return (v.getColumnNumber() >= Start.getColumnNumber() && v.getColumnNumber() <= End.getColumnNumber());
            default: return false;// Why do i have to have a default here.  i hate java
        }
    }
    public boolean Contains(InventoryCoords v){ return Contains(v, this.Type); }
}
