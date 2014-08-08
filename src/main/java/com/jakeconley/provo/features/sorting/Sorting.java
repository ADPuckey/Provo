package com.jakeconley.provo.features.sorting;

import com.jakeconley.provo.Provo;
import com.jakeconley.provo.backend.ProvoFormatException;
import com.jakeconley.provo.backend.SortingPreferencesBackend;
import com.jakeconley.provo.bukkit.Messages;
import com.jakeconley.provo.utils.Comparators;
import com.jakeconley.provo.utils.Utils;
import com.jakeconley.provo.utils.inventory.CraftedUtility;
import com.jakeconley.provo.utils.inventory.InventoryCoords;
import com.jakeconley.provo.utils.inventory.InventoryRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Sorting
{    
    /**
     * Returns a map of item types to indexes of the strongest_i of that setrule_type in the list provided.
     * @param list List of items to sort through
     * @return Map of CraftedUtility.Item types to the indexes of the strongest_i types in the list.
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
	    if(craft == null) continue;//Is not a tool or ArmorContents
	    parsed.put(i, craft);
	    
	    if(ret.get(craft.getItem()) == null){ ret.put(craft.getItem(), i); continue; }// If none there, as default
	    if(craft.getQuality().getIndex() > parsed.get(ret.get(craft.getItem())).getQuality().getIndex()) ret.put(craft.getItem(), i);
	}
	return ret;
    }
    
    public static List<ItemStack> CollapseInventory(ItemStack[] Contents, ItemStack[] ArmorContents, HashMap<Integer, ItemStack> LockedIndices, boolean diagnostic)
    {
        // This is collapsing and autostacking code.
        // What happens here is that types are stored in a HashMap of ItemStacks to the amount that there should be.
        // To account for item damage and the like, a "ref" stack is created with a quantity of 1.
        // That way, objects with the same material and damage and enchantments should hash the same, disregarding amount.
        Utils.Debug("Collapsing...");   
        HashMap<ItemStack, Integer> count = new HashMap<>();
	for(int i = 0; i < Contents.length; i++)
        {
            ItemStack stack = Contents[i];
            if(stack == null || stack.getType() == Material.AIR) continue;
            if(LockedIndices != null && LockedIndices.get(i) != null) continue;
            
            // Since java is too good to distinguish between reference and value i spent like a day debugging this shit
            // So make sure this is cloned
            // Lots of java hate in this plugin in particular lol sry
            ItemStack ref = stack.clone();
            ref.setAmount(1);
            
            int qty = stack.getAmount();
            String debug_msg = "  " + stack.toString() + " " + stack.hashCode();
            if(count.get(ref) == null){ debug_msg += (":  Creating..." + qty); count.put(ref, qty); }
            else
            {
                int qtyfin = count.get(ref) + qty;
                debug_msg += (":  Putting with existing..." + qtyfin); 
                count.put(ref, qtyfin);
            }
            Utils.Debug(debug_msg);
        }
        
        // Take all the stuff in the maps and change them into stacks
	List<ItemStack> collapsed = new ArrayList<>();
        Utils.Debug("Compiling...");
        for(HashMap.Entry<ItemStack, Integer> entry : count.entrySet())
        {
            int max = entry.getKey().getType().getMaxStackSize();
            int total = entry.getValue();
            int remainder = total % max;
            int fullstackcount = (total - remainder) / max;
            //Utils.Debug("  " + entry.getKey().getType().toString() + ": " + fullstackcount + "*" + max + "i + " + remainder + " = " + total);
            
            for(int i = 1; i <= fullstackcount; i++)
            {
                ItemStack full = entry.getKey().clone();
                full.setAmount(max);
                collapsed.add(full);
                //Utils.Debug("    ADDING " + full.toString());
            }
            
            if(remainder > 0)
            {
                ItemStack partial = entry.getKey().clone();
                partial.setAmount(remainder);
                collapsed.add(partial);
                //Utils.Debug("    ADDING " + partial.toString());
            }
        }
	
	if(ArmorContents != null)
	{
	    for(ItemStack stack : ArmorContents){ if(stack != null && stack.getType() != Material.AIR){  collapsed.add(stack); } }
	}
        
        collapsed.sort(new Comparators.ITEMSTACK_ASCENDING());
        
        Utils.Debug("Result...");
        for(ItemStack i : collapsed){ Utils.Debug("  " + i.toString()); }
	
	return collapsed;
    }
    public static List<ItemStack> CollapseInventory(ItemStack[] Contents, ItemStack[] ArmorContents, HashMap<Integer, ItemStack> LockedIndices)
    { return CollapseInventory(Contents, ArmorContents, LockedIndices, false); }
    public static List<ItemStack> CollapseInventory(Inventory inventory, HashMap<Integer, ItemStack> lockedIndices, boolean diagnostic)
    {
        ItemStack[] ArmorContents = null;
        if(inventory instanceof PlayerInventory) ArmorContents = ((PlayerInventory) inventory).getArmorContents();
        return CollapseInventory(inventory.getContents(), ArmorContents, lockedIndices, diagnostic);
    }
    public static List<ItemStack> CollapseInventory(Inventory inventory, HashMap<Integer, ItemStack> lockedIndices)
    { return CollapseInventory(inventory, lockedIndices, false); }
    
    private static int FindNextIndex(HashMap<PreferencesRule, List<Integer>> claimedIndices, HashMap<Integer, ItemStack> lockedIndices, HashMap<Integer, List<Integer>> priorityIndices, PreferencesRule rule, ItemStack[] contents)
    {
        Set<Integer> reserved = new HashSet<>();
        for(HashMap.Entry<Integer, List<Integer>> entry : priorityIndices.entrySet())
        {
            if(entry.getKey() <= rule.getPriority()) continue;
            for(Integer in : entry.getValue()){ reserved.add(in); }
        }
        
        int ret;
        for(int i = 0; i < claimedIndices.get(rule).size(); i++)
        {
            ret = claimedIndices.get(rule).get(i);
            if(lockedIndices.get(ret) != null) continue;
            if(contents[ret] != null) continue;            
            if(reserved.contains(ret)){ continue; }
            
            claimedIndices.get(rule).remove(i);//holy fucking java lists.  keep in mind this removes index 0 not value 0 hopefully thanks to hacky autoboxing
            return ret;
        }
        return -1;
    }
    
    // TODO: Deambiguify 
    /*
        I know this source seems messy to read.  Check out the trello card if you're having trouble deciphering this.
        Also watch the various index properties, they can be ambiguous, i need to refactor those...
    */
    /**
     * Sort an inventory
     * @param inventory The inventory to sort
     * @param pclass The preferences class to apply
     * @param itemgroups The ItemGroups which the preferences class may reference
     * @return A SortingResult object containing sorted ItemStack[]s.
     */
    public static SortingResult SortInventory(Inventory inventory, PreferencesClass pclass, HashMap<String, LinkedList<Material>> itemgroups)
    {
	
	// Indexing for the various pref rules
        // These indices are all for the INVENTORY arrays, not for `queue`
        HashMap<Integer, ItemStack> lockedIndices = new HashMap<>();
	HashMap<PreferencesRule, List<Integer>> claimedIndices = new HashMap<>();
        HashMap<Integer, List<Integer>> priorityIndices = new HashMap<>();
	List<Integer> unclaimedIndices = new LinkedList<>();
	
	// Initial lists, sort and collapse
	// `queue` (initialized later) acts as a queue of items to be sorting.  its entry is set to null whenever the item is assigned a spot.
	List<PreferencesRule> rules = new LinkedList<>();//fucking java why do i have to initialize this
        if(pclass != null)
        {
            rules= pclass.getRules();
            rules.sort(new Comparators.PREFERENCESRULE_DESCENDING());
            for(PreferencesRule rule : rules)
            {
                //initialize
                claimedIndices.put(rule, new LinkedList<Integer>());
                if(priorityIndices.get(rule.getPriority()) == null) priorityIndices.put(rule.getPriority(), new LinkedList<Integer>());
            }
        }
        
        Utils.Debug("INDEXING:");
	for(int i = 0; i < inventory.getSize(); i++)
	{
            if(pclass != null)
            {
                boolean claimed = false;
                InventoryCoords coords = InventoryCoords.FromIndex(i, pclass.getTargetType());
                if(coords == null)
                {
                    // Needed for applying chest rules to a doublechest inventory
                    unclaimedIndices.add(i);
                    continue;
                }
                
                for(PreferencesRule rule : rules)
                {
                    if(rule.getTargetArea().Contains(coords))
                    {
                        if(rule.getItem().equalsIgnoreCase("locked")) lockedIndices.put(i, inventory.getItem(i));
                        else
                        {
                            claimedIndices.get(rule).add(i);
                            priorityIndices.get(rule.getPriority()).add(i);
                        }
                        claimed = true;
                    }
                }	    
                if(!claimed)
                {
                    unclaimedIndices.add(i);
                }
            }
            else unclaimedIndices.add(i);
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
            Utils.Debug("Priorities:");
            for(HashMap.Entry<Integer, List<Integer>> entry : priorityIndices.entrySet())
            {
                String set = Integer.toString(entry.getKey()) + ": ";
                for(Integer i : entry.getValue()) set += Integer.toString(i) + " ";
                Utils.Debug("  " + set);
            }
        }
                
        SortingResult ret = new SortingResult();
        Utils.Debug("QUEUEING:");
        List<ItemStack> queue = CollapseInventory(inventory, lockedIndices);
        //Utils.Debug("TEST:"); CollapseInventory(inventory, null);
	HashMap<CraftedUtility.Item, Integer> strongestset = FindStrongestSet(queue);
        if(strongestset == null) Utils.Debug("HELP");
	
	//inventory.clear();
	ItemStack[] InventoryContents = new ItemStack[inventory.getSize()];
        ItemStack[] ArmorContents = null;
	
        // Armor StrongestSet
	if(inventory instanceof PlayerInventory)
	{
            ArmorContents = new ItemStack[4];
	    int i_helmet    = strongestset.getOrDefault(CraftedUtility.Item.ARMOR_HELMET,	-1);
	    int i_chestplate= strongestset.getOrDefault(CraftedUtility.Item.ARMOR_CHESTPLATE,	-1);
	    int i_leggings  = strongestset.getOrDefault(CraftedUtility.Item.ARMOR_LEGGINGS,	-1);
	    int i_boots	    = strongestset.getOrDefault(CraftedUtility.Item.ARMOR_BOOTS,	-1);
	    
	    if(i_helmet != -1)
	    {
		ArmorContents[3] = queue.get(i_helmet);
		queue.set(i_helmet, null);
	    }
	    if(i_chestplate != -1)
	    {
		ArmorContents[2] = queue.get(i_chestplate);
		queue.set(i_chestplate, null);
	    }
	    if(i_leggings != -1)
	    {
		ArmorContents[1] = queue.get(i_leggings);
		queue.set(i_leggings, null);
	    }
	    if(i_boots != -1)
	    {
		ArmorContents[0] = queue.get(i_boots);
		queue.set(i_boots, null);
	    }
	}
	
	// Actual sorting code below.
	// Take note that a null check is required for each iteration of a loop
	// as the `queue` bit is set to null whenever the item is given a home
        Utils.Debug("SORTING:");
        for(PreferencesRule rule : rules)
        {
            Utils.Debug("Rule " + rule.toString());
            if(claimedIndices.get(rule).isEmpty()) continue;//no more fillable spaces
            if(rule.getItem().equalsIgnoreCase("locked")) continue;//locked rules will be accounted for later
            
            CraftedUtility.Item setrule_type = CraftedUtility.Item.getToolType(rule.getItem());
            if(setrule_type != null)
            {
                // Get StrongestSet stuff
                
                if(rule.getTargetArea().getType() == InventoryRange.Type.SINGULAR)
                {
                    Utils.Debug("  Set-rule as singular");
                    
                    int strongest_i = strongestset.getOrDefault(setrule_type, -1);                    
                    if(strongest_i == -1) continue;
                    
                    if(claimedIndices.get(rule).isEmpty()) continue;//no more fillable spaces, CONTINUE cause this isn't an inner loop
                    int newindex = FindNextIndex(claimedIndices, lockedIndices, priorityIndices, rule, InventoryContents);
                    if(newindex < 0) continue;//failure, CONTINUE cause this isn't an inner loop
                    
                    ItemStack strongest_stack = queue.get(strongest_i);
                    if(strongest_stack == null){ Utils.Debug("  Already assigned"); continue; }
                    
                    InventoryContents[newindex] = strongest_stack;
                    queue.set(strongest_i, null);
                    Utils.Debug("  Assigning " + strongest_stack.toString() + " to index " + newindex);
                }
                else
                {
                    Utils.Debug("  Set-rule as plural");
                    for(int i = 0; i < queue.size(); i++)
                    {
                        ItemStack stack = queue.get(i);
                        if(stack == null) continue;
                        CraftedUtility utility = CraftedUtility.fromMaterial(stack.getType());
                        if(utility == null) continue;
                        if(utility.getItem() != setrule_type) continue;
                        
                        if(claimedIndices.get(rule).isEmpty()) break;//no more fillable spaces, BREAK cause this is an inner loop
                        int newindex = FindNextIndex(claimedIndices, lockedIndices, priorityIndices, rule, InventoryContents);
                        if(newindex < 0) break;//failure, BREAK cause this is an inner loop

                        InventoryContents[newindex] = stack;
                        queue.set(i, null);
                        Utils.Debug("  Assigning " + stack.toString() + " to index " + newindex);
                    }
                }
                
                //if code reaches this point, it will continue loop
            }
            else
            {
                for(int i = 0; i < queue.size(); i++)
                {                
                    ItemStack stack = queue.get(i);
                    if(stack == null) continue;
                    if(!rule.MatchesMaterial(stack.getType(), itemgroups)) continue;

                    if(claimedIndices.get(rule).isEmpty()) break;//no more fillable spaces, BREAK cause this is an inner loop
                        int newindex = FindNextIndex(claimedIndices, lockedIndices, priorityIndices, rule, InventoryContents);
                        if(newindex < 0) break;//failure, BREAK cause this is an inner loop

                    InventoryContents[newindex] = stack;
                    Utils.Debug("  Assigning " + stack.toString() + " to index " + newindex);

                    queue.set(i, null);
                }
            }
        }
	// After all matchable items are matched, if there are any stragglers...
        Utils.Debug("Ended rule matching, looking for other spaces...");
	for(int i = 0; i < queue.size(); i++)
	{
	    ItemStack stack = queue.get(i);
	    if(stack == null) continue;
	    
	    if(!unclaimedIndices.isEmpty())
	    {
                int newindex = unclaimedIndices.get(0);
                if(InventoryContents[newindex] != null) continue;
		InventoryContents[newindex] = stack;
		unclaimedIndices.remove(0);
                Utils.Debug("  Assigning " + stack.toString() + " to unclaimed index " + newindex);
		queue.set(i, null);
	    }
	    else
	    {
                // Through the INVENTORY, not `queue`
		for(int j = 0; j < InventoryContents.length; j++)
		{
                    if(lockedIndices.get(j) != null) continue;
		    if(InventoryContents[j] == null)
		    {
                        Utils.Debug("  Assigning " + stack.toString() + " to claimed index " + j + " " + (InventoryContents[j] != null ? InventoryContents[j].toString() : "null"));
			InventoryContents[j] = stack;
			queue.set(j, null);
			break;//next ItemStack
		    }
		}
	    }
	}
        
        //re-add locked indices
        for(HashMap.Entry<Integer, ItemStack> entry : lockedIndices.entrySet())
        {
            InventoryContents[entry.getKey()] = entry.getValue();
        }        
	
	// Slick, finally done smh
        ret.Contents = InventoryContents;
        if(inventory instanceof PlayerInventory) { ret.ArmorContents = ArmorContents; }
        return ret;
    }
    
    /**
     * Do the sort including frontend messages and verification.
     * @param player The player to send the messages to and get item groups from
     * @param inventory The inventory to sort
     * @param pclass The preferencesclass to sort it by
     * @param backend The backend to use in retrieval of stuff
     */
    public static void FrontendExecute(Player player, Inventory inventory, PreferencesClass pclass, SortingPreferencesBackend backend)
    {
        try
        {
            PlayerInventory playerinv = null;
            if(inventory instanceof PlayerInventory) playerinv = (PlayerInventory) inventory;
            
            ItemStack[] invContents = inventory.getContents();
            ItemStack[] armorContents = (playerinv != null ? playerinv.getArmorContents() : null);
            
            HashMap<String, LinkedList<Material>> igroups = backend.FetchItemGroups(player.getUniqueId().toString());
            Utils.Debug("PRESORT:"); List<ItemStack> PreSort = Sorting.CollapseInventory(invContents, armorContents, null);
            SortingResult res = Sorting.SortInventory(inventory, pclass, igroups);
            Utils.Debug("POSTSORT:"); List<ItemStack> PostSort = Sorting.CollapseInventory(res.Contents, res.ArmorContents, null);

            if(PreSort.equals(PostSort))
            {
                Utils.Debug("final contents");
                for(int i = 0; i < res.Contents.length; i++){ Utils.Debug("  " + i + ": " + (res.Contents[i] != null ? res.Contents[i].toString() : "null")); }
                inventory.clear();
                inventory.setContents(res.Contents);
                if(playerinv != null) playerinv.setArmorContents(res.ArmorContents);
                player.sendMessage(ChatColor.GREEN + "Successfully sorted inventory.");
            }
            else
            {
                player.sendMessage(ChatColor.RED + "Sort failed!");
            }
        }
        catch(ProvoFormatException e){ Messages.ReportProvoFormatException(player, e); return; }
        catch(Exception e){ Messages.ReportException(player, e); return; }
    }
}