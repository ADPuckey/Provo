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
    
    public InventoryRange(InventoryCoords _Start, InventoryCoords _End, Type _Type)
    {
        // Gotta make sure this is correct on the lowest level
        // Is this good practice or no?
        if(_Start.getActualIndex() < _End.getActualIndex())
        {
            this.Start = _Start;
            this.End = _End;
        }
        else
        {
            this.Start = _End;
            this.End = _Start;
        }
        this.Type = _Type;
    }
    
    public boolean Contains(InventoryCoords v, Type type)
    {
        switch(type)
        {
            case SINGULAR: return v.equals(Start) || v.equals(End);
            case LINEAR: return (v.getActualIndex() >= Start.getActualIndex() && v.getActualIndex() <= End.getActualIndex());
            case RECTANGULAR:
                if(v.getRowNumber() < Start.getRowNumber() || v.getRowNumber() > End.getRowNumber()) return false;
                return (v.getColumnNumber() >= Start.getColumnNumber() && v.getColumnNumber() <= End.getColumnNumber());
            default: return false;// Why do i have to have a default here.  i hate java
        }
    }
    public boolean Contains(InventoryCoords v){ return Contains(v, this.Type); }
}
