package com.jakeconley.provo.functions.sorting;

import com.jakeconley.provo.utils.inventory.InventoryRange;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import org.bukkit.Material;

public class PreferencesRule
{
    private int Priority;
    private InventoryRange TargetArea;
    private String Type;//the material/group to match it to
    private boolean Inherited;
    private String InheritedFrom;
    
    public int getPriority(){ return this.Priority; }
    public InventoryRange getTargetArea(){ return this.TargetArea; }
    public String getType(){ return this.Type; }
    public boolean isInherited(){ return Inherited; }
    public String getInheritedFrom(){ return InheritedFrom; }
    public void setPriority(int value){ this.Priority = value; }
    public void setTargetArea(InventoryRange value){ this.TargetArea = value; }
    public void setItemGroupName(String value){ this.Type = value; }
    public void setInherited(boolean value){ this.Inherited = value; }
    public void setInheritedFrom(String value){ this.InheritedFrom = value; }
    
    public PreferencesRule(int _Priority, InventoryRange _TargetArea, String _ItemGroupName)
    {
        Priority = _Priority;
        TargetArea = _TargetArea;
        Type = _ItemGroupName;
    }
    
    public boolean MatchesMaterial(Material m, HashMap<String, LinkedList<Material>> itemgroups)
    {
        LinkedList<Material> group = itemgroups.get(Type);
        if(group != null){ if(group.contains(m)) return true; }
        
        if(Type.equals("any")) return true;
        if(Type.equals("blocks")) return m.isBlock();
        if(Type.equals("items")) return !(m.isBlock());
        if(Type.equals("edible")) return m.isEdible();
        if(Type.equals("flammable")) return m.isFlammable();
        if(Type.equals("burnable")) return m.isBurnable();
        
        return false;
    }
    
    @Override
    public String toString(){ return "Priority " + Priority + " " + TargetArea.toString() + ": " + Type; }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 31 * hash + this.Priority;
	hash = 31 * hash + Objects.hashCode(this.TargetArea);
	hash = 31 * hash + Objects.hashCode(this.Type);
	hash = 31 * hash + Objects.hashCode(this.InheritedFrom);
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
	final PreferencesRule other = (PreferencesRule) obj;
	if (this.Priority != other.Priority) {
	    return false;
	}
	if (!Objects.equals(this.TargetArea, other.TargetArea)) {
	    return false;
	}
	if (!Objects.equals(this.Type, other.Type)) {
	    return false;
	}
	if (!Objects.equals(this.InheritedFrom, other.InheritedFrom)) {
	    return false;
	}
	return true;
    }
    
    
}