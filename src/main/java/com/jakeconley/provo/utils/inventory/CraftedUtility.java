package com.jakeconley.provo.utils.inventory;

public class CraftedUtility
{
    public enum Item
    {
        TOOL_SWORD, TOOL_SPADE, TOOL_PICKAXE, TOOL_AXE, TOOL_HOE,
        ARMOR_HELMET, ARMOR_CHESTPLATE, ARMOR_LEGGINGS, ARMOR_BOOTS;
    }
    public enum ItemMaterial
    {
        WOOD(0), LEATHER(0), STONE(1), CHAINMAIL(1), IRON(2), GOLD(3), DIAMOND(4);
        
        public int Index;
        ItemMaterial(int _Index){ this.Index = _Index; }
    }
    
    public Item Type;
    public ItemMaterial Quality;
    public Item getItem(){ return this.Type; }
    public ItemMaterial getQuality(){ return this.Quality; }
    
    public CraftedUtility(Item _Item, ItemMaterial _Quality)
    {
        this.Type = _Item;
        this.Quality = _Quality;
    }
    public static CraftedUtility fromMaterial(org.bukkit.Material m)
    {
        switch(m)
        {
case WOOD_SWORD:	return new CraftedUtility(Item.TOOL_SWORD, ItemMaterial.WOOD);
case WOOD_SPADE:	return new CraftedUtility(Item.TOOL_SPADE, ItemMaterial.WOOD);
case WOOD_PICKAXE:	return new CraftedUtility(Item.TOOL_PICKAXE, ItemMaterial.WOOD);
case WOOD_AXE:  	return new CraftedUtility(Item.TOOL_AXE, ItemMaterial.WOOD);
case WOOD_HOE:  	return new CraftedUtility(Item.TOOL_HOE, ItemMaterial.WOOD);
case STONE_SWORD:	return new CraftedUtility(Item.TOOL_SWORD, ItemMaterial.STONE);
case STONE_SPADE:	return new CraftedUtility(Item.TOOL_SPADE, ItemMaterial.STONE);
case STONE_PICKAXE:	return new CraftedUtility(Item.TOOL_PICKAXE, ItemMaterial.STONE);
case STONE_AXE: 	return new CraftedUtility(Item.TOOL_AXE, ItemMaterial.STONE);
case STONE_HOE:     	return new CraftedUtility(Item.TOOL_HOE, ItemMaterial.STONE);
case GOLD_SWORD:	return new CraftedUtility(Item.TOOL_SWORD, ItemMaterial.GOLD);
case GOLD_SPADE:	return new CraftedUtility(Item.TOOL_SPADE, ItemMaterial.GOLD);
case GOLD_PICKAXE:	return new CraftedUtility(Item.TOOL_PICKAXE, ItemMaterial.GOLD);
case GOLD_AXE:          return new CraftedUtility(Item.TOOL_AXE, ItemMaterial.GOLD);
case GOLD_HOE:          return new CraftedUtility(Item.TOOL_HOE, ItemMaterial.GOLD);
case DIAMOND_SWORD:	return new CraftedUtility(Item.TOOL_SWORD, ItemMaterial.DIAMOND);
case DIAMOND_SPADE:	return new CraftedUtility(Item.TOOL_SPADE, ItemMaterial.DIAMOND);
case DIAMOND_PICKAXE:	return new CraftedUtility(Item.TOOL_PICKAXE, ItemMaterial.DIAMOND);
case DIAMOND_AXE:	return new CraftedUtility(Item.TOOL_AXE, ItemMaterial.DIAMOND);
case DIAMOND_HOE:	return new CraftedUtility(Item.TOOL_HOE, ItemMaterial.DIAMOND);
case LEATHER_HELMET:	return new CraftedUtility(Item.ARMOR_HELMET, ItemMaterial.LEATHER);
case LEATHER_CHESTPLATE:return new CraftedUtility(Item.ARMOR_CHESTPLATE, ItemMaterial.LEATHER);
case LEATHER_LEGGINGS:	return new CraftedUtility(Item.ARMOR_LEGGINGS, ItemMaterial.LEATHER);
case LEATHER_BOOTS:	return new CraftedUtility(Item.ARMOR_BOOTS, ItemMaterial.LEATHER);
case CHAINMAIL_HELMET:	return new CraftedUtility(Item.ARMOR_HELMET, ItemMaterial.CHAINMAIL);
case CHAINMAIL_CHESTPLATE:return new CraftedUtility(Item.ARMOR_CHESTPLATE, ItemMaterial.CHAINMAIL);// toOO LONG, PISS
case CHAINMAIL_LEGGINGS:	return new CraftedUtility(Item.ARMOR_LEGGINGS, ItemMaterial.CHAINMAIL);
case CHAINMAIL_BOOTS:	return new CraftedUtility(Item.ARMOR_BOOTS, ItemMaterial.CHAINMAIL);
case IRON_HELMET:	return new CraftedUtility(Item.ARMOR_HELMET, ItemMaterial.IRON);
case IRON_CHESTPLATE:	return new CraftedUtility(Item.ARMOR_CHESTPLATE, ItemMaterial.IRON);
case IRON_LEGGINGS:	return new CraftedUtility(Item.ARMOR_LEGGINGS, ItemMaterial.IRON);
case IRON_BOOTS:    	return new CraftedUtility(Item.ARMOR_BOOTS, ItemMaterial.IRON);
case GOLD_HELMET:	return new CraftedUtility(Item.ARMOR_HELMET, ItemMaterial.GOLD);
case GOLD_CHESTPLATE:	return new CraftedUtility(Item.ARMOR_CHESTPLATE, ItemMaterial.GOLD);
case GOLD_LEGGINGS:	return new CraftedUtility(Item.ARMOR_LEGGINGS, ItemMaterial.GOLD);
case GOLD_BOOTS:        	return new CraftedUtility(Item.ARMOR_BOOTS, ItemMaterial.GOLD);
case DIAMOND_HELMET:	return new CraftedUtility(Item.ARMOR_HELMET, ItemMaterial.DIAMOND);
case DIAMOND_CHESTPLATE:return new CraftedUtility(Item.ARMOR_CHESTPLATE, ItemMaterial.DIAMOND);
case DIAMOND_LEGGINGS:	return new CraftedUtility(Item.ARMOR_LEGGINGS, ItemMaterial.DIAMOND);
case DIAMOND_BOOTS:	return new CraftedUtility(Item.ARMOR_BOOTS, ItemMaterial.DIAMOND);
            default: return null;
        }
    }
    
    /**
     * Finds the highest-quality CraftedUtility in the supplied list
     * @param list List/array to search through
     * @return Highest-quality CraftedUtility found
     */
    public static CraftedUtility FindBest(CraftedUtility... list)
    {
        if(list.length == 0) return null;
        CraftedUtility res = list[0];
        for(CraftedUtility u : list)
        {
           if(u.Quality == ItemMaterial.DIAMOND) return u;// Diamond is highest quality, therefore it can go ahead and be returned
           if(u.Quality.Index > res.Quality.Index) res = u;
        }
        return res;
    }
}
