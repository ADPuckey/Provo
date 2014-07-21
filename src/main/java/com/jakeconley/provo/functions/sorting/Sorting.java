package com.jakeconley.provo.functions.sorting;

import com.jakeconley.provo.Provo;
import com.jakeconley.provo.utils.Comparators;
import com.jakeconley.provo.utils.Utils;
import com.jakeconley.provo.utils.inventory.CraftedUtility;
import com.jakeconley.provo.utils.inventory.InventoryCoords;
import com.jakeconley.provo.utils.inventory.InventoryRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Sorting
{    
    /**
     * Returns a map of item types to indexes of the strongest of that type in the list provided.
     * @param list List of items to sort through
     * @return Map of CraftedUtility.Item types to the indexes of the strongest types in the list.
     */
    private static HashMap<CraftedUtility.Item, Integer> FindStrongestSet(List<ItemStack> list)
    {
	HashMap<CraftedUtility.Item, Integer> ret = new HashMap<>();// Returing value
	HashMap<Integer, CraftedUtility> parsed = new HashMap<>();// Map of already-parsed CraftedUtility values so we don't parse twice

	for(int i = 0; i < list.size(); i++)
	{
	    // Get the ItemStack at that index, parse it into a CraftedUtility if possible,
	    // continue if not possible, and put it into the map of `parsed` if possible
	    // Therefore any materials in the `ret` HashMap should have a matching CraftedUtility
	    // in the `parsed` map, which can be used to compare
	    ItemStack stack = list.get(i);
	    CraftedUtility craft = CraftedUtility.fromMaterial(stack.getType());
	    if(craft == null) continue;//Is not a tool or armor
	    parsed.put(i, craft);
	    
	    if(ret.get(craft.getItem()) == null){ ret.put(craft.getItem(), i); continue; }// If none there, as default
	    if(craft.getQuality().Index > parsed.get(ret.get(craft.getItem())).getQuality().Index) ret.put(craft.getItem(), i);
	}
	return ret;
    }
    private static List<ItemStack> CollapseInventory(Inventory inventory, HashMap<Integer, ItemStack> lockedIndices)
    {	
	List<ItemStack> collapsed = new ArrayList<>();
	for(int i = 0; i < inventory.getSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            if(stack != null && lockedIndices.get(i) == null) collapsed.add(stack);
        }
	
	if(inventory instanceof PlayerInventory)
	{
	    PlayerInventory playerinv = (PlayerInventory) inventory;
	    for(ItemStack stack : playerinv.getArmorContents()){ if(stack != null) collapsed.add(stack); }
	}
	
	return collapsed;
    }
    
    // TODO: Deambiguify 
    /*
        I know this source seems messy to read.  Check out the trello card if you're having trouble deciphering this.
        Also watch the various index properties, they can be ambiguous, i need to refactor those...
    */
    public static SortingResult SortInventory(Inventory inventory, PreferencesClass pclass, HashMap<String, LinkedList<Material>> itemgroups)
    {
	// Initial lists, sort and collapse
	// `collapsed` (initialized later) acts as a queue of items to be sorting.  its entry is set to null whenever the item is assigned a spot.
	List<PreferencesRule> rules = pclass.getRules();
	rules.sort(new Comparators.PREFERENCESRULE_DESCENDING());
	
	// Indexing for the various pref rules
        // These indices are all for the INVENTORY arrays, not for `collapsed`
        HashMap<Integer, ItemStack> lockedIndices = new HashMap<>();
	HashMap<PreferencesRule, List<Integer>> claimedIndices = new HashMap<>();
	List<Integer> unclaimedIndices = new LinkedList<>();
	
	for(PreferencesRule rule : rules){ claimedIndices.put(rule, new LinkedList<Integer>()); }//initialize
	for(int i = 0; i < inventory.getSize(); i++)
	{
	    boolean claimed = false;
	    InventoryCoords coords = InventoryCoords.FromIndex(i, pclass.getTargetType());
	    for(PreferencesRule rule : rules)
            {
                if(rule.getTargetArea().Contains(coords))
                {
                    if(rule.getType().equalsIgnoreCase("locked")) lockedIndices.put(i, inventory.getItem(i));
                    else claimedIndices.get(rule).add(i);
                    claimed = true;
                }
            }
	    
	    if(!claimed)
            {
                unclaimedIndices.add(i);
            }
	}
        
        if(Provo.Debug)
        {
            Utils.Debug("Claimed:");
            for(HashMap.Entry<PreferencesRule, List<Integer>> entry : claimedIndices.entrySet())
            {
                String vals = new String();
                for(Integer index : entry.getValue()){ vals += (Integer.toString(index) + " "); }
                Utils.Debug("  " + entry.getKey().toString() + ": " + vals);
            }
            Utils.Debug("Locked:");
            for(HashMap.Entry<Integer, ItemStack> entry : lockedIndices.entrySet()){ Utils.Debug("  " + entry.getKey() + " - " + (entry.getValue() != null ? entry.getValue().toString() : "null")); }
            Utils.Debug("Unclaimed:");
                String vals = new String();
                for(Integer i : unclaimedIndices){ vals += (Integer.toString(i) + " "); }
                Utils.Debug("  " + vals);
        }
        
        List<ItemStack> collapsed = CollapseInventory(inventory, lockedIndices);
	collapsed.sort(new Comparators.ITEMSTACK_ASCENDING());
	HashMap<CraftedUtility.Item, Integer> strongestset = FindStrongestSet(collapsed);
	
	inventory.clear();
        SortingResult ret = new SortingResult();
	ItemStack[] result = new ItemStack[inventory.getSize()];
        
        if(Provo.Debug)
        {
            Utils.Debug("Stacks:");
            for(ItemStack stack : collapsed){ Utils.Debug("  " + stack.toString()); }
        }
	
	if(inventory instanceof PlayerInventory)
	{
	    PlayerInventory playerinv = (PlayerInventory) inventory;
	    int i_helmet    = strongestset.getOrDefault(CraftedUtility.Item.ARMOR_HELMET,	-1);
	    int i_chestplate= strongestset.getOrDefault(CraftedUtility.Item.ARMOR_CHESTPLATE,	-1);
	    int i_leggings  = strongestset.getOrDefault(CraftedUtility.Item.ARMOR_LEGGINGS,	-1);
	    int i_boots	    = strongestset.getOrDefault(CraftedUtility.Item.ARMOR_BOOTS,	-1);
	    
	    if(i_helmet != -1)
	    {
		playerinv.setHelmet(collapsed.get(i_helmet));
		collapsed.set(i_helmet, null);
	    }
	    if(i_chestplate != -1)
	    {
		playerinv.setChestplate(collapsed.get(i_chestplate));
		collapsed.set(i_chestplate, null);
	    }
	    if(i_leggings != -1)
	    {
		playerinv.setLeggings(collapsed.get(i_leggings));
		collapsed.set(i_leggings, null);
	    }
	    if(i_boots != -1)
	    {
		playerinv.setBoots(collapsed.get(i_boots));
		collapsed.set(i_boots, null);
	    }
            
            ret.ArmorContents = playerinv.getArmorContents();
	}
	
	// Actual sorting code below.
	// Take note that a null check is required for each iteration of a loop
	// as the `collapsed` bit is set to null whenever the item is given a home
        for(PreferencesRule rule : rules)
        {
            Utils.Debug("Rule " + rule.toString());
            if(claimedIndices.get(rule).isEmpty()) continue;//no more fillable spaces
            if(rule.getType().equalsIgnoreCase("locked")) continue;//locked rules will be accounted for later
            
            try
            {
                // Get StrongestSet stuff
                CraftedUtility.Item type = CraftedUtility.Item.valueOf("TOOL_" + rule.getType().toUpperCase());// Will throw exception if not a set rule
                
                if(rule.getTargetArea().getType() == InventoryRange.Type.SINGULAR)
                {
                    Utils.Debug("set rule as singular");
                    
                    int strongest = strongestset.get(type);
                    int newindex = claimedIndices.get(rule).get(0);
                    if(lockedIndices.get(newindex) != null) continue;
                    result[newindex] = collapsed.get(strongest);
                    Utils.Debug("  Assigning " + collapsed.get(strongest).toString() + " to index " + newindex);
                    collapsed.set(strongest, null);
                    claimedIndices.get(rule).remove(0);//see annotation below about lists
                }
                else
                {
                    Utils.Debug("set rule as plural");
                    for(int i = 0; i < collapsed.size(); i++)
                    {
                        ItemStack stack = collapsed.get(i);
                        if(stack == null) continue;
                        CraftedUtility utility = CraftedUtility.fromMaterial(stack.getType());
                        if(utility == null) continue;
                        if(utility.getItem() != type) continue;

                        int newindex = claimedIndices.get(rule).get(0);
                        if(lockedIndices.get(newindex) != null) continue;

                        result[newindex] = stack;
                        collapsed.set(i, null);
                        claimedIndices.get(rule).remove(0);//see annotation below about lists
                        Utils.Debug("  Assigning " + stack.toString() + " to index " + newindex);
                    }
                }
                
                //if code reaches this point, it will continue loop
            }
            catch(IllegalArgumentException e)
            {//i know using exceptions as a control statement is a bad idea but java leaves me no choice soz
                for(int i = 0; i < collapsed.size(); i++)
                {                
                    ItemStack stack = collapsed.get(i);
                    if(stack == null) continue;
                    if(!rule.MatchesMaterial(stack.getType(), itemgroups)) continue;

                    int newindex = claimedIndices.get(rule).get(0);
                    if(lockedIndices.get(newindex) != null) continue;
                    result[newindex] = stack;
                    claimedIndices.get(rule).remove(0);//holy fucking java lists.  keep in mind this removes index 0 not value 0 hopefully thanks to hacky autoboxing
                    Utils.Debug("  Assigning " + stack.toString() + " to index " + newindex);

                    collapsed.set(i, null);
                }
            }
            catch(Exception e)
            {
                Utils.LogException("sorting inventory", e);
            }
        }
	// After all matchable items are matched, if there are any stragglers...
	for(int i = 0; i < collapsed.size(); i++)
	{
	    ItemStack stack = collapsed.get(i);
	    if(stack == null) continue;
            Utils.Debug("Looking for a space for " + stack.toString() + "...");
	    
	    if(!unclaimedIndices.isEmpty())
	    {
		result[unclaimedIndices.get(0)] = stack;
		unclaimedIndices.remove(0);
		collapsed.set(i, null);
	    }
	    else
	    {
                // Through the INVENTORY, not `collapsed`
		for(int j = 0; j < result.length; j++)
		{
                    if(lockedIndices.get(j) != null) continue;
		    if(result[j] != null)
		    {
			result[j] = stack;
			collapsed.set(j, null);
			break;//next ItemStack
		    }
		}
	    }
	}
        
        //re-add locked indices
        for(HashMap.Entry<Integer, ItemStack> entry : lockedIndices.entrySet())
        {
            result[entry.getKey()] = entry.getValue();
        }
	
	// Slick, finally done smh
        ret.Contents = result;
        return ret;
    }
}