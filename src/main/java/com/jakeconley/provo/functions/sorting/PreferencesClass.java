package com.jakeconley.provo.functions.sorting;

import com.jakeconley.provo.utils.inventory.InventoryType;
import java.util.LinkedList;
import java.util.List;

public class PreferencesClass
{
    private String Name;
    private final InventoryType TargetType;
    private final List<PreferencesRule> Rules;
    private String Inheritee;
    
    public String getName(){ return Name; }
    public InventoryType getTargetType(){ return TargetType; }
    public List<PreferencesRule> getRules(){ return Rules; }
    public String getInheritee(){ return Inheritee; }
    public void setName(String value){ Name = value; }
    public void setInheritee(String value){ Inheritee = value; }
    public boolean addRule(PreferencesRule value){ return Rules.add(value); }
    public boolean removeRule(PreferencesRule value){ return Rules.remove(value); }
    
    public PreferencesClass(String _Name, InventoryType _TargetType, List<PreferencesRule> _Rules)
    {
        Name = _Name;
        TargetType = _TargetType;
        if(_Rules == null) Rules = new LinkedList<>();
        else Rules = _Rules;
    }
}
