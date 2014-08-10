package com.jakeconley.provo.utils;

import com.jakeconley.provo.features.sorting.PreferencesRule;
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
            ret = o1.getItemMeta().hashCode() - o2.getItemMeta().hashCode();//Last resort to differentiate between item meta shit, kinda hacky but it works ok
            if(ret != 0) return ret;
            ret = o2.getAmount() - o1.getAmount();//Amount should sthill be descending.
            if(ret != 0) return ret;
            return 0;
        }
    }
    public static class COORDINATEAXIS_ASCENDING implements Comparator<CoordinateAxis>
    {
        @Override public int compare(CoordinateAxis o1, CoordinateAxis o2){ return o1.compareTo(o2); }
    }
}
