package com.jakeconley.provo.utils.inventory;

public abstract class InventoryRange
{
    public static enum Type{ SINGULAR, WRAPAROUND, RECTANGULAR }
    
    protected Type Type;
    protected final InventoryCoords Start;
    protected final InventoryCoords End;
    
    public Type getType(){ return this.Type; }
    public InventoryCoords getStart(){ return this.Start; }
    public InventoryCoords getEnd(){ return this.End; }
    
    protected InventoryRange(InventoryCoords start, InventoryCoords end)
    {
        Start = start;
        End = end;
    }
    
    public static InventoryRange ForType(InventoryCoords start, InventoryCoords end, Type type)
    {
        switch(type)
        {
            case SINGULAR: return new InventoryRangeSingular(start);
            case WRAPAROUND: return new InventoryRangeWraparound(start, end);
            case RECTANGULAR: return new InventoryRangeRectangular(start, end);
            default: return null;
        }
    }
    
    protected int CurrentIndex = -1;
    /**
     * Gets a GAME index of this range, as iterated by NextIndex().
     * @return GAME index
     * @see NextIndex()
     */
    public int getCurrentIndex(){ return CurrentIndex; }
    /**
     * Cycle to the next index defined by this range.
     * @return False if there is no next index, else true.
     */
    public boolean NextIndex()
    {
        if(CurrentIndex == -1)
        {
            this.CurrentIndex = this.Start.getGameIndex();
            return true;
        }
        return false;
    }
    /**
     * Reset the iteration state of the range.  When NextIndex() is called after this, it will return the first index.
     */
    public void resetCurrentIndex(){ CurrentIndex = -1; }
    
    public abstract boolean Contains(InventoryCoords v);
    
    @Override public String toString()
    {
        if(Type == Type.SINGULAR) return Start.toString() + " " + Type.toString();
        else return Start.toString() + "-" + End.toString() + " " + Type.toString();
    }
}
