package com.jakeconley.provo.functions.sorting;

import org.bukkit.Material;

public enum PreferencesRuleItemGroup
{
    /*
        Split into two main categories, blocks and items
        Each one has a _GENERAL instance which matches any of said category
        Beyond that, they're broken down into more specific types
    */
    // SPECIFIED - a particular set of rules
    /*
    BLOCKS_GENERAL, BLOCKS_BASIC, BLOCKS_COMPLEX, BLOCKS_STORAGE, BLOCKS_FLAMMABLE,
    ITEMS_GENERAL, ITEMS_CONSTRUCTION, ITEMS_CRAFTING, ITEMS_CRAFTED, ITEMS_MINED,
    ITEMS_TOOLS, ITEMS_EDIBLE, ITEMS_FOOD, ITEMS_FUEL, ITEMS_PLANTS,
    SPECIFIED;
    */
    BLOCKS_GENERAL, ITEMS_GENERAL, BLOCKS_FLAMMABLE, ITEMS_EDIBLE, SPECIFIED;
    
    public static PreferencesRuleItemGroup FromName(String s)
    {
        return null;
    }
    
    public boolean MatchesMaterial(Material m)
    {
        if(this == BLOCKS_GENERAL) return m.isBlock();
        if(this == ITEMS_GENERAL) return (!m.isBlock());
        if(this == BLOCKS_FLAMMABLE) return m.isFlammable();
        if(this == ITEMS_EDIBLE) return m.isEdible();
        
        return false;
    }
}
