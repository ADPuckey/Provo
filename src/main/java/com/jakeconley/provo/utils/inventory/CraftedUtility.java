package com.jakeconley.provo.utils.inventory;

public class CraftedUtility
{
    public enum Item
    {
        ARMOR_HELMET, ARMOR_CHESTPLATE, ARMOR_LEGGINGS, ARMOR_BOOTS,
        TOOL_SWORD, TOOL_SPADE, TOOL_PICKAXE, TOOL_AXE, TOOL_SHOVEL;
    }
    public enum ItemMaterial
    {
        WOOD(0), LEATHER(0), STONE(1), CHAIN(1), IRON(2), GOLD(3), DIAMOND(4);
        
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
            case WOOD_SWORD: return new CraftedUtility(Item.TOOL_SWORD, ItemMaterial.WOOD);
            case WOOD_SPADE: return new CraftedUtility(Item.TOOL_SPADE, ItemMaterial.WOOD);
            default: return null;
        }
    }
}
