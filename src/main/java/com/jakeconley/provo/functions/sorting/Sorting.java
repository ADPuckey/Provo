package com.jakeconley.provo.functions.sorting;

import com.jakeconley.provo.utils.Comparators;
import com.jakeconley.provo.utils.inventory.CraftedUtility;
import com.jakeconley.provo.utils.inventory.InventoryCoords;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
	    parsed.put(Integer.SIZE, craft);
	    
	    if(ret.get(craft.getItem()) == null){ ret.put(craft.getItem(), i); continue; }// If none there, as default
	    if(craft.getQuality().Index > parsed.get(i).getQuality().Index) ret.put(craft.getItem(), i);
	}
	return ret;
    }
    private static List<ItemStack> CollapseInventory(Inventory inventory)
    {	
	List<ItemStack> collapsed = new ArrayList<>();
	for(ItemStack stack : inventory.getContents()){ if(stack != null) collapsed.add(stack); }
	
	if(inventory instanceof PlayerInventory)
	{
	    PlayerInventory playerinv = (PlayerInventory) inventory;
	    for(ItemStack stack : playerinv.getArmorContents()){ if(stack != null) collapsed.add(stack); }
	}
	
	return collapsed;
    }
    
    public static void SortInventory(Inventory inventory, PreferencesClass pclass)
    {
	// Initial lists, sort and collapse
	// `collapsed` acts as a queue of items to be sorting.  its entry is set to null whenever the item is assigned a spot.
	List<PreferencesRule> rules = pclass.getRules();
	List<ItemStack> collapsed = CollapseInventory(inventory);
	rules.sort(new Comparators.PREFERENCESRULE_DESCENDING());
	collapsed.sort(new Comparators.ITEMSTACK_ASCENDING());
	HashMap<CraftedUtility.Item, Integer> strongestset = FindStrongestSet(collapsed);
	
	// Indexing for the various pref rules
	HashMap<PreferencesRule, List<Integer>> claimedIndices = new HashMap<>();
	List<Integer> unclaimedIndices = new LinkedList<>();
	
	for(PreferencesRule rule : rules){ claimedIndices.put(rule, new LinkedList<Integer>()); }//initialize
	for(int i = 0; i < inventory.getSize(); i++)
	{
	    boolean claimed = false;
	    InventoryCoords coords = InventoryCoords.FromIndex(i, pclass.getTargetType());
	    for(PreferencesRule rule : rules){ if(rule.getTargetArea().Contains(coords)) {
		claimedIndices.get(rule).add(i);
		claimed = true;
	    } }
	    
	    if(!claimed) unclaimedIndices.add(i);
	}
	
	inventory.clear();
	ItemStack[] result = new ItemStack[inventory.getSize()];
	
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
	}
	
	// Actual sorting code below.
	// Take note that a null check is required for each iteration of a loop
	// as the `collapsed` bit is set to null whenever the item is given a home
	
	for(int i = 0; i < collapsed.size(); i++)
	{
	    ItemStack stack = collapsed.get(i);
	    if(stack == null) continue;
	    
	    for(PreferencesRule rule : rules){ if(rule.MatchesMaterial(stack.getType())){
		if(claimedIndices.get(rule).isEmpty()) break;//no more fillable spaces
		
		int newindex = claimedIndices.get(rule).get(0);
		result[newindex] = stack;
		claimedIndices.get(rule).remove(0);//holy fucking java lists.  keep in mind this removes index 0 not value 0 hopefully thanks to hacky autoboxing
		
		collapsed.set(i, null);
		break;
	    } }
	}
	// After all matchable items are matched, if there are any stragglers...
	for(int i = 0; i < collapsed.size(); i++)
	{
	    ItemStack stack = collapsed.get(i);
	    if(stack == null) continue;
	    
	    if(!unclaimedIndices.isEmpty())
	    {
		result[unclaimedIndices.get(0)] = stack;
		unclaimedIndices.remove(0);
		collapsed.set(i, null);
	    }
	    else
	    {
		for(int j = 0; i < result.length; i++)
		{
		    if(result[j] != null)
		    {
			result[j] = stack;
			collapsed.set(j, null);
			break;//next ItemStack
		    }
		}
	    }
	}
	
	// Slick, finally done smh
	inventory.setContents(result);
    }
}