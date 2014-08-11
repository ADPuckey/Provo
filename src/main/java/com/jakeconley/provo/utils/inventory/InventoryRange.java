package com.jakeconley.provo.utils.inventory;

public class InventoryRange
{
    public static enum Type{ SINGULAR, WRAPAROUND, RECTANGULAR }
    
    private Type Type;
    private InventoryCoords Start = null;
    private InventoryCoords End = null;
    
    public Type getType(){ return this.Type; }
    public InventoryCoords getStart(){ return this.Start; }
    public InventoryCoords getEnd(){ return this.End; }
    
    public InventoryRange(InventoryCoords _Start, InventoryCoords _End, Type _Type)
    {
        this.Start = _Start;
        this.End = _End;
        this.Type = _Type;
    }
    
    // Not sure why i did separate methods but oh well
    private boolean Contains(InventoryCoords v, Type type)
    {
        switch(type)
        {
            case SINGULAR: return v.equals(Start) || v.equals(End);
            case WRAPAROUND:
                int big;
                int mid = v.getActualIndex();
                int lil;
                
                if(Start.getActualIndex() < End.getActualIndex()){ lil = Start.getActualIndex(); big = End.getActualIndex(); }
                else{ lil = End.getActualIndex(); big = Start.getActualIndex(); }
                return (lil <= mid && mid <= big);
            case RECTANGULAR:
                // Test Columns
                int bigrow;
                int midrow = v.getRowNumber();
                int lilrow;
                if(Start.getRowNumber() < End.getRowNumber()){ lilrow = Start.getRowNumber(); bigrow = End.getRowNumber(); }
                else{ lilrow = End.getRowNumber(); bigrow = Start.getRowNumber(); }
                if(!(lilrow <= midrow && midrow <= bigrow)) return false;
                
                int bigcol;
                int midcol = v.getColumnNumber();
                int lilcol;
                if(Start.getColumnNumber() < End.getColumnNumber()){ lilcol = Start.getColumnNumber(); bigcol = End.getColumnNumber(); }
                else{ lilcol = End.getColumnNumber(); bigcol = Start.getColumnNumber(); }
                if(!(lilcol <= midcol && midcol <= bigcol)) return false;
                
                return true;
            default: return false;// Why do i have to have a default here.  i hate java
        }
    }
    public boolean Contains(InventoryCoords v){ return Contains(v, this.Type); }
    
    @Override public String toString()
    {
        if(Type == Type.SINGULAR) return Start.toString() + " " + Type.toString();
        else return Start.toString() + "-" + End.toString() + " " + Type.toString();
    }
}
