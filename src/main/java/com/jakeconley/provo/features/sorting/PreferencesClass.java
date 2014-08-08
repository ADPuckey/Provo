package com.jakeconley.provo.features.sorting;

import com.jakeconley.provo.utils.inventory.InventoryType;
import java.util.LinkedList;
import java.util.List;

public class PreferencesClass
{
    private String Name;
    private final InventoryType TargetType;
    private final List<PreferencesRule> Rules;
    private String Inheritance = null;
    
    public String getName(){ return Name; }
    public InventoryType getTargetType(){ return TargetType; }
    public List<PreferencesRule> getRules(){ return Rules; }
    public String getInheritance(){ return Inheritance; }
    public void setName(String value){ Name = value; }
    public void setInheritance(String value){ Inheritance = value; }
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
