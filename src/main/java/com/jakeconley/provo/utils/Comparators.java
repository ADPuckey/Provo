package com.jakeconley.provo.utils;

import com.jakeconley.provo.functions.sorting.PreferencesRule;
import java.util.Comparator;
import org.bukkit.inventory.ItemStack;

public class Comparators
{
    public static class PREFERENCESRULE_DESCENDING implements Comparator<PreferencesRule>
    {
	@Override
	public int compare(PreferencesRule o1, PreferencesRule o2){ return o2.getPriority() - o1.getPriority(); }
    }    
    public static class ITEMSTACK_ASCENDING implements Comparator<ItemStack>
    {
	@Override
	public int compare(ItemStack o1, ItemStack o2)
        {
            int ret;
            ret = o1.getType().compareTo(o2.getType());
            if(ret != 0) return ret;
            ret = o1.getDurability() - o2.getDurability();
            if(ret != 0) return ret;
            ret = o1.hashCode() - o2.hashCode();//kinda hacky, that can be assessed later
            if(ret != 0) return ret;
            return 0;
        }
    }
}
