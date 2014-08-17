package com.jakeconley.provo.utils.ghostblock;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GhostBlock
{
    private final List<Player> Players = new LinkedList<>();
    public List<Player> getPlayers(){ return Players; }
    
    private final Block BlockInstance;
    public Block getBlock(){ return BlockInstance; }
    
    private final Material OriginalMaterial;
    private final byte OriginalData;
    
    public GhostBlock(Block block)
    {
	BlockInstance = block;
	OriginalMaterial = block.getType();
	OriginalData = block.getData();
    }
    
    public GhostBlock Enable(Material material)
    {
	for(Player p : Players) p.sendBlockChange(BlockInstance.getLocation(), material, (byte) 0);
	return this;
    }
    public GhostBlock Disable()
    {
	for(Player p : Players) p.sendBlockChange(BlockInstance.getLocation(), OriginalMaterial, OriginalData);
	return this;
    }
}
